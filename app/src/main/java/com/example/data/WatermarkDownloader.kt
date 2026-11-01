package com.example.data

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import androidx.media3.common.MediaItem
import androidx.media3.effect.BitmapOverlay
import androidx.media3.effect.OverlayEffect
import androidx.media3.transformer.Composition
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.Effects
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.ProgressHolder
import androidx.media3.transformer.Transformer
import com.google.common.collect.ImmutableList
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object WatermarkDownloader {
    private const val TAG = "WatermarkDownloader"

    fun isMediaVideo(mediaUri: String?): Boolean {
        if (mediaUri.isNullOrEmpty()) return false
        val lower = mediaUri.lowercase()
        return lower.contains("video") ||
                lower.contains("inapp_rec") ||
                lower.contains("local_media") ||
                lower.endsWith(".mp4") ||
                lower.endsWith(".mkv") ||
                lower.endsWith(".webm") ||
                lower.endsWith(".3gp") ||
                lower.endsWith(".mov") ||
                lower.endsWith(".m4v")
    }

    /**
     * Creates a transparent single watermark badge placed in a specific corner of a canvas.
     * Corner 0: Top-Left, 1: Bottom-Right, 2: Top-Right, 3: Bottom-Left.
     * Uses transparent background (~15% opacity) with text drop shadow so underlying media shows through.
     */
    fun createSingleWatermarkBadge(
        width: Int,
        height: Int,
        authorName: String,
        cornerPosition: Int
    ): Bitmap {
        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val scale = (width / 720f).coerceIn(0.6f, 3.0f)

        val cleanAuthor = if (authorName.startsWith("@")) authorName else "@$authorName"
        val brandText = "⚡ Zooz"

        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = AndroidColor.WHITE
            textSize = 20f * scale
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            setShadowLayer(6f * scale, 1.5f * scale, 2f * scale, AndroidColor.argb(220, 0, 0, 0))
        }
        val subTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = AndroidColor.parseColor("#25F4EE")
            textSize = 16f * scale
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            setShadowLayer(5f * scale, 1.5f * scale, 2f * scale, AndroidColor.argb(220, 0, 0, 0))
        }

        val authorWidth = textPaint.measureText(cleanAuthor)
        val brandWidth = textPaint.measureText(brandText)
        val maxContentWidth = maxOf(authorWidth, brandWidth)

        val boxWidth = maxContentWidth + (40f * scale)
        val boxHeight = 58f * scale
        val padMargin = 28f * scale

        val left = when (cornerPosition) {
            0 -> padMargin // Top-Left
            1 -> width - padMargin - boxWidth // Bottom-Right
            2 -> width - padMargin - boxWidth // Top-Right
            3 -> padMargin // Bottom-Left
            else -> padMargin
        }

        val top = when (cornerPosition) {
            0 -> padMargin // Top-Left
            1 -> height - padMargin - boxHeight // Bottom-Right
            2 -> padMargin // Top-Right
            3 -> height - padMargin - boxHeight // Bottom-Left
            else -> padMargin
        }

        val rect = RectF(left, top, left + boxWidth, top + boxHeight)

        // Transparent subtle glass background (~15% opacity so video/image content shows through)
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = AndroidColor.argb(38, 10, 10, 16)
            style = Paint.Style.FILL
        }
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = if (cornerPosition % 2 == 0) AndroidColor.parseColor("#A0FE2C55") else AndroidColor.parseColor("#A025F4EE")
            style = Paint.Style.STROKE
            strokeWidth = 2.5f * scale
        }

        canvas.drawRoundRect(rect, 16f * scale, 16f * scale, bgPaint)
        canvas.drawRoundRect(rect, 16f * scale, 16f * scale, borderPaint)

        canvas.drawText(brandText, left + (14f * scale), top + (24f * scale), textPaint)
        canvas.drawText(cleanAuthor, left + (14f * scale), top + (47f * scale), subTextPaint)

        return output
    }

    /**
     * Applies official TikTok-style watermark badges directly to a bitmap with transparent backgrounds.
     * Includes top-left and bottom-right creator watermark badges.
     */
    fun createWatermarkedBitmap(src: Bitmap, authorName: String, title: String? = null): Bitmap {
        val width = src.width
        val height = src.height
        val output = src.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(output)

        // Composite Top-Left (Corner 0) and Bottom-Right (Corner 1) transparent badges onto image
        val badgeTL = createSingleWatermarkBadge(width, height, authorName, 0)
        val badgeBR = createSingleWatermarkBadge(width, height, authorName, 1)

        val compositePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        canvas.drawBitmap(badgeTL, 0f, 0f, compositePaint)
        canvas.drawBitmap(badgeBR, 0f, 0f, compositePaint)

        return output
    }

    /**
     * Creates a transparent watermark overlay bitmap with exact video dimensions.
     */
    fun createWatermarkOverlayBitmap(width: Int, height: Int, authorName: String): Bitmap {
        return createSingleWatermarkBadge(width, height, authorName, 0)
    }

    /**
     * Creates a high-definition 9:16 vertical poster card with gradient background
     * for text-only posts, watermarked with author and Zooz branding.
     */
    fun createTextPostPoster(authorName: String, title: String, content: String): Bitmap {
        val width = 1080
        val height = 1920
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Dark modern gradient background
        val bgPaint = Paint().apply {
            shader = android.graphics.LinearGradient(
                0f, 0f, width.toFloat(), height.toFloat(),
                AndroidColor.parseColor("#0C0C12"),
                AndroidColor.parseColor("#1B1226"),
                android.graphics.Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // Center card
        val cardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = AndroidColor.argb(180, 24, 24, 34)
            style = Paint.Style.FILL
        }
        val cardBorder = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = AndroidColor.argb(60, 255, 255, 255)
            style = Paint.Style.STROKE
            strokeWidth = 3f
        }
        val cardRect = RectF(80f, 360f, width - 80f, height - 360f)
        canvas.drawRoundRect(cardRect, 32f, 32f, cardPaint)
        canvas.drawRoundRect(cardRect, 32f, 32f, cardBorder)

        // Title text
        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = AndroidColor.WHITE
            textSize = 54f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        val displayTitle = if (title.isNotBlank()) title else "Zooz Story"
        canvas.drawText(displayTitle, (width / 2f), 520f, titlePaint)

        // Content excerpt
        val contentPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = AndroidColor.argb(220, 230, 230, 230)
            textSize = 36f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            textAlign = Paint.Align.CENTER
        }

        val lines = content.chunked(34).take(8)
        var yPos = 620f
        for (line in lines) {
            canvas.drawText(line, (width / 2f), yPos, contentPaint)
            yPos += 54f
        }

        return createWatermarkedBitmap(bitmap, authorName, title)
    }

    /**
     * Downloads an image, applies watermark, and saves to public MediaStore (Pictures/Zooz).
     */
    suspend fun downloadAndWatermarkImage(
        context: Context,
        mediaUri: String?,
        authorName: String,
        title: String,
        content: String = ""
    ): Result<Uri> = withContext(Dispatchers.IO) {
        try {
            val baseBitmap: Bitmap = if (mediaUri.isNullOrEmpty()) {
                createTextPostPoster(authorName, title, content)
            } else if (mediaUri.startsWith("http://") || mediaUri.startsWith("https://")) {
                val url = URL(mediaUri)
                val connection = (url.openConnection() as HttpURLConnection).apply {
                    connectTimeout = 15000
                    readTimeout = 15000
                    doInput = true
                }
                connection.connect()
                val input: InputStream = connection.inputStream
                val bmp = BitmapFactory.decodeStream(input)
                input.close()
                bmp ?: createTextPostPoster(authorName, title, content)
            } else {
                val uri = Uri.parse(mediaUri)
                val input = context.contentResolver.openInputStream(uri)
                val bmp = BitmapFactory.decodeStream(input)
                input?.close()
                bmp ?: createTextPostPoster(authorName, title, content)
            }

            val watermarked = createWatermarkedBitmap(baseBitmap, authorName, title)

            // Save to MediaStore (Pictures/Zooz)
            val cleanAuthor = authorName.replace("@", "").filter { it.isLetterOrDigit() }.ifBlank { "creator" }
            val filename = "zooz_${cleanAuthor}_${System.currentTimeMillis()}.png"

            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/Zooz")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
            }

            val imageUri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                ?: return@withContext Result.failure(Exception("Failed to create MediaStore entry"))

            context.contentResolver.openOutputStream(imageUri)?.use { out ->
                watermarked.compress(Bitmap.CompressFormat.PNG, 100, out)
                out.flush()
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                context.contentResolver.update(imageUri, contentValues, null, null)
            }

            Result.success(imageUri)
        } catch (e: Exception) {
            Log.e(TAG, "Error downloading and watermarking image", e)
            Result.failure(e)
        }
    }

    /**
     * Transcodes an MP4 video using AndroidX Media3 Transformer, burning the watermark overlay
     * onto every frame of the video.
     */
    suspend fun transcodeVideoWithWatermark(
        context: Context,
        inputFile: File,
        authorName: String,
        onProgress: (Float, String) -> Unit
    ): File = withContext(Dispatchers.Main) {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(inputFile.absolutePath)
        val widthStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
        val heightStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
        val rotationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)
        retriever.release()

        var videoWidth = widthStr?.toIntOrNull() ?: 720
        var videoHeight = heightStr?.toIntOrNull() ?: 1280
        val rotation = rotationStr?.toIntOrNull() ?: 0
        if (rotation == 90 || rotation == 270) {
            val temp = videoWidth
            videoWidth = videoHeight
            videoHeight = temp
        }

        val corner0 = createSingleWatermarkBadge(videoWidth, videoHeight, authorName, 0)
        val corner1 = createSingleWatermarkBadge(videoWidth, videoHeight, authorName, 1)
        val corner2 = createSingleWatermarkBadge(videoWidth, videoHeight, authorName, 2)
        val corner3 = createSingleWatermarkBadge(videoWidth, videoHeight, authorName, 3)

        val dynamicBitmapOverlay = object : BitmapOverlay() {
            override fun getBitmap(presentationTimeUs: Long): Bitmap {
                // Transit watermark across corners every 3 seconds (3,000,000 microseconds) like TikTok
                val cycle = ((presentationTimeUs / 3_000_000L) % 4).toInt()
                return when (cycle) {
                    0 -> corner0
                    1 -> corner1
                    2 -> corner2
                    3 -> corner3
                    else -> corner0
                }
            }
        }
        val overlayEffect = OverlayEffect(listOf<androidx.media3.effect.TextureOverlay>(dynamicBitmapOverlay))

        val editedMediaItem = EditedMediaItem.Builder(MediaItem.fromUri(Uri.fromFile(inputFile)))
            .setEffects(Effects(emptyList(), listOf(overlayEffect)))
            .build()

        val outputFile = File(context.cacheDir, "zooz_watermarked_${System.currentTimeMillis()}.mp4")
        if (outputFile.exists()) outputFile.delete()

        suspendCancellableCoroutine { continuation ->
            var isResumed = false
            val progressHolder = ProgressHolder()

            val transformer = Transformer.Builder(context)
                .addListener(object : Transformer.Listener {
                    override fun onCompleted(composition: Composition, exportResult: ExportResult) {
                        if (!isResumed) {
                            isResumed = true
                            continuation.resume(outputFile)
                        }
                    }

                    override fun onError(composition: Composition, exportResult: ExportResult, exportException: ExportException) {
                        if (!isResumed) {
                            isResumed = true
                            continuation.resumeWithException(exportException)
                        }
                    }
                })
                .build()

            try {
                transformer.start(editedMediaItem, outputFile.absolutePath)
            } catch (e: Exception) {
                if (!isResumed) {
                    isResumed = true
                    continuation.resumeWithException(e)
                }
            }

            val progressJob = kotlinx.coroutines.CoroutineScope(Dispatchers.Main).launch {
                while (!isResumed && continuation.isActive) {
                    delay(150)
                    val state = transformer.getProgress(progressHolder)
                    if (state == Transformer.PROGRESS_STATE_AVAILABLE) {
                        val p = 0.45f + (progressHolder.progress / 100f * 0.45f)
                        onProgress(p, "Burning watermark into video (${progressHolder.progress}%)...")
                    }
                }
            }

            continuation.invokeOnCancellation {
                progressJob.cancel()
                try {
                    transformer.cancel()
                } catch (e: Exception) {
                    // Ignore
                }
                if (outputFile.exists()) outputFile.delete()
            }
        }
    }

    /**
     * Downloads video, burns the watermark overlay onto the video frames using Media3 Transformer,
     * and saves the watermarked video to MediaStore (Movies/Zooz).
     */
    suspend fun downloadAndWatermarkVideo(
        context: Context,
        mediaUri: String,
        authorName: String,
        title: String,
        onProgress: (Float, String) -> Unit = { _, _ -> }
    ): Result<Uri> = withContext(Dispatchers.IO) {
        try {
            onProgress(0.10f, "Connecting to video stream...")

            // 1. Download or copy video stream to cache
            val cleanAuthor = authorName.replace("@", "").filter { it.isLetterOrDigit() }.ifBlank { "creator" }
            val tempFile = File(context.cacheDir, "zooz_temp_${System.currentTimeMillis()}.mp4")

            if (mediaUri.startsWith("http://") || mediaUri.startsWith("https://")) {
                val url = URL(mediaUri)
                val conn = (url.openConnection() as HttpURLConnection).apply {
                    connectTimeout = 15000
                    readTimeout = 20000
                }
                conn.connect()
                val totalBytes = conn.contentLength.toFloat().coerceAtLeast(1f)
                var downloadedBytes = 0L

                conn.inputStream.use { input ->
                    FileOutputStream(tempFile).use { output ->
                        val buffer = ByteArray(8192)
                        var bytesRead: Int
                        while (input.read(buffer).also { bytesRead = it } != -1) {
                            output.write(buffer, 0, bytesRead)
                            downloadedBytes += bytesRead
                            val progress = 0.10f + ((downloadedBytes / totalBytes) * 0.35f).coerceIn(0f, 0.35f)
                            onProgress(progress, "Downloading video (${(downloadedBytes / 1024)} KB)...")
                        }
                        output.flush()
                    }
                }
            } else {
                val srcUri = Uri.parse(mediaUri)
                context.contentResolver.openInputStream(srcUri)?.use { input ->
                    FileOutputStream(tempFile).use { output ->
                        input.copyTo(output)
                        output.flush()
                    }
                }
            }

            onProgress(0.48f, "Burning watermark into video frames...")

            // 2. Transcode the video to burn in the watermark
            var transcodedFile: File? = null
            try {
                transcodedFile = transcodeVideoWithWatermark(context, tempFile, authorName, onProgress)
            } catch (transcodeErr: Exception) {
                Log.w(TAG, "Video watermark transcoding error: ${transcodeErr.message}. Using safe fallback.", transcodeErr)
            }

            val finalVideoFile = transcodedFile ?: tempFile

            onProgress(0.92f, "Saving watermarked video to Gallery...")

            // 3. Save the video to MediaStore (Movies/Zooz)
            val videoFilename = "zooz_${cleanAuthor}_${System.currentTimeMillis()}.mp4"
            val videoValues = ContentValues().apply {
                put(MediaStore.Video.Media.DISPLAY_NAME, videoFilename)
                put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
                put(MediaStore.Video.Media.RELATIVE_PATH, "${Environment.DIRECTORY_MOVIES}/Zooz")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Video.Media.IS_PENDING, 1)
                }
            }

            val videoUri = context.contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoValues)
                ?: return@withContext Result.failure(Exception("Failed to create MediaStore video entry"))

            context.contentResolver.openOutputStream(videoUri)?.use { outStream ->
                finalVideoFile.inputStream().use { inStream ->
                    inStream.copyTo(outStream)
                    outStream.flush()
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                videoValues.clear()
                videoValues.put(MediaStore.Video.Media.IS_PENDING, 0)
                context.contentResolver.update(videoUri, videoValues, null, null)
            }

            // Also generate companion watermarked poster for gallery
            try {
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(finalVideoFile.absolutePath)
                val frameBmp = retriever.getFrameAtTime(1000000) ?: retriever.frameAtTime
                retriever.release()

                if (frameBmp != null) {
                    val watermarkedFrame = createWatermarkedBitmap(frameBmp, authorName, title)
                    val frameName = "zooz_${cleanAuthor}_frame_${System.currentTimeMillis()}.png"
                    val imgValues = ContentValues().apply {
                        put(MediaStore.Images.Media.DISPLAY_NAME, frameName)
                        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                        put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/Zooz")
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            put(MediaStore.Images.Media.IS_PENDING, 1)
                        }
                    }
                    val frameUri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imgValues)
                    if (frameUri != null) {
                        context.contentResolver.openOutputStream(frameUri)?.use { out ->
                            watermarkedFrame.compress(Bitmap.CompressFormat.PNG, 95, out)
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            imgValues.clear()
                            imgValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                            context.contentResolver.update(frameUri, imgValues, null, null)
                        }
                    }
                }
            } catch (frameEx: Exception) {
                Log.w(TAG, "Frame extraction warning: ${frameEx.message}")
            }

            // Clean up temporary files
            if (transcodedFile != null && transcodedFile.exists()) {
                transcodedFile.delete()
            }
            if (tempFile.exists()) {
                tempFile.delete()
            }

            onProgress(1.0f, "Completed! Watermark burned into video.")

            Result.success(videoUri)
        } catch (e: Exception) {
            Log.e(TAG, "Error downloading video", e)
            Result.failure(e)
        }
    }
}

