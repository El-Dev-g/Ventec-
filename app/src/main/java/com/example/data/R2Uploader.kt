package com.example.data

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.BuildConfig
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object R2Uploader {
    private const val TAG = "R2Uploader"
    private val client = OkHttpClient()

    // Retrieve from BuildConfig
    private val ACCOUNT_ID: String get() = try { BuildConfig.CLOUDFLARE_R2_ACCOUNT_ID } catch (e: Exception) { "" }
    private val BUCKET_NAME: String get() = try { BuildConfig.CLOUDFLARE_R2_BUCKET_NAME } catch (e: Exception) { "" }
    private val ACCESS_KEY_ID: String get() = try { BuildConfig.CLOUDFLARE_R2_ACCESS_KEY_ID } catch (e: Exception) { "" }
    private val SECRET_ACCESS_KEY: String get() = try { BuildConfig.CLOUDFLARE_R2_SECRET_ACCESS_KEY } catch (e: Exception) { "" }
    private val PUBLIC_URL_PREFIX: String get() = try { BuildConfig.CLOUDFLARE_R2_PUBLIC_URL_PREFIX } catch (e: Exception) { "" }

    fun isConfigured(): Boolean {
        val configured = ACCOUNT_ID.isNotBlank() &&
                BUCKET_NAME.isNotBlank() &&
                ACCESS_KEY_ID.isNotBlank() &&
                SECRET_ACCESS_KEY.isNotBlank() &&
                !ACCOUNT_ID.contains("YOUR_") &&
                !BUCKET_NAME.contains("YOUR_")
        Log.d(TAG, "R2 Configured checking: $configured (Account ID: $ACCOUNT_ID, Bucket: $BUCKET_NAME)")
        return configured
    }

    suspend fun uploadFile(context: Context, uri: Uri): String? = withContext(Dispatchers.IO) {
        if (!isConfigured()) {
            Log.w(TAG, "Cloudflare R2 is not fully configured in environment variables / BuildConfig.")
            return@withContext null
        }

        try {
            val contentResolver = context.contentResolver
            val originalMimeType = contentResolver.getType(uri) ?: "image/jpeg"
            val isImage = originalMimeType.startsWith("image/") && !originalMimeType.contains("gif", ignoreCase = true)
            
            val extension: String
            val uploadBytesToUse: ByteArray
            val uploadMimeType: String
            
            if (isImage) {
                val optimizedBytes = optimizeImage(context, uri)
                if (optimizedBytes != null) {
                    uploadBytesToUse = optimizedBytes
                    uploadMimeType = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                        "image/webp"
                    } else {
                        "image/jpeg"
                    }
                    extension = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) "webp" else "jpg"
                    Log.d(TAG, "Image optimized successfully. New size: ${uploadBytesToUse.size} bytes.")
                } else {
                    // Fallback to raw bytes
                    val bytes = readBytes(context, uri) ?: return@withContext null
                    uploadBytesToUse = bytes
                    uploadMimeType = originalMimeType
                    extension = "jpg"
                }
            } else {
                extension = when {
                    originalMimeType.contains("video", ignoreCase = true) -> "mp4"
                    originalMimeType.contains("gif", ignoreCase = true) -> "gif"
                    else -> "jpg"
                }
                uploadBytesToUse = readBytes(context, uri) ?: return@withContext null
                uploadMimeType = originalMimeType
            }

            val fileName = "r2_media_${System.currentTimeMillis()}_${(100..999).random()}.$extension"

            return@withContext uploadBytes(uploadBytesToUse, fileName, uploadMimeType)
        } catch (e: Exception) {
            Log.e(TAG, "Failed resolving Uri content to upload to Cloudflare R2", e)
            null
        }
    }

    private fun readBytes(context: Context, uri: Uri): ByteArray? {
        return try {
            val inputStream: InputStream = context.contentResolver.openInputStream(uri) ?: return null
            val byteBuffer = ByteArrayOutputStream()
            val buffer = ByteArray(4096)
            var len: Int
            while (inputStream.read(buffer).also { len = it } != -1) {
                byteBuffer.write(buffer, 0, len)
            }
            inputStream.close()
            byteBuffer.toByteArray()
        } catch (e: Exception) {
            Log.e(TAG, "Error reading bytes from URI", e)
            null
        }
    }

    private fun optimizeImage(context: Context, uri: Uri): ByteArray? {
        return try {
            val options = android.graphics.BitmapFactory.Options()
            options.inJustDecodeBounds = true
            context.contentResolver.openInputStream(uri)?.use { 
                android.graphics.BitmapFactory.decodeStream(it, null, options)
            }
            
            // Calculate a reasonable size, max 1080p
            val reqWidth = 1080
            val reqHeight = 1080
            var inSampleSize = 1
            if (options.outHeight > reqHeight || options.outWidth > reqWidth) {
                val halfHeight: Int = options.outHeight / 2
                val halfWidth: Int = options.outWidth / 2
                while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                    inSampleSize *= 2
                }
            }
            
            options.inJustDecodeBounds = false
            options.inSampleSize = inSampleSize
            
            val bitmap = context.contentResolver.openInputStream(uri)?.use {
                android.graphics.BitmapFactory.decodeStream(it, null, options)
            } ?: return null
            
            val bos = ByteArrayOutputStream()
            // Compress with WebP on Android 11+ for better file size/quality, or JPEG on older devices
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                bitmap.compress(android.graphics.Bitmap.CompressFormat.WEBP_LOSSY, 80, bos)
            } else {
                @Suppress("DEPRECATION")
                bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 80, bos)
            }
            
            val bytes = bos.toByteArray()
            bitmap.recycle()
            bytes
        } catch (e: Exception) {
            Log.e(TAG, "Image optimization failed", e)
            null
        }
    }

    suspend fun uploadBytes(fileBytes: ByteArray, key: String, contentType: String): String? = withContext(Dispatchers.IO) {
        val accountId = ACCOUNT_ID.trim()
        val bucket = BUCKET_NAME.trim()
        val accessKey = ACCESS_KEY_ID.trim()
        val secretKey = SECRET_ACCESS_KEY.trim()
        val publicPrefix = PUBLIC_URL_PREFIX.trim().removeSuffix("/")

        val region = "auto"
        val amzDateFormat = SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'").apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val dateFormat = SimpleDateFormat("yyyyMMdd").apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

        val now = Date()
        val amzDate = amzDateFormat.format(now)
        val dateStamp = dateFormat.format(now)

        val host = "$bucket.$accountId.r2.cloudflarestorage.com"
        val canonicalUri = "/$key"
        val endpoint = "https://$host$canonicalUri"

        val canonicalQueryString = ""
        val canonicalHeaders = "host:$host\n" +
                "x-amz-content-sha256:UNSIGNED-PAYLOAD\n" +
                "x-amz-date:$amzDate\n"
        val signedHeaders = "host;x-amz-content-sha256;x-amz-date"
        val payloadHash = "UNSIGNED-PAYLOAD"

        val canonicalRequest = "PUT\n" +
                "$canonicalUri\n" +
                "$canonicalQueryString\n" +
                "$canonicalHeaders\n" +
                "$signedHeaders\n" +
                payloadHash

        val hashedCanonicalRequest = sha256(canonicalRequest)
        val algorithm = "AWS4-HMAC-SHA256"
        val credentialScope = "$dateStamp/$region/s3/aws4_request"

        val stringToSign = "$algorithm\n" +
                "$amzDate\n" +
                "$credentialScope\n" +
                hashedCanonicalRequest

        val kDate = hmacSHA256(dateStamp, ("AWS4$secretKey").toByteArray(Charsets.UTF_8))
        val kRegion = hmacSHA256(region, kDate)
        val kService = hmacSHA256("s3", kRegion)
        val kSigning = hmacSHA256("aws4_request", kService)

        val signatureBytes = hmacSHA256(stringToSign, kSigning)
        val signature = bytesToHex(signatureBytes)

        val authorizationHeader = "$algorithm " +
                "Credential=$accessKey/$credentialScope, " +
                "SignedHeaders=$signedHeaders, " +
                "Signature=$signature"

        val requestBody = fileBytes.toRequestBody(contentType.toMediaTypeOrNull())

        val request = Request.Builder()
            .url(endpoint)
            .put(requestBody)
            .addHeader("Authorization", authorizationHeader)
            .addHeader("host", host)
            .addHeader("x-amz-content-sha256", payloadHash)
            .addHeader("x-amz-date", amzDate)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val finalUrl = if (publicPrefix.isNotBlank()) {
                        "$publicPrefix/$key"
                    } else {
                        "https://pub-${accountId}.r2.dev/$bucket/$key"
                    }
                    Log.d(TAG, "Uploaded to Cloudflare R2 successfully: $finalUrl")
                    return@withContext finalUrl
                } else {
                    val bodyStr = response.body?.string() ?: ""
                    Log.e(TAG, "Upload failed to Cloudflare R2: Status: ${response.code}, Message: ${response.message}, Body: $bodyStr")
                    return@withContext null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error executing Cloudflare R2 upload request", e)
            return@withContext null
        }
    }

    private fun sha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(input.toByteArray(Charsets.UTF_8))
        return hash.joinToString("") { String.format("%02x", it) }
    }

    private fun hmacSHA256(data: String, key: ByteArray): ByteArray {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(key, "HmacSHA256"))
        return mac.doFinal(data.toByteArray(Charsets.UTF_8))
    }

    private fun hmacSHA256(data: ByteArray, key: ByteArray): ByteArray {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(key, "HmacSHA256"))
        return mac.doFinal(data)
    }

    private fun bytesToHex(bytes: ByteArray): String {
        return bytes.joinToString("") { String.format("%02x", it) }
    }
}