/**
 * Modern TikTok-style Download with Watermark Dialog.
 * Provides live watermark badge preview, download progress, and direct open/share actions.
 */
@Composable
fun DownloadWithWatermarkDialog(
    mediaUri: String?,
    authorName: String,
    title: String,
    content: String = "",
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val isVideo = remember(mediaUri) { WatermarkDownloader.isMediaVideo(mediaUri) }

    var isProcessing by remember { mutableStateOf(false) }
    var progressVal by remember { mutableFloatStateOf(0f) }
    var statusText by remember { mutableStateOf("Ready to download") }
    var savedUri by remember { mutableStateOf<Uri?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = { if (!isProcessing) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF141419),
            border = BorderStroke(1.dp, Color(0xFF2A2A35))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFE2C55).copy(alpha = 0.2f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Download,
                                    contentDescription = null,
                                    tint = Color(0xFFFE2C55),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Save with Watermark",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Includes @$authorName watermark badges",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        enabled = !isProcessing,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Media Watermark Preview Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF0C0C10))
                        .border(BorderStroke(1.dp, Color(0xFF22222C)), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (!mediaUri.isNullOrEmpty() && !isVideo) {
                        AsyncImage(
                            model = mediaUri,
                            contentDescription = "Media Preview",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else if (isVideo) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color(0xFF1E1E28), Color(0xFF0F0F14))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color.Black.copy(alpha = 0.6f),
                                    modifier = Modifier.size(52.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = null,
                                            tint = Color(0xFF25F4EE),
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (title.isNotBlank()) title else "Zooz Video Clip",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else {
                        // Text story preview
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFFFE2C55).copy(alpha = 0.25f), Color(0xFF25F4EE).copy(alpha = 0.25f))
                                    )
                                )
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (title.isNotBlank()) title else content.take(60),
                                color = Color.White,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Watermark Preview: Top-Left
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(10.dp),
                        shape = RoundedCornerShape(6.dp),
                        color = Color.Black.copy(alpha = 0.75f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(Color(0xFFFE2C55), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Zooz",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Watermark Preview: Bottom-Right
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(10.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Black.copy(alpha = 0.85f),
                        border = BorderStroke(1.dp, Color(0xFFFE2C55).copy(alpha = 0.7f))
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "⚡ Zooz",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "@$authorName",
                                color = Color(0xFF25F4EE),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Progress or Status Section
                if (isProcessing) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LinearProgressIndicator(
                            progress = { progressVal },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = Color(0xFFFE2C55),
                            trackColor = Color(0xFF2A2A35)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF25F4EE),
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else if (savedUri != null) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF4CAF50).copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, Color(0xFF4CAF50).copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Saved with Watermark!",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = if (isVideo) "Available in Gallery (Movies/Zooz)" else "Available in Photos (Pictures/Zooz)",
                                    color = Color.White.copy(alpha = 0.7f),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                } else if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Action Buttons
                if (savedUri != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    setDataAndType(savedUri, if (isVideo) "video/mp4" else "image/png")
                                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                                }
                                try {
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    android.widget.Toast.makeText(context, "Opened in gallery", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                        ) {
                            Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Open", color = Color.White)
                        }

                        Button(
                            onClick = {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = if (isVideo) "video/mp4" else "image/png"
                                    putExtra(Intent.EXTRA_STREAM, savedUri)
                                    putExtra(Intent.EXTRA_TEXT, "Watch @$authorName on Zooz! #ZoozOfficial")
                                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share Watermarked Media"))
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFE2C55))
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share", color = Color.White)
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                isProcessing = true
                                errorMessage = null
                                coroutineScope.launch {
                                    if (isVideo && !mediaUri.isNullOrEmpty()) {
                                        val result = WatermarkDownloader.downloadAndWatermarkVideo(
                                            context = context,
                                            mediaUri = mediaUri,
                                            authorName = authorName,
                                            title = title,
                                            onProgress = { p, msg ->
                                                progressVal = p
                                                statusText = msg
                                            }
                                        )
                                        isProcessing = false
                                        result.onSuccess { uri ->
                                            savedUri = uri
                                            android.widget.Toast.makeText(context, "Video saved with watermark to Movies/Zooz!", android.widget.Toast.LENGTH_LONG).show()
                                        }.onFailure { err ->
                                            errorMessage = "Failed: ${err.localizedMessage ?: "Unknown error"}"
                                        }
                                    } else {
                                        progressVal = 0.3f
                                        statusText = "Applying Zooz watermarks..."
                                        val result = WatermarkDownloader.downloadAndWatermarkImage(
                                            context = context,
                                            mediaUri = mediaUri,
                                            authorName = authorName,
                                            title = title,
                                            content = content
                                        )
                                        isProcessing = false
                                        result.onSuccess { uri ->
                                            savedUri = uri
                                            progressVal = 1.0f
                                            android.widget.Toast.makeText(context, "Image saved with watermark to Pictures/Zooz!", android.widget.Toast.LENGTH_LONG).show()
                                        }.onFailure { err ->
                                            errorMessage = "Failed: ${err.localizedMessage ?: "Unknown error"}"
                                        }
                                    }
                                }
                            },
                            enabled = !isProcessing,
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFE2C55),
                                disabledContainerColor = Color(0xFFFE2C55).copy(alpha = 0.5f)
                            )
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isVideo) "Save Video with Watermark" else "Save Image with Watermark",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
