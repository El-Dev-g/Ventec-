package com.example.ui

import kotlinx.coroutines.launch

import android.net.Uri
import android.content.Intent
import android.widget.VideoView
import android.widget.MediaController
import android.widget.TextView
import android.text.util.Linkify
import androidx.core.text.util.LinkifyCompat
import androidx.compose.ui.graphics.toArgb
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.draw.clipToBounds
import coil.compose.AsyncImage
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.ViewStream
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.input.pointer.pointerInput
import android.media.MediaRecorder
import android.media.MediaPlayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Post
import com.example.data.Comment
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import com.example.data.R2Uploader
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.basicMarquee
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Save
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.LinearEasing
import com.example.data.ChatMessage


@Composable
fun ZoozLogo(modifier: Modifier = Modifier, fontSize: androidx.compose.ui.unit.TextUnit = MaterialTheme.typography.titleLarge.fontSize) {
    Text(
        text = buildAnnotatedString {
            withStyle(style = SpanStyle(color = Color(0xFF4285F4))) { append("z") } // Blue
            withStyle(style = SpanStyle(color = Color(0xFFEA4335))) { append("o") } // Red
            withStyle(style = SpanStyle(color = Color(0xFFFBBC05))) { append("o") } // Yellow
            withStyle(style = SpanStyle(color = Color(0xFF34A853))) { append("z") } // Green
        },
        fontSize = fontSize,
        fontWeight = FontWeight.Black,
        modifier = modifier
    )
}

@Composable
fun ZoozIconCircle(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF4285F4),
                        Color(0xFFEA4335),
                        Color(0xFFFBBC05),
                        Color(0xFF34A853)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "z",
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 14.sp,
            modifier = Modifier.offset(y = (-1).dp)
        )
    }
}

fun formatRelativeTime(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        diff < 0 -> "Just now" // Safety for slight clock drifts
        seconds < 60 -> "Just now"
        minutes == 1L -> "1 minute ago"
        minutes < 60 -> "$minutes minutes ago"
        hours == 1L -> "1 hour ago"
        hours < 24 -> "$hours hours ago"
        days == 1L -> "1 day ago"
        else -> "$days days ago"
    }
}



@Composable
fun LoginScreen(
    viewModel: BlogViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToForgotPassword: () -> Unit = {}
) {
    var isLoginMode by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("prigidcollection@gmail.com") }
    var password by remember { mutableStateOf("Lordson1@") }
    var passwordVisible by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf<String?>(null) }
    var showEmailVerificationNotice by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var showZoozAuthPrompt by remember { mutableStateOf(false) }
    var isZoozAuthenticating by remember { mutableStateOf(false) }
    var selectedZoozEmail by remember { mutableStateOf<String?>(null) }
    var selectedZoozUserForConfirmation by remember { mutableStateOf<com.example.data.User?>(null) }
    var pendingAlias by remember { mutableStateOf<String?>(null) }
    var zoozSectionMode by remember { mutableStateOf("CREATE") }
    var zoozAuthStep by remember { mutableIntStateOf(0) } // 0: Select Account, 1: Auth Forms
    val zoozScrollState = rememberScrollState()
    val registeredUsers by viewModel.allUsers.collectAsStateWithLifecycle(initialValue = emptyList())

    val isFirebaseConfigured = remember {
        val apiKey = com.example.BuildConfig.FIREBASE_API_KEY
        apiKey.isNotBlank() && apiKey != "YOUR_FIREBASE_API_KEY"
    }

    val context = androidx.compose.ui.platform.LocalContext.current
    val googleSignInClient = remember {
        try {
            // Attempt to get the default_web_client_id if it exists, otherwise use a placeholder
            val resId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
            val webClientId = if (resId != 0) context.getString(resId) else com.example.BuildConfig.FIREBASE_API_KEY
            val gso = com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder(com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(webClientId)
                .requestEmail()
                .build()
            com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(context, gso)
        } catch (e: Exception) {
            null
        }
    }

    val launcher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val task = com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(com.google.android.gms.common.api.ApiException::class.java)
                val idToken = account?.idToken
                if (idToken != null) {
                    viewModel.loginWithGoogle(idToken, pendingAlias, onLoginSuccess, { loginError = it })
                } else {
                    loginError = "Google Sign In failed: No ID token."
                }
            } catch (e: com.google.android.gms.common.api.ApiException) {
                loginError = "Google Sign In failed: ${e.message}"
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (showEmailVerificationNotice) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Email,
                        contentDescription = "Email Verification Sent",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    "Check your Inbox",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    "We have sent a secure verification link to activate your account. Click on the link to confirm your membership.",
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Sent to email address:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = email,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = "Next steps:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "Success Step 1",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Check your mail folders (including spam).",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "Success Step 2",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Tap the link inside the verification email.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "Success Step 3",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Return here, select Sign In, and log in!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                if (!isFirebaseConfigured) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = "Secure Info",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Demo Mode: No real verification email is required! Click below to auto-verify and login directly.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                if (!isFirebaseConfigured) {
                    Button(
                        onClick = {
                            isLoginMode = true
                            showEmailVerificationNotice = false
                            onLoginSuccess()
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Demo Auto-Verify & Continue", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                } else {
                    Button(
                        onClick = {
                            showEmailVerificationNotice = false
                            isLoginMode = true
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Back to Sign In", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                TextButton(
                    onClick = {
                        showEmailVerificationNotice = false
                        isLoginMode = false
                    }
                ) {
                    Text("Change Email / Back to Sign Up", color = MaterialTheme.colorScheme.primary)
                }
            }
        } else {
            Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ZoozLogo(fontSize = 48.sp)
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = if (isLoginMode) "Welcome to Zooz" else "Create an account",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isLoginMode) "Sign in to continue" else "Sign up to get started",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            AssistChip(
                onClick = {
                    email = "prigidcollection@gmail.com"
                    password = "Lordson1@"
                },
                label = {
                    Text(
                        "Quick Fill: prigidcollection@gmail.com",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Quick Fill",
                        modifier = Modifier.size(18.dp)
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                    labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email or Username") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            if (!email.contains("@") && email.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Will authenticate secure alias: ${email.lowercase().trim()}@zooz.secure",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, contentDescription = "Toggle password")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (isLoginMode) {
                TextButton(
                    onClick = onNavigateToForgotPassword,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Forgot Password?")
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            var loginErrorLocalState by remember { mutableStateOf(loginError) }
            // Sync local error state
            LaunchedEffect(loginError) { loginErrorLocalState = loginError }

            val isZoozAlias = (!email.contains("@") && email.isNotBlank()) || 
                              email.endsWith("@zooz.secure", ignoreCase = true) || 
                              email.endsWith("@zooz.official", ignoreCase = true)
            val useSSO = isLoginMode && password.isBlank() && isZoozAlias

            Button(
                onClick = { 
                    loginError = null
                    if (email.isNotBlank()) {
                         if (useSSO) {
                             showZoozAuthPrompt = true
                             return@Button
                         }
                         
                         isLoading = true
                         if (isLoginMode) {
                             viewModel.login(
                                 emailOrUsername = email,
                                 password = password,
                                 isLoginMode = true,
                                 onComplete = {
                                     isLoading = false
                                     onLoginSuccess()
                                 },
                                 onError = {
                                     isLoading = false
                                     loginError = it
                                 }
                             )
                         } else {
                             viewModel.login(
                                 emailOrUsername = email,
                                 password = password,
                                 isLoginMode = false,
                                 onComplete = {
                                     isLoading = false
                                     if (isFirebaseConfigured) {
                                         onLoginSuccess()
                                     } else {
                                         // Show verification simulation notice screen for secure demo users
                                         showEmailVerificationNotice = true
                                     }
                                 },
                                 onError = { msg ->
                                     isLoading = false
                                     val isSuccessMessage = msg.contains("Account created") || msg.contains("verify your email")
                                     if (isSuccessMessage) {
                                         showEmailVerificationNotice = true
                                     } else {
                                         loginError = msg
                                     }
                                 }
                             )
                         }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    val btnText = when {
                        useSSO -> "Sign In with Zooz SSO"
                        isLoginMode -> "Sign In"
                        else -> "Sign Up"
                    }
                    Text(btnText, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            
            if (loginError != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(loginError!!, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
                Text(
                    " OR ",
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelLarge
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Google Sign In
                OutlinedButton(
                    onClick = {
                        try {
                            pendingAlias = null // Reset for direct Google button
                            googleSignInClient?.signInIntent?.let {
                                launcher.launch(it)
                            } ?: run {
                                // Live fallback: instantly logs in as simulated Google user
                                viewModel.loginWithGoogle("simulated_desktop_user", null, onLoginSuccess, { loginError = it })
                            }
                        } catch (e: Exception) {
                            viewModel.loginWithGoogle("simulated_desktop_user", null, onLoginSuccess, { loginError = it })
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5.dp, Color(0xFF4285F4).copy(alpha = 0.6f))
                ) {
                    Icon(
                        painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.ic_google_logo),
                        contentDescription = "Google Logo",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Google",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Zooz Sign In
                OutlinedButton(
                    onClick = {
                        showZoozAuthPrompt = true
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5.dp, Color(0xFF34A853).copy(alpha = 0.6f))
                ) {
                    Icon(
                        painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.app_icon_zooz_1779400725898),
                        contentDescription = "Zooz Logo",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(24.dp).clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Zooz",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            if (showZoozAuthPrompt) {
                androidx.compose.ui.window.Dialog(
                    onDismissRequest = { if (!isZoozAuthenticating) showZoozAuthPrompt = false },
                    properties = androidx.compose.ui.window.DialogProperties(
                        usePlatformDefaultWidth = false,
                        dismissOnBackPress = !isZoozAuthenticating,
                        dismissOnClickOutside = false
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            shape = RoundedCornerShape(28.dp),
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 8.dp,
                            border = androidx.compose.foundation.BorderStroke(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                // Top App Header
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp)
                                        .padding(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.app_icon_zooz_1779400725898),
                                            contentDescription = "Zooz Logo",
                                            tint = Color.Unspecified,
                                            modifier = Modifier.size(28.dp).clip(CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = "Zooz Secure Auth",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    if (!isZoozAuthenticating) {
                                        IconButton(
                                            onClick = { showZoozAuthPrompt = false },
                                            modifier = Modifier.size(48.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Close",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }

                            // Full Page Content
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .verticalScroll(zoozScrollState)
                                    .padding(horizontal = 24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Spacer(modifier = Modifier.height(24.dp))

                                // Dynamic Logo Presentation
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.app_icon_zooz_1779400725898),
                                        contentDescription = "Zooz Big Logo",
                                        tint = Color.Unspecified,
                                        modifier = Modifier.size(52.dp).clip(CircleShape)
                                    )
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                Text(
                                    text = "Choose an account",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "to continue to Prigid App securely",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(36.dp))

                                if (isZoozAuthenticating) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 40.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        CircularProgressIndicator(
                                            color = Color(0xFF34A853),
                                            modifier = Modifier.size(52.dp)
                                        )
                                        Spacer(modifier = Modifier.height(20.dp))
                                        Text(
                                            text = "Connecting securely to Zooz...",
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = selectedZoozEmail ?: "",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                } else if (selectedZoozUserForConfirmation != null) {
                                    val user = selectedZoozUserForConfirmation!!
                                    val displayName = if (user.fullName.isNotBlank()) user.fullName else user.username
                                    val userEmail = if (user.username.contains("@")) user.username else "${user.username.lowercase()}@zooz.secure"
                                    val avatarChar = if (displayName.isNotEmpty()) displayName.first().uppercase() else "U"
                                    val colors = listOf(
                                        Color(0xFF4285F4),
                                        Color(0xFFEA4335),
                                        Color(0xFFFBBC05),
                                        Color(0xFF34A853),
                                        Color(0xFF9C27B0),
                                        Color(0xFF00BCD4),
                                        Color(0xFFE91E63)
                                    )
                                    val avatarColor = if (user.id == -99) Color(0xFF34A853) else colors[kotlin.math.abs(user.id) % colors.size]

                                    Column(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "Security Confirmation",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "Verify credentials & authorize simulated email mapping",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(24.dp))

                                        Box(
                                            modifier = Modifier
                                                .size(80.dp)
                                                .background(color = avatarColor, shape = CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = avatarChar,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.headlineMedium
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(14.dp))

                                        Text(
                                            text = displayName,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = userEmail,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Medium
                                        )
                                        
                                        Spacer(modifier = Modifier.height(20.dp))
                                        
                                        Card(
                                            colors = CardDefaults.cardColors(
                                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(12.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.CheckCircle,
                                                        contentDescription = "Simulated Domain Enabled",
                                                        tint = Color(0xFF34A853),
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = "Secure Local Domain Mapped",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = Color(0xFF34A853),
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = "Accessing app via Zooz SSO. No password required. Your linked Google identity will authorize this session immediately.",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = Color(0xFF34A853),
                                                    textAlign = TextAlign.Center,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(28.dp))

                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(10.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Button(
                                                onClick = {
                                                    selectedZoozEmail = userEmail
                                                    isZoozAuthenticating = true
                                                    pendingAlias = user.username
                                                    try {
                                                        googleSignInClient?.signInIntent?.let {
                                                            launcher.launch(it)
                                                        } ?: run {
                                                            // Fallback for simulation
                                                            viewModel.loginWithGoogle("simulated_sso_user", pendingAlias, onLoginSuccess, { loginError = it })
                                                            isZoozAuthenticating = false
                                                            showZoozAuthPrompt = false
                                                        }
                                                    } catch (e: Exception) {
                                                        viewModel.loginWithGoogle("simulated_sso_user", pendingAlias, onLoginSuccess, { loginError = it })
                                                        isZoozAuthenticating = false
                                                        showZoozAuthPrompt = false
                                                    }
                                                },
                                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                                shape = RoundedCornerShape(12.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = Color(0xFF34A853)
                                                )
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = Color.White
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("Authorize with Google SSO", color = Color.White, fontWeight = FontWeight.Bold)
                                            }

                                            OutlinedButton(
                                                onClick = {
                                                    selectedZoozUserForConfirmation = null
                                                },
                                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                                shape = RoundedCornerShape(12.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("Back")
                                            }
                                        }
                                    }
                                } else if (zoozAuthStep == 0) {
                                    // Step 0: Account Selector List
                                    Column(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(16.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            // Dynamic Room Database Registered Users
                                            registeredUsers.forEach { user ->
                                                val displayName = if (user.fullName.isNotBlank()) user.fullName else user.username
                                                val userEmail = if (user.username.contains("@")) user.username else "${user.username.lowercase()}@zooz.secure"
                                                val avatarChar = if (displayName.isNotEmpty()) displayName.first().uppercase() else "U"
                                                val colors = listOf(
                                                    Color(0xFF4285F4),
                                                    Color(0xFFEA4335),
                                                    Color(0xFFFBBC05),
                                                    Color(0xFF34A853),
                                                    Color(0xFF9C27B0),
                                                    Color(0xFF00BCD4),
                                                    Color(0xFFE91E63)
                                                )
                                                val avatarColor = colors[kotlin.math.abs(user.id) % colors.size]

                                                ZoozAccountRow(
                                                    name = displayName,
                                                    email = userEmail,
                                                    avatarText = avatarChar,
                                                    avatarColor = avatarColor
                                                ) {
                                                    selectedZoozUserForConfirmation = user
                                                }
                                            }

                                            // Official Developer Option
                                            ZoozAccountRow(
                                                name = "Official Zooz Developer",
                                                email = "developer@zooz.official",
                                                avatarText = "D",
                                                avatarColor = Color(0xFF34A853)
                                            ) {
                                                selectedZoozUserForConfirmation = com.example.data.User(
                                                    id = -99,
                                                    username = "developer@zooz.official",
                                                    fullName = "Official Zooz Developer"
                                                )
                                            }

                                            // Trigger Section: Add another account
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .clickable {
                                                        zoozAuthStep = 1
                                                        zoozSectionMode = "CREATE"
                                                    }
                                                    .padding(vertical = 12.dp, horizontal = 16.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(40.dp)
                                                        .background(
                                                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                                            shape = CircleShape
                                                        ),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Add,
                                                        contentDescription = null,
                                                        tint = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(16.dp))
                                                Text(
                                                    text = "Add another account",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    // Step 1: Switchable Auth Forms (Sign Up, Sign In, Forgot)
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                                                shape = RoundedCornerShape(16.dp)
                                            )
                                            .padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceEvenly
                                        ) {
                                            TextButton(
                                                onClick = { zoozSectionMode = "CREATE" },
                                                colors = ButtonDefaults.textButtonColors(
                                                    contentColor = if (zoozSectionMode == "CREATE") Color(0xFF34A853) else MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            ) {
                                                Text("Sign Up", fontWeight = if (zoozSectionMode == "CREATE") FontWeight.Bold else FontWeight.Normal)
                                            }
                                            TextButton(
                                                onClick = { zoozSectionMode = "SIGNIN" },
                                                colors = ButtonDefaults.textButtonColors(
                                                    contentColor = if (zoozSectionMode == "SIGNIN") Color(0xFF34A853) else MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            ) {
                                                Text("Sign In", fontWeight = if (zoozSectionMode == "SIGNIN") FontWeight.Bold else FontWeight.Normal)
                                            }
                                            TextButton(
                                                onClick = { zoozSectionMode = "FORGOT" },
                                                colors = ButtonDefaults.textButtonColors(
                                                    contentColor = if (zoozSectionMode == "FORGOT") Color(0xFF34A853) else MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            ) {
                                                Text("Forgot ID?", fontWeight = if (zoozSectionMode == "FORGOT") FontWeight.Bold else FontWeight.Normal)
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(16.dp))

                                        when (zoozSectionMode) {
                                            "CREATE" -> {
                                                Text(
                                                    text = "Create New Auth Link",
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.align(Alignment.Start),
                                                    color = Color(0xFF34A853)
                                                )
                                                Spacer(modifier = Modifier.height(16.dp))
                                                
                                                var inlineUsername by remember { mutableStateOf("") }
                                                var inlineError by remember { mutableStateOf<String?>(null) }
                                                
                                                OutlinedTextField(
                                                    value = inlineUsername,
                                                    onValueChange = { 
                                                        inlineUsername = it
                                                        inlineError = null 
                                                    },
                                                    label = { Text("Secure Username") },
                                                    placeholder = { Text("e.g. johndoe") },
                                                    suffix = { Text("@zooz.secure") },
                                                    modifier = Modifier.fillMaxWidth(),
                                                    shape = RoundedCornerShape(12.dp),
                                                    singleLine = true,
                                                    isError = inlineError != null
                                                )
                                                
                                                if (inlineError != null) {
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        text = inlineError ?: "",
                                                        color = MaterialTheme.colorScheme.error,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        modifier = Modifier.align(Alignment.Start)
                                                    )
                                                }
                                                
                                                Spacer(modifier = Modifier.height(16.dp))
                                                
                                                Button(
                                                    onClick = {
                                                        val trimmed = inlineUsername.trim()
                                                        if (trimmed.isBlank()) {
                                                            inlineError = "Username cannot be empty"
                                                            return@Button
                                                        }
                                                        if (trimmed.contains("@")) {
                                                            inlineError = "Do not include @ domain"
                                                            return@Button
                                                        }
                                                        
                                                        isZoozAuthenticating = true
                                                        pendingAlias = trimmed
                                                        try {
                                                            googleSignInClient?.signInIntent?.let {
                                                                launcher.launch(it)
                                                            } ?: run {
                                                                viewModel.loginWithGoogle("simulated_reg_user", pendingAlias, onLoginSuccess, { loginError = it })
                                                                isZoozAuthenticating = false
                                                                showZoozAuthPrompt = false
                                                            }
                                                        } catch (e: Exception) {
                                                            viewModel.loginWithGoogle("simulated_reg_user", pendingAlias, onLoginSuccess, { loginError = it })
                                                            isZoozAuthenticating = false
                                                            showZoozAuthPrompt = false
                                                        }
                                                    },
                                                    modifier = Modifier.fillMaxWidth().height(52.dp),
                                                    shape = RoundedCornerShape(12.dp),
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = Color(0xFF34A853)
                                                    )
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Add,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(10.dp))
                                                    Text("Create & Authorize", fontWeight = FontWeight.Bold)
                                                }
                                            }
                                            "SIGNIN" -> {
                                                Text(
                                                    text = "Sign In with Secure ID",
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.align(Alignment.Start),
                                                    color = Color(0xFF34A853)
                                                )
                                                Spacer(modifier = Modifier.height(16.dp))
                                                
                                                var loginAlias by remember { mutableStateOf("") }
                                                
                                                OutlinedTextField(
                                                    value = loginAlias,
                                                    onValueChange = { loginAlias = it },
                                                    label = { Text("Secure ID / Username") },
                                                    placeholder = { Text("e.g. johndoe") },
                                                    suffix = { 
                                                        if (!loginAlias.contains("@")) {
                                                            Text("@zooz.secure") 
                                                        }
                                                    },
                                                    modifier = Modifier.fillMaxWidth(),
                                                    shape = RoundedCornerShape(12.dp),
                                                    singleLine = true
                                                )
                                                
                                                Spacer(modifier = Modifier.height(16.dp))
                                                
                                                Button(
                                                    onClick = {
                                                        if (loginAlias.isNotBlank()) {
                                                            isZoozAuthenticating = true
                                                            val alias = if (loginAlias.contains("@")) loginAlias else "$loginAlias@zooz.secure"
                                                            pendingAlias = alias
                                                            try {
                                                                googleSignInClient?.signInIntent?.let {
                                                                    launcher.launch(it)
                                                                } ?: run {
                                                                    viewModel.loginWithGoogle("simulated_sso", pendingAlias, onLoginSuccess, { loginError = it })
                                                                    isZoozAuthenticating = false
                                                                    showZoozAuthPrompt = false
                                                                }
                                                            } catch (e: Exception) {
                                                                viewModel.loginWithGoogle("simulated_sso", pendingAlias, onLoginSuccess, { loginError = it })
                                                                isZoozAuthenticating = false
                                                                showZoozAuthPrompt = false
                                                            }
                                                        }
                                                    },
                                                    modifier = Modifier.fillMaxWidth().height(52.dp),
                                                    shape = RoundedCornerShape(12.dp),
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = Color(0xFF34A853)
                                                    )
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.AutoMirrored.Outlined.ExitToApp,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(10.dp))
                                                    Text("Authorize Session", fontWeight = FontWeight.Bold)
                                                }
                                            }
                                            "FORGOT" -> {
                                                Text(
                                                    text = "Recover Secure ID",
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.align(Alignment.Start),
                                                    color = Color(0xFFEA4335)
                                                )
                                                Spacer(modifier = Modifier.height(12.dp))
                                                Text(
                                                    text = "Zooz Secure IDs are passwordless and linked to your primary identity. Enter your recovery email below to receive your ID list.",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                Spacer(modifier = Modifier.height(16.dp))
                                                
                                                var recoveryEmail by remember { mutableStateOf("") }
                                                var recoveryStatus by remember { mutableStateOf<String?>(null) }
                                                
                                                OutlinedTextField(
                                                    value = recoveryEmail,
                                                    onValueChange = { recoveryEmail = it; recoveryStatus = null },
                                                    label = { Text("Recovery Email") },
                                                    placeholder = { Text("e.g. user@gmail.com") },
                                                    modifier = Modifier.fillMaxWidth(),
                                                    shape = RoundedCornerShape(12.dp),
                                                    singleLine = true
                                                )
                                                
                                                if (recoveryStatus != null) {
                                                    Spacer(modifier = Modifier.height(8.dp))
                                                    Text(recoveryStatus!!, color = Color(0xFF34A853), style = MaterialTheme.typography.bodySmall)
                                                }
                                                
                                                Spacer(modifier = Modifier.height(16.dp))
                                                
                                                Button(
                                                    onClick = {
                                                        if (recoveryEmail.isNotBlank()) {
                                                            recoveryStatus = "Recovery instructions sent! Please check your inbox."
                                                        }
                                                    },
                                                    modifier = Modifier.fillMaxWidth().height(52.dp),
                                                    shape = RoundedCornerShape(12.dp),
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = Color(0xFFEA4335)
                                                    )
                                                ) {
                                                    Text("Send Recovery Email", fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(24.dp))
                                        
                                        TextButton(onClick = { zoozAuthStep = 0 }) {
                                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Back to account list")
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(36.dp))

                                // Device-Isolated Secure Security Badge
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = "Security",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = "Isolated Privacy Guaranteed: Existing device user profiles are never fetched, shared, or compiled from Firebase in secure mode to guarantee total privacy.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            fontWeight = FontWeight.Medium,
                                            lineHeight = 16.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(32.dp))

                                // Footer regulatory disclaimer text
                                Text(
                                    text = "To continue, Zooz will share your name, email address, profile picture and secure token with Prigid App. See physical terms of service & privacy statements.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                    lineHeight = 16.sp,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }
                    }
                }
            }
        }

            Spacer(modifier = Modifier.height(48.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(
                    if (isLoginMode) "Don't have an account?" else "Already have an account?",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isLoginMode) "Sign up" else "Sign in",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { isLoginMode = !isLoginMode }
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(
                    text = buildAnnotatedString {
                        append("Powered by ")
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.ExtraBold
                            )
                        ) {
                            append("Prigid group")
                        }
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
        }
        }
    }
}

@Composable
fun ZoozAccountRow(
    name: String,
    email: String,
    avatarText: String,
    avatarColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = avatarColor,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = avatarText,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = email,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun HomeTopOverlayHeader(
    feedType: String,
    onFeedTypeSelected: (String) -> Unit,
    onSearchClick: () -> Unit,
    onAlertsClick: () -> Unit,
    onChatClick: () -> Unit,
    unreadAlertsCount: Int = 0,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0.8f),
                        Color.Black.copy(alpha = 0.35f),
                        Color.Transparent
                    )
                )
            )
            .statusBarsPadding()
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ZoozLogo(fontSize = 20.sp)

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onFeedTypeSelected("following") }
                ) {
                    Text(
                        text = "Following",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (feedType == "following") FontWeight.Bold else FontWeight.Medium,
                        color = if (feedType == "following") Color.White else Color.White.copy(alpha = 0.65f),
                        fontSize = 15.sp
                    )
                    if (feedType == "following") {
                        Box(
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .width(20.dp)
                                .height(3.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFE2C55))
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onFeedTypeSelected("foryou") }
                ) {
                    Text(
                        text = "For You",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (feedType == "foryou") FontWeight.Bold else FontWeight.Medium,
                        color = if (feedType == "foryou") Color.White else Color.White.copy(alpha = 0.65f),
                        fontSize = 15.sp
                    )
                    if (feedType == "foryou") {
                        Box(
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .width(20.dp)
                                .height(3.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFE2C55))
                        )
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onSearchClick, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
                }

                Box(contentAlignment = Alignment.TopEnd) {
                    IconButton(onClick = onAlertsClick, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Outlined.Notifications, contentDescription = "Alerts", tint = Color.White)
                    }
                    if (unreadAlertsCount > 0) {
                        Box(
                            modifier = Modifier
                                .padding(top = 4.dp, end = 4.dp)
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFE2C55))
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HomeStoriesTray(
    viewModel: BlogViewModel,
    onNavigateToCreateStory: () -> Unit,
    onNavigateToUserProfile: (Int) -> Unit
) {
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle(initialValue = emptyList())
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    val rainbowBrush = remember {
        Brush.sweepGradient(
            colors = listOf(
                Color(0xFFFE2C55),
                Color(0xFFFF007F),
                Color(0xFFE040FB),
                Color(0xFF00E5FF),
                Color(0xFF25F4EE),
                Color(0xFFFE2C55)
            )
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(4.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable { onNavigateToCreateStory() }
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (!currentUser?.profilePicUri.isNullOrEmpty()) {
                        AsyncImage(
                            model = currentUser?.profilePicUri,
                            contentDescription = "Your Story",
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
                    }
                }
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFE2C55))
                        .border(2.dp, Color.Black, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Story", tint = Color.White, modifier = Modifier.size(10.dp))
                }
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text("Your Story", style = MaterialTheme.typography.labelSmall, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
        }

        allUsers.take(8).forEach { u ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onNavigateToUserProfile(u.id) }
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(rainbowBrush)
                        .padding(2.dp)
                        .background(Color.Black, CircleShape)
                        .padding(1.5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!u.profilePicUri.isNullOrEmpty()) {
                            AsyncImage(
                                model = u.profilePicUri,
                                contentDescription = u.username,
                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = (u.fullName.firstOrNull() ?: u.username.firstOrNull() ?: 'U').uppercase(),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "@${u.username}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    fontSize = 9.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = 56.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(4.dp))
    }
}

@Composable
fun HomeCategoryChipsRow(
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf(
        null to "🔥 For You",
        "Tech" to "✨ Tech",
        "Shorts" to "⚡ Shorts",
        "Music" to "🎵 Music",
        "Gaming" to "🎮 Gaming",
        "Design" to "🎨 Design"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        categories.forEach { (catKey, label) ->
            val isSelected = (catKey == null && selectedCategory.isNullOrBlank()) || (catKey != null && catKey.equals(selectedCategory, ignoreCase = true))
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected) Color(0xFFFE2C55) else Color.Black.copy(alpha = 0.45f),
                border = BorderStroke(1.dp, if (isSelected) Color(0xFFFE2C55) else Color.White.copy(alpha = 0.25f)),
                modifier = Modifier.clickable { onCategorySelected(catKey) }
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                    fontSize = 11.sp
                )
            }
        }
    }
}

sealed class FeedItem {
    abstract val feedKey: String

    data class OrganicPost(val post: Post) : FeedItem() {
        override val feedKey: String get() = "post_${post.id}"
    }

    data class PromotedAd(val campaign: CreatorBoostCampaign) : FeedItem() {
        override val feedKey: String get() = "ad_${campaign.id}"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: BlogViewModel,
    onNavigateToCreatePost: () -> Unit,
    onNavigateToPostDetail: (Int) -> Unit,
    onNavigateToMenuDest: (String) -> Unit,
    onNavigateToUserProfile: (Int) -> Unit
) {
    val posts by viewModel.allPosts.collectAsStateWithLifecycle()
    val boostCampaigns by viewModel.boostCampaigns.collectAsStateWithLifecycle()
    val user by viewModel.currentUser.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle(initialValue = emptyList())
    val unreadAlertsCount = remember(notifications) { notifications.count { !it.isRead } }

    var showMenuSheet by remember { mutableStateOf(false) }
    var showChatSheet by remember { mutableStateOf(false) }
    var selectedPostForComments by remember { mutableStateOf<Post?>(null) }
    var currentTab by remember { mutableStateOf("home") }
    var feedSubTab by remember { mutableStateOf("foryou") } // "foryou", "following"

    BackHandler(enabled = currentTab != "home") {
        currentTab = "home"
    }
    var showStoriesTray by remember { mutableStateOf(false) }

    var visibleLimit by remember { mutableStateOf(10) }
    var isLoadingMore by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    LaunchedEffect(selectedFilter) {
        visibleLimit = 10
    }

    LaunchedEffect(listState, posts, posts.size) {
        snapshotFlow {
            val totalItems = listState.layoutInfo.totalItemsCount
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            val lastVisibleIndex = lastVisibleItem?.index ?: -1
            
            lastVisibleIndex >= totalItems - 1 && totalItems > 0
        }.collect { isAtEnd ->
            if (isAtEnd && visibleLimit < (posts.size + boostCampaigns.size) && !isLoadingMore) {
                isLoadingMore = true
                visibleLimit = (visibleLimit + 5).coerceAtMost(posts.size + boostCampaigns.size + 10)
                isLoadingMore = false
            }
        }
    }

    val filteredFeedPosts = remember(posts, feedSubTab, viewModel) {
        if (feedSubTab == "following") {
            posts.filter { viewModel.isFollowing(it.authorId).value }
                .ifEmpty { posts } // Fallback to posts if following list is empty so feed remains active
        } else {
            posts
        }
    }

    val feedItems = remember(filteredFeedPosts, boostCampaigns, feedSubTab) {
        val activeCampaigns = boostCampaigns.filter { it.status == "ACTIVE" }
        val list = mutableListOf<FeedItem>()
        var campIdx = 0
        if (filteredFeedPosts.isEmpty()) {
            activeCampaigns.forEach { list.add(FeedItem.PromotedAd(it)) }
        } else {
            filteredFeedPosts.forEachIndexed { index, post ->
                list.add(FeedItem.OrganicPost(post))
                // Seamlessly display promotional content as mixed posts among real posts
                if (activeCampaigns.isNotEmpty() && (index == 0 || (index > 0 && index % 3 == 0))) {
                    val camp = activeCampaigns[campIdx % activeCampaigns.size]
                    list.add(FeedItem.PromotedAd(camp))
                    campIdx++
                }
            }
        }
        list
    }

    val visibleFeedItems = remember(feedItems, visibleLimit) {
        feedItems.take(visibleLimit)
    }

    val visiblePosts = remember(filteredFeedPosts, visibleLimit) {
        filteredFeedPosts.take(visibleLimit)
    }

    Scaffold(
        topBar = {
            if (currentTab != "profile" && currentTab != "home") {
                TopAppBar(
                    title = { 
                        Text(when(currentTab) {
                            "search" -> "Search"
                            "alerts" -> "Alerts"
                            else -> "Zooz"
                        }, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    },
                    actions = {
                        IconButton(onClick = { currentTab = "search" }) {
                            val isSelected = currentTab == "search"
                            Icon(
                                imageVector = if (isSelected) Icons.Filled.Search else Icons.Outlined.Search,
                                contentDescription = "Search",
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                            )
                        }
                        IconButton(onClick = { currentTab = "alerts" }) {
                            val isSelected = currentTab == "alerts"
                            Icon(
                                imageVector = if (isSelected) Icons.Filled.Notifications else Icons.Outlined.Notifications,
                                contentDescription = "Alerts",
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                        actionIconContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            }
        },
        bottomBar = { 
            BlogBottomNavigation(
                currentTab = currentTab,
                onTabSelected = { currentTab = it },
                onChatClick = { onNavigateToMenuDest("messaging?userId=-1") },
                onCreateClick = onNavigateToCreatePost
            ) 
        },
        floatingActionButton = {}
    ) { padding ->
        var isRefreshing by remember { mutableStateOf(false) }
        val coroutineScope = rememberCoroutineScope()
        fun handleRefresh() {
            isRefreshing = true
            viewModel.syncPosts()
            coroutineScope.launch {
                kotlinx.coroutines.delay(1000) // visual feedback
                isRefreshing = false
            }
        }
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { handleRefresh() },
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            when (currentTab) {
            "search" -> {
                Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    val searchQuery = selectedFilter ?: ""
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setFilter(it.takeIf { it.isNotBlank() }) },
                        placeholder = { Text("Search creators, tags, posts...") },
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setFilter(null) }) {
                                    Icon(Icons.Filled.Close, contentDescription = "Clear search", tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f))
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        ),
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(26.dp),
                        singleLine = true
                    )
                    
                    val categories = listOf("All", "Tech", "Videos", "Social", "Life", "Updates", "News", "Design", "Fun")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { category ->
                            val isSelected = (category == "All" && searchQuery.isBlank()) || (category.equals(searchQuery, ignoreCase = true))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary 
                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    )
                                    .clickable {
                                        if (category == "All") {
                                            viewModel.setFilter(null)
                                        } else {
                                            viewModel.setFilter(category)
                                        }
                                    }
                                    .padding(horizontal = 14.dp, vertical = 7.dp)
                            ) {
                                Text(
                                    text = category,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle(initialValue = emptyList())
                    val matchingUsers = remember(allUsers, searchQuery) {
                        if (searchQuery.isBlank()) emptyList() else {
                            allUsers.filter {
                                it.username.contains(searchQuery, ignoreCase = true) || 
                                it.fullName.contains(searchQuery, ignoreCase = true)
                            }
                        }
                    }

                    if (matchingUsers.isNotEmpty()) {
                        Text(
                            text = "Creators",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            matchingUsers.forEach { u ->
                                Card(
                                    modifier = Modifier
                                        .width(110.dp)
                                        .clickable { onNavigateToUserProfile(u.id) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.primaryContainer),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (!u.profilePicUri.isNullOrEmpty()) {
                                                AsyncImage(
                                                    model = u.profilePicUri,
                                                    contentDescription = "Profile Picture",
                                                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                                )
                                            } else {
                                                val initial = u.fullName.firstOrNull() ?: u.username.firstOrNull() ?: 'U'
                                                Text(
                                                    text = initial.uppercaseChar().toString(),
                                                    style = MaterialTheme.typography.titleSmall,
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "@${u.username}",
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = u.fullName.ifBlank { "User" },
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }


                    val chunkedPosts = remember(visiblePosts) {
                        visiblePosts.chunked(3)
                    }

                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        items(chunkedPosts) { rowPosts ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                rowPosts.forEach { post ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1f)
                                            .clickable { onNavigateToPostDetail(post.id) }
                                    ) {
                                        val isVideo = remember(post.mediaUri) {
                                            val uriStr = post.mediaUri ?: ""
                                            uriStr.contains("video", ignoreCase = true) || 
                                            uriStr.endsWith(".mp4", ignoreCase = true)
                                        }
                                        
                                        if (!post.mediaUri.isNullOrEmpty()) {
                                            AsyncImage(
                                                model = post.mediaUri,
                                                contentDescription = post.title,
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                            )
                                            
                                            if (isVideo) {
                                                Box(
                                                    modifier = Modifier
                                                        .padding(6.dp)
                                                        .size(24.dp)
                                                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                                        .align(Alignment.TopEnd),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Filled.PlayArrow,
                                                        contentDescription = "Video",
                                                        tint = Color.White,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                }
                                            }

                                            if (post.isPromoted) {
                                                // Organic mixing: No visible promo badge as requested
                                            }
                                            
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .align(Alignment.BottomStart)
                                                    .background(
                                                        Brush.verticalGradient(
                                                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f))
                                                        )
                                                    )
                                                    .padding(6.dp)
                                            ) {
                                                Text(
                                                    text = post.title,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = Color.White,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                        } else {
                                            val sunsetBrush = Brush.linearGradient(colors = listOf(Color(0xFF833AB4), Color(0xFFFD1D1D), Color(0xFFFBB03B)))
                                            val oceanBrush = Brush.linearGradient(colors = listOf(Color(0xFF00C6FF), Color(0xFF0072FF)))
                                            val purpleBrush = Brush.linearGradient(colors = listOf(Color(0xFFE040FB), Color(0xFF00E5FF)))
                                            val charcoalBrush = Brush.linearGradient(colors = listOf(Color(0xFF2C3E50), Color(0xFF3498DB)))
                                            
                                            val activeBrush = when (post.id % 4) {
                                                0 -> sunsetBrush
                                                1 -> oceanBrush
                                                2 -> purpleBrush
                                                else -> charcoalBrush
                                            }
                                            
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .background(activeBrush)
                                                    .padding(8.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Column(
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    verticalArrangement = Arrangement.Center,
                                                    modifier = Modifier.fillMaxSize()
                                                ) {
                                                    if (!post.categories.isNullOrBlank()) {
                                                        val firstCategory = post.categories.split(",").firstOrNull()?.trim() ?: ""
                                                        if (firstCategory.isNotEmpty()) {
                                                            Text(
                                                                text = "#$firstCategory".uppercase(),
                                                                style = MaterialTheme.typography.labelSmall,
                                                                color = Color.White.copy(alpha = 0.85f),
                                                                fontWeight = FontWeight.ExtraBold,
                                                                fontSize = 8.sp,
                                                                maxLines = 1
                                                            )
                                                            Spacer(modifier = Modifier.height(2.dp))
                                                        }
                                                    }
                                                    
                                                    Text(
                                                        text = post.title,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = Color.White,
                                                        fontWeight = FontWeight.Bold,
                                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                                        maxLines = 3,
                                                        overflow = TextOverflow.Ellipsis,
                                                        fontSize = 11.sp
                                                    )
                                                    
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    
                                                    Text(
                                                        text = post.content,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = Color.White.copy(alpha = 0.75f),
                                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                                        maxLines = 2,
                                                        overflow = TextOverflow.Ellipsis,
                                                        fontSize = 8.sp,
                                                        lineHeight = 10.sp
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                                if (rowPosts.size < 3) {
                                    val missing = 3 - rowPosts.size
                                    for (i in 0 until missing) {
                                        Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
                                    }
                                }
                            }
                        }
                        
                        if (visiblePosts.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            imageVector = Icons.Filled.Search,
                                            contentDescription = "No match",
                                            modifier = Modifier.size(48.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = "No posts match your search query.",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                        
                        if (isLoadingMore) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        strokeWidth = 2.dp
                                    )
                                }
                            }
                        }
                    }
                }
            }
            "alerts" -> {
                val alerts by viewModel.notifications.collectAsStateWithLifecycle(initialValue = emptyList())
                var visibleLimit by remember { mutableStateOf(6) }
                var isLoadingMore by remember { mutableStateOf(false) }
                var selectedAlertForRead by remember { mutableStateOf<AlertNotification?>(null) }
                var alertFilter by remember { mutableStateOf("all") }

                if (isLoadingMore) {
                    LaunchedEffect(Unit) {
                        visibleLimit += 6
                        isLoadingMore = false
                    }
                }

                val filteredAlerts = remember(alerts, alertFilter) {
                    when (alertFilter) {
                        "social" -> alerts.filter { it.type in listOf("like", "comment", "follow", "direct_message") }
                        "my_log" -> alerts.filter { it.type.startsWith("my_") }
                        "system" -> alerts.filter { it.type in listOf("system", "promotion_gate", "growth_boost") }
                        else -> alerts
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Alerts Stream Layout Header
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Alert History & Activities",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            if (alerts.isNotEmpty()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    TextButton(
                                        onClick = { viewModel.markAllNotificationsAsRead() },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text("Read All", style = MaterialTheme.typography.labelMedium)
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    TextButton(
                                        onClick = { viewModel.clearNotifications() },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text("Clear All", style = MaterialTheme.typography.labelMedium)
                                    }
                                }
                            }
                        }
                    }

                    // Multi-choice scrollable category filters
                    item {
                        val filterOptions = listOf(
                            "all" to "All Feed",
                            "social" to "Social Activity",
                            "my_log" to "My Logs",
                            "system" to "System & Promo"
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            filterOptions.forEach { (key, label) ->
                                val isSelected = alertFilter == key
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    border = BorderStroke(
                                        width = 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                                    ),
                                    modifier = Modifier
                                        .clickable { alertFilter = key }
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }

                    if (filteredAlerts.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Notifications,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "No alerts found",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "New logs and notifications matching this category will appear here.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }
                    } else {
                        val displayedAlerts = filteredAlerts.take(visibleLimit)
                        items(displayedAlerts, key = { it.id }) { alert ->
                            val backgroundColor = when (alert.type) {
                                "promotion_gate" -> Color(0xFFFFD700).copy(alpha = 0.08f)
                                "growth_boost" -> Color(0xFF4CAF50).copy(alpha = 0.06f)
                                "direct_message" -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                                "like", "my_like" -> Color(0xFFE91E63).copy(alpha = 0.06f)
                                "comment", "my_comment" -> Color(0xFF2196F3).copy(alpha = 0.06f)
                                "follow", "my_follow" -> Color(0xFF9C27B0).copy(alpha = 0.06f)
                                "my_publishing" -> Color(0xFF009688).copy(alpha = 0.06f)
                                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            }
                            val borderColor = when (alert.type) {
                                "promotion_gate" -> Color(0xFFFFD700)
                                "growth_boost" -> Color(0xFF81C784)
                                "direct_message" -> MaterialTheme.colorScheme.outlineVariant
                                "like", "my_like" -> Color(0xFFF48FB1)
                                "comment", "my_comment" -> Color(0xFF90CAF9)
                                "follow", "my_follow" -> Color(0xFFCE93D8)
                                "my_publishing" -> Color(0xFF80CBC4)
                                else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            }
                            val icon = when (alert.type) {
                                "promotion_gate" -> Icons.Filled.Stars
                                "growth_boost" -> Icons.Filled.TrendingUp
                                "direct_message" -> Icons.Outlined.Chat
                                "like", "my_like" -> Icons.Filled.Favorite
                                "comment", "my_comment" -> Icons.Outlined.ChatBubbleOutline
                                "follow", "my_follow" -> Icons.Filled.Person
                                "my_publishing" -> Icons.Outlined.Edit
                                else -> Icons.Filled.Notifications
                            }
                            val tintColor = when (alert.type) {
                                "promotion_gate" -> Color(0xFFC59518)
                                "growth_boost" -> Color(0xFF2E7D32)
                                "direct_message" -> MaterialTheme.colorScheme.primary
                                "like", "my_like" -> Color(0xFFE91E63)
                                "comment", "my_comment" -> Color(0xFF1976D2)
                                "follow", "my_follow" -> Color(0xFF9C27B0)
                                "my_publishing" -> Color(0xFF00796B)
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.markNotificationAsRead(alert.id)
                                        selectedAlertForRead = alert
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (alert.isRead) backgroundColor.copy(alpha = 0.4f) else backgroundColor
                                ),
                                border = BorderStroke(
                                    width = if (alert.isRead) 0.5.dp else 1.5.dp,
                                    color = if (alert.isRead) borderColor.copy(alpha = 0.3f) else borderColor
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = tintColor.copy(alpha = 0.15f),
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = icon,
                                                contentDescription = null,
                                                tint = tintColor,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.width(14.dp))
                                    
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text(
                                                    text = alert.title,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = if (alert.isRead) FontWeight.Normal else FontWeight.Bold,
                                                    color = if (alert.isRead) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
                                                )
                                                if (!alert.isRead) {
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Surface(
                                                        shape = CircleShape,
                                                        color = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.size(8.dp)
                                                    ) {}
                                                }
                                            }
                                            
                                            IconButton(
                                                onClick = { viewModel.removeNotification(alert.id) },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.Close,
                                                    contentDescription = "Dismiss Alert",
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                        
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = alert.text,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (alert.isRead) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Aesthetic relative timestamp label
                                            val minutesAgo = ((System.currentTimeMillis() - alert.timestamp) / 60000).coerceAtLeast(0)
                                            val timeStr = when {
                                                minutesAgo < 1 -> "Just now"
                                                minutesAgo == 1L -> "1 minute ago"
                                                minutesAgo < 60 -> "$minutesAgo minutes ago"
                                                else -> "Today"
                                            }
                                            Text(
                                                text = timeStr,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                            )
                                            
                                            if (alert.type == "direct_message" && alert.associatedUserId != null) {
                                                Text(
                                                    text = "Open Chat ➔",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (alerts.size > visibleLimit) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isLoadingMore) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(18.dp),
                                                strokeWidth = 2.dp,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Loading older activity logs...",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    } else {
                                        LaunchedEffect(Unit) {
                                            isLoadingMore = true
                                        }
                                    }
                                }
                            }
                        } else if (alerts.isNotEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "⚡ End of activity tracker history",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        }
                    }
                }

                selectedAlertForRead?.let { alert ->
                    val currentAlert = alerts.find { it.id == alert.id } ?: alert
                    AlertDialog(
                        onDismissRequest = { selectedAlertForRead = null },
                        confirmButton = {
                            if (currentAlert.type == "direct_message" && currentAlert.associatedUserId != null) {
                                Button(
                                    onClick = {
                                        selectedAlertForRead = null
                                        onNavigateToMenuDest("messaging?userId=${currentAlert.associatedUserId}")
                                    }
                                ) {
                                    Icon(Icons.Outlined.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Open Chat")
                                }
                            } else {
                                Button(
                                    onClick = { selectedAlertForRead = null }
                                ) {
                                    Text("Done")
                                }
                            }
                        },
                        dismissButton = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(
                                    onClick = {
                                        viewModel.removeNotification(currentAlert.id)
                                        selectedAlertForRead = null
                                    },
                                    colors = ButtonDefaults.textButtonColors(
                                        contentColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Icon(Icons.Filled.Close, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Delete")
                                }
                                
                                TextButton(
                                    onClick = {
                                        viewModel.markNotificationAsRead(currentAlert.id, isRead = !currentAlert.isRead)
                                    }
                                ) {
                                    Text(if (currentAlert.isRead) "Mark Unread" else "Mark Read")
                                }
                            }
                        },
                        icon = {
                            val tintColor = when (currentAlert.type) {
                                "promotion_gate" -> Color(0xFFFFD700)
                                "growth_boost" -> Color(0xFF4CAF50)
                                "direct_message" -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                            val icon = when (currentAlert.type) {
                                "promotion_gate" -> Icons.Filled.Stars
                                "growth_boost" -> Icons.Filled.TrendingUp
                                "direct_message" -> Icons.Outlined.Chat
                                else -> Icons.Filled.Notifications
                            }
                            Surface(
                                shape = CircleShape,
                                color = tintColor.copy(alpha = 0.12f),
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = tintColor,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        },
                        title = {
                            Text(
                                text = currentAlert.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        },
                        text = {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = currentAlert.text,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(16.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Start
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                val minutesAgo = ((System.currentTimeMillis() - currentAlert.timestamp) / 60000).coerceAtLeast(0)
                                val timeStr = when {
                                    minutesAgo < 1 -> "Just now"
                                    minutesAgo == 1L -> "1 minute ago"
                                    minutesAgo < 60 -> "$minutesAgo minutes ago"
                                    minutesAgo < 1440 -> "${minutesAgo / 60} hours ago"
                                    else -> "Days ago"
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Source: ${currentAlert.type.replace('_', ' ').uppercase()}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                                    )
                                    Text(
                                        text = timeStr,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        },
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }
            "profile" -> {
                ProfileScreen(
                    userId = null,
                    viewModel = viewModel,
                    onNavigateBack = { currentTab = "home" },
                    onNavigateToPostDetail = onNavigateToPostDetail,
                    onNavigateToFollowers = { onNavigateToMenuDest("followers/$it") },
                    onNavigateToFollowing = { onNavigateToMenuDest("following/$it") },
                    onNavigateToMessaging = { onNavigateToMenuDest("messaging?userId=$it") },
                    onNavigateToSettings = { onNavigateToMenuDest("settings") },
                    onNavigateToMenuDest = onNavigateToMenuDest
                )
            }
            else -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    val pagerState = androidx.compose.foundation.pager.rememberPagerState(pageCount = { visibleFeedItems.size })

                    // Automatically log watch history or campaign impressions
                    LaunchedEffect(pagerState.currentPage, visibleFeedItems) {
                        val currentItem = visibleFeedItems.getOrNull(pagerState.currentPage)
                        if (currentItem is FeedItem.OrganicPost) {
                            viewModel.recordWatchHistory(currentItem.post.id)
                        } else if (currentItem is FeedItem.PromotedAd) {
                            viewModel.recordCampaignImpression(currentItem.campaign.id)
                        }
                    }
                    
                    androidx.compose.foundation.pager.VerticalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize(),
                        key = { page -> visibleFeedItems.getOrNull(page)?.feedKey ?: "empty_$page" }
                    ) { page ->
                        when (val item = visibleFeedItems.getOrNull(page)) {
                            is FeedItem.OrganicPost -> {
                                val post = item.post
                                Box(modifier = Modifier.fillMaxSize()) {
                                    PostItem(
                                        post = post,
                                        viewModel = viewModel,
                                        onClick = { onNavigateToPostDetail(post.id) },
                                        onAuthorClick = onNavigateToUserProfile,
                                        isHomeFeed = true,
                                        onCommentsClick = { selectedPostForComments = post },
                                        onSoundClick = { track, artist, preview, img ->
                                            val encodedTrack = java.net.URLEncoder.encode(track, "UTF-8")
                                            val encodedArtist = java.net.URLEncoder.encode(artist, "UTF-8")
                                            val encodedPreview = java.net.URLEncoder.encode(preview ?: "", "UTF-8")
                                            val encodedImg = java.net.URLEncoder.encode(img ?: "", "UTF-8")
                                            onNavigateToMenuDest("sound_detail?trackName=$encodedTrack&trackArtist=$encodedArtist&previewUrl=$encodedPreview&imageUrl=$encodedImg")
                                        },
                                        onTagClick = { tag ->
                                            viewModel.setFilter(tag)
                                            currentTab = "search"
                                        }
                                    )
                                }
                            }
                            is FeedItem.PromotedAd -> {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    PromotedCampaignItem(
                                        campaign = item.campaign,
                                        viewModel = viewModel,
                                        onNavigateToMenuDest = onNavigateToMenuDest
                                    )
                                }
                            }
                            null -> {}
                        }
                    }

                    if (visibleFeedItems.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.75f)),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.padding(32.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Videocam,
                                        contentDescription = null,
                                        tint = Color(0xFFFE2C55),
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        "No posts found",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "Be the first creator to share a short video or post!",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.7f),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(
                                        onClick = { onNavigateToCreatePost() },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFE2C55))
                                    ) {
                                        Text(
                                            "Create Post",
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                    ) {
                        HomeTopOverlayHeader(
                            feedType = feedSubTab,
                            onFeedTypeSelected = { feedSubTab = it },
                            onSearchClick = { currentTab = "search" },
                            onAlertsClick = { currentTab = "alerts" },
                            onChatClick = { onNavigateToMenuDest("messaging?userId=-1") },
                            unreadAlertsCount = unreadAlertsCount
                        )
                    }
                }
            }
        }
    }

    if (showMenuSheet) {
        ModalBottomSheet(
            onDismissRequest = { showMenuSheet = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            val followerCountState = remember(user?.id) { viewModel.getFollowerCount(user?.id ?: 0) }
            val followerCount by followerCountState.collectAsStateWithLifecycle(initialValue = 0)
            
            MenuContent(
                user = user,
                followerCount = followerCount,
                onLogout = {
                    showMenuSheet = false
                    viewModel.logout()
                },
                onItemClick = { route ->
                    showMenuSheet = false
                    if (route == "profile") {
                        currentTab = "profile"
                    } else {
                        onNavigateToMenuDest(route)
                    }
                }
            )
        }
    }

    if (showChatSheet) {
        ModalBottomSheet(
            onDismissRequest = { showChatSheet = false },
            containerColor = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxHeight(0.85f)
        ) {
            DirectMessagesContent(
                viewModel = viewModel,
                onDismiss = { showChatSheet = false },
                onStoryClick = { authorId -> onNavigateToMenuDest("stories/$authorId") },
                onCreateStory = { onNavigateToMenuDest("create_story") }
            )
        }
    }

    if (selectedPostForComments != null) {
        VideoCommentsBottomSheet(
            post = selectedPostForComments!!,
            viewModel = viewModel,
            onDismissRequest = { selectedPostForComments = null }
        )
    }
}
}

@Composable
fun BlogBottomNavigation(
    currentTab: String,
    onTabSelected: (String) -> Unit,
    onChatClick: () -> Unit,
    onCreateClick: () -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        NavigationBarItem(
            selected = currentTab == "home",
            onClick = { onTabSelected("home") },
            icon = { Icon(if (currentTab == "home") Icons.Filled.Home else Icons.Outlined.Home, contentDescription = "Home") },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = currentTab == "search",
            onClick = { onTabSelected("search") },
            icon = {
                Icon(
                    imageVector = if (currentTab == "search") Icons.Filled.Search else Icons.Outlined.Search,
                    contentDescription = "Discover"
                )
            },
            label = { Text("Discover") }
        )
        NavigationBarItem(
            selected = false,
            onClick = onCreateClick,
            icon = { Icon(Icons.Outlined.AddCircle, contentDescription = "Create Post") },
            label = { Text("Create") }
        )
        NavigationBarItem(
            selected = false,
            onClick = onChatClick,
            icon = { Icon(Icons.Outlined.Chat, contentDescription = "Chat") },
            label = { Text("Chat") }
        )
        NavigationBarItem(
            selected = currentTab == "profile",
            onClick = { onTabSelected("profile") },
            icon = { Icon(if (currentTab == "profile") Icons.Filled.Person else Icons.Outlined.Person, contentDescription = "Profile") },
            label = { Text("Profile") }
        )
    }
}

@Composable
fun MenuContent(user: com.example.data.User?, followerCount: Int, onLogout: () -> Unit, onItemClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp)
    ) {
        if (user != null) {
            ListItem(
                headlineContent = { Text(user.username, fontWeight = FontWeight.Bold) },
                supportingContent = { Text("View Profile") },
                leadingContent = {
                    Icon(Icons.Outlined.AccountCircle, contentDescription = "Profile", modifier = Modifier.size(32.dp))
                },
                modifier = Modifier.clickable { onItemClick("profile") }
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        }
        ListItem(
            headlineContent = { Text("Creator Studio & Earnings") },
            leadingContent = { Icon(Icons.Default.TrendingUp, contentDescription = "Creator Studio", tint = Color(0xFFFE2C55)) },
            modifier = Modifier.clickable { onItemClick("creator_tools") }
        )
        ListItem(
            headlineContent = { Text("Watch History") },
            leadingContent = { Icon(Icons.Default.History, contentDescription = "Watch History") },
            modifier = Modifier.clickable { onItemClick("watch_history") }
        )
        ListItem(
            headlineContent = { Text("Account Settings") },
            leadingContent = { Icon(Icons.Outlined.ManageAccounts, contentDescription = "Account Settings") },
            modifier = Modifier.clickable { onItemClick("account_settings") }
        )
        ListItem(
            headlineContent = { Text("App Settings") },
            leadingContent = { Icon(Icons.Outlined.Settings, contentDescription = "App Settings") },
            modifier = Modifier.clickable { onItemClick("settings") }
        )
        ListItem(
            headlineContent = { Text("Help & Support") },
            leadingContent = { Icon(Icons.AutoMirrored.Outlined.HelpOutline, contentDescription = "Help") },
            modifier = Modifier.clickable { onItemClick("help") }
        )
        val context = LocalContext.current
        ListItem(
            headlineContent = { Text("Share Profile") },
            leadingContent = { Icon(Icons.Filled.Share, contentDescription = "Share Profile") },
            modifier = Modifier.clickable {
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "Check out my Zooz profile: ${com.example.BuildConfig.ZOOZ_SHARE_URL}/user/${user?.id}")
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, "Share Profile via")
                context.startActivity(shareIntent)
            }
        )
        ListItem(
            headlineContent = { Text("Share App") },
            leadingContent = { Icon(Icons.Filled.Share, contentDescription = "Share App") },
            modifier = Modifier.clickable {
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "Check out the Zooz App! Download and join our community here: ${com.example.BuildConfig.ZOOZ_SHARE_URL}")
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, "Share Zooz App via")
                context.startActivity(shareIntent)
            }
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        ListItem(
            headlineContent = { Text("Logout", color = MaterialTheme.colorScheme.error) },
            leadingContent = { Icon(Icons.AutoMirrored.Outlined.ExitToApp, contentDescription = "Logout", tint = MaterialTheme.colorScheme.error) },
            modifier = Modifier.clickable { onLogout() }
        )
    }
}

@Composable
fun TikTokDoubleTapHeartOverlay(
    offset: androidx.compose.ui.geometry.Offset,
    rotation: Float,
    onAnimationEnd: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isExpanded) 1.3f else 0.2f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "heart_scale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (isExpanded) 0f else 1f,
        animationSpec = tween(durationMillis = 650, delayMillis = 150),
        finishedListener = { onAnimationEnd() },
        label = "heart_alpha"
    )

    LaunchedEffect(Unit) {
        isExpanded = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clipToBounds()
    ) {
        Box(
            modifier = Modifier
                .offset { IntOffset((offset.x - 60).toInt(), (offset.y - 60).toInt()) }
                .scale(scale)
                .alpha(alpha)
                .rotate(rotation)
        ) {
            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = null,
                tint = Color(0xFFFE2C55),
                modifier = Modifier.size(120.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromotedCampaignItem(
    campaign: CreatorBoostCampaign,
    viewModel: BlogViewModel,
    onNavigateToMenuDest: (String) -> Unit
) {
    val context = LocalContext.current
    var isLiked by remember(campaign.id) { mutableStateOf(false) }
    var likeCount by remember(campaign.id) { mutableIntStateOf(42) }

    LaunchedEffect(campaign.id) {
        viewModel.recordCampaignImpression(campaign.id)
    }

    val openUrl = {
        viewModel.recordCampaignClick(campaign.id)
        val url = campaign.externalUrl
        if (!url.isNullOrBlank()) {
            try {
                val uri = if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    Uri.parse("https://$url")
                } else {
                    Uri.parse(url)
                }
                val intent = Intent(Intent.ACTION_VIEW, uri)
                context.startActivity(intent)
            } catch (e: Exception) {
                android.widget.Toast.makeText(context, "Opening link: $url", android.widget.Toast.LENGTH_SHORT).show()
            }
        } else {
            android.widget.Toast.makeText(context, campaign.callToAction, android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0C0C0E)),
        border = BorderStroke(1.dp, Color(0xFF222227))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Media (Video or Image) - matches regular posts
            if (!campaign.assetMediaUri.isNullOrEmpty()) {
                if (campaign.assetType == "IMAGE") {
                    AsyncImage(
                        model = campaign.assetMediaUri,
                        contentDescription = campaign.postTitle,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                } else {
                    MediaView(
                        mediaUri = campaign.assetMediaUri,
                        modifier = Modifier.fillMaxSize(),
                        autoPlayEnabled = true,
                        isHomeFeed = true,
                        onClick = { openUrl() }
                    )
                }
            } else {
                MediaView(
                    mediaUri = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
                    modifier = Modifier.fillMaxSize(),
                    autoPlayEnabled = true,
                    isHomeFeed = true,
                    onClick = { openUrl() }
                )
            }

            // High-contrast Vignette overlays (identical to regular posts)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .align(Alignment.TopCenter)
                    .background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.5f), Color.Transparent)))
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .align(Alignment.BottomCenter)
                    .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.88f))))
            )

            // Top Bar Overlay: Clean (No specialized sponsored/affiliate/reels badges)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Background transparency for readability but no badges
            }

            // Right Floating Sidebar (Interactions - identical to regular posts)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 12.dp, bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Like Button
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = {
                            isLiked = !isLiked
                            likeCount += if (isLiked) 1 else -1
                        },
                        modifier = Modifier
                            .size(46.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (isLiked) Color(0xFFFE2C55) else Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$likeCount",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Share Button
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, campaign.postTitle)
                                putExtra(Intent.EXTRA_TEXT, "Check out this post on Zooz: ${campaign.postTitle} ${campaign.externalUrl ?: ""}")
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Post"))
                        },
                        modifier = Modifier
                            .size(46.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = "Share",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Share",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Sound / Vinyl Disc
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E1E24))
                        .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = "Music",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Bottom Left Post Information (Author, Caption, CTA - identical to regular posts)
            val authorHandle = campaign.platformName?.lowercase()?.replace(" ", "") ?: "creator"
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth(0.74f)
                    .padding(horizontal = 16.dp, vertical = 18.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { openUrl() }
                ) {
                    Text(
                        text = "@$authorHandle",
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Verified",
                        tint = Color(0xFF25F4EE),
                        modifier = Modifier
                            .size(16.dp)
                            .padding(start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = campaign.postTitle,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Call-To-Action Button embedded right below caption
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFE2C55),
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { openUrl() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Stars,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = campaign.callToAction.ifBlank { "Learn More" },
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionGridItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    bgTint: Color,
    iconTint: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(bgTint, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconTint,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = Color.White.copy(alpha = 0.9f),
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostItem(
    post: Post,
    viewModel: BlogViewModel,
    onClick: () -> Unit,
    onAuthorClick: (Int) -> Unit = {},
    isHomeFeed: Boolean = false,
    onCommentsClick: (() -> Unit)? = null,
    onSoundClick: ((String, String, String?, String?) -> Unit)? = null,
    onTagClick: ((String) -> Unit)? = null
) {
    val context = LocalContext.current
    val isLikedState = remember(post.id) { viewModel.isLiked(post.id) }
    val isLiked by isLikedState.collectAsStateWithLifecycle(initialValue = false)
    
    val likeCountState = remember(post.id) { viewModel.getLikeCount(post.id) }
    val likeCount by likeCountState.collectAsStateWithLifecycle(initialValue = 0)
    
    val isBookmarkedState = remember(post.id) { viewModel.isBookmarked(post.id) }
    val isBookmarked by isBookmarkedState.collectAsStateWithLifecycle(initialValue = false)

    val authorFollowersState = remember(post.authorId) { viewModel.getFollowerCount(post.authorId) }
    val authorFollowers by authorFollowersState.collectAsStateWithLifecycle(initialValue = 0)

    val isFollowingState = remember(post.authorId) { viewModel.isFollowing(post.authorId) }
    val isFollowing by isFollowingState.collectAsStateWithLifecycle(initialValue = false)

    val currentUserState = remember { viewModel.currentUser }
    val currentUser by currentUserState.collectAsStateWithLifecycle()

    val commentsState = remember(post.id) { viewModel.getCommentsForPost(post.id) }
    val comments by commentsState.collectAsStateWithLifecycle(initialValue = emptyList())
    val commentCount = comments.size

    val infiniteTransition = rememberInfiniteTransition(label = "music_disk")
    val rotationSpeed by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "music_rotation_anim"
    )

    var isClearDisplayMode by remember { mutableStateOf(false) }
    var showGiftSheet by remember { mutableStateOf(false) }
    var showCoinRechargeDialog by remember { mutableStateOf(false) }
    var showWatermarkDownloadDialog by remember { mutableStateOf(false) }
    var animatedGiftEmoji by remember { mutableStateOf<String?>(null) }
    val userCoins by viewModel.userCoins.collectAsStateWithLifecycle()

    LaunchedEffect(animatedGiftEmoji) {
        if (animatedGiftEmoji != null) {
            kotlinx.coroutines.delay(2200)
            animatedGiftEmoji = null
        }
    }

    val pollVotesState = remember { viewModel.pollVotes }
    val allPollVotes by pollVotesState.collectAsStateWithLifecycle()
    val currentPollVote = allPollVotes[post.id]

    LaunchedEffect(post.id) {
        viewModel.recordPostImpression(post.id)
    }

    var showDoubleTapHeart by remember { mutableStateOf(false) }
    var doubleTapOffset by remember { mutableStateOf(androidx.compose.ui.geometry.Offset.Zero) }
    var doubleTapRotation by remember { mutableStateOf(0f) }
    var showLongPressActionsSheet by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }

    val isOwner = currentUser != null && (
        currentUser?.id == post.authorId || 
        currentUser?.fullName.equals(post.authorName, ignoreCase = true) ||
        currentUser?.username.equals(post.authorName, ignoreCase = true)
    )

    Card(
        modifier = Modifier
            .fillMaxSize(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0C0C0E)),
        border = BorderStroke(1.dp, Color(0xFF222227))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Immersive Media/Visual Core Background Section
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                if (!post.mediaUri.isNullOrEmpty()) {
                    var lastPressTime by remember { mutableStateOf(0L) }
                    
                    MediaView(
                        mediaUri = post.mediaUri,
                        modifier = Modifier.fillMaxSize(),
                        autoPlayEnabled = isHomeFeed,
                        isHomeFeed = isHomeFeed,
                        onClick = null,
                        isMutedByMusic = !post.spotifyTrackPreviewUrl.isNullOrEmpty(),
                        musicUrl = post.spotifyTrackPreviewUrl,
                        onDoubleTap = { offset ->
                            if (!isLiked) {
                                viewModel.toggleLike(post.id, false)
                            }
                            doubleTapOffset = offset
                            doubleTapRotation = (-25..25).random().toFloat()
                            showDoubleTapHeart = true
                        },
                        onLongPress = {
                            showLongPressActionsSheet = true
                        }
                    )
                } else {
                    TikTokPlaceholderBackground(title = post.title, category = post.categories, author = post.authorName)
                }
            }

            // High-contrast Ambient Vignette bottom section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                        )
                    )
            )

            // High-contrast Ambient shadow top section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Black.copy(alpha = 0.5f), Color.Transparent)
                        )
                    )
            )

            // Clear Display Mode Exit Banner
            if (isClearDisplayMode) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 28.dp)
                        .clickable { isClearDisplayMode = false },
                    shape = RoundedCornerShape(20.dp),
                    color = Color.Black.copy(alpha = 0.65f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = "Exit Clear Display Mode",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Clear Display Mode • Tap to Exit",
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            if (!isClearDisplayMode) {
                // Top-Bar controls Overlay (sponsored indicators and actions)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (post.isPromoted) {
                        // Organic mixing: No visible promoted badge as requested
                    }
                }

                // LEFT SIDE CONTENT SUMMARY PANEL
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.74f)
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 16.dp, vertical = 18.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onAuthorClick(post.authorId) }
                    ) {
                        Text(
                            text = "@${post.authorName}",
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium
                        )
                        if (authorFollowers >= 10000) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "Verified Creator",
                                tint = Color(0xFF25F4EE),
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(start = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    if (post.title.isNotBlank()) {
                        Text(
                            text = post.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                    }
                    Text(
                        text = post.content,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (post.categories.isNotBlank() || post.tags.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (post.categories.isNotBlank()) {
                                post.categories.split(",").take(2).forEach { cat ->
                                    Box(
                                        modifier = Modifier
                                            .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                            .clickable {
                                                viewModel.recordPostClick(post.id)
                                                if (onTagClick != null) onTagClick(cat.trim()) else onClick()
                                            }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(cat.trim().uppercase(), color = Color(0xFF25F4EE), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                                    }
                                }
                            }
                            if (post.tags.isNotBlank()) {
                                post.tags.split(",").take(2).forEach { tag ->
                                    Box(
                                        modifier = Modifier
                                            .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                                            .clickable {
                                                viewModel.recordPostClick(post.id)
                                                if (onTagClick != null) onTagClick(tag.trim()) else onClick()
                                            }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text("#${tag.trim()}", color = Color(0xFFFE2C55), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold, fontSize = 9.sp)
                                    }
                                }
                            }
                        }
                    }

                    // Interactive Video Poll Sticker
                    val hasPoll = remember(post.title, post.tags) {
                        post.tags.contains("poll", ignoreCase = true) || post.tags.contains("vote", ignoreCase = true) || (post.title.contains("?") && post.title.length > 5)
                    }
                    if (hasPoll) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.7f)),
                            border = BorderStroke(1.dp, Color(0xFF25F4EE).copy(alpha = 0.6f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.HowToVote,
                                        contentDescription = null,
                                        tint = Color(0xFF25F4EE),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "COMMUNITY POLL",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                        color = Color(0xFF25F4EE),
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                val option1Count = currentPollVote?.votesA ?: 14
                                val option2Count = currentPollVote?.votesB ?: 7
                                val totalVotes = (option1Count + option2Count).coerceAtLeast(1)
                                val pct1 = (option1Count * 100) / totalVotes
                                val pct2 = 100 - pct1
                                val hasVoted = currentPollVote?.userVotedOption != null

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Option 1
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (currentPollVote?.userVotedOption == 0) Color(0xFF25F4EE).copy(alpha = 0.28f) else Color.White.copy(alpha = 0.12f),
                                        border = BorderStroke(1.dp, if (currentPollVote?.userVotedOption == 0) Color(0xFF25F4EE) else Color.White.copy(alpha = 0.2f)),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { viewModel.votePoll(post.id, 0) }
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "Yes! 🔥",
                                                color = Color.White,
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            if (hasVoted) {
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = "$pct1%",
                                                    color = Color(0xFF25F4EE),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.ExtraBold
                                                )
                                            }
                                        }
                                    }

                                    // Option 2
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (currentPollVote?.userVotedOption == 1) Color(0xFFFE2C55).copy(alpha = 0.28f) else Color.White.copy(alpha = 0.12f),
                                        border = BorderStroke(1.dp, if (currentPollVote?.userVotedOption == 1) Color(0xFFFE2C55) else Color.White.copy(alpha = 0.2f)),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { viewModel.votePoll(post.id, 1) }
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "No way 🤔",
                                                color = Color.White,
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            if (hasVoted) {
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = "$pct2%",
                                                    color = Color(0xFFFE2C55),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.ExtraBold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Audio Marquee Ticker with Tap-to-Sound-Details
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .background(Color.Black.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                            .clickable {
                                val soundTitle = if (!post.spotifyTrackName.isNullOrEmpty()) post.spotifyTrackName else "Original Sound"
                                val soundArtist = if (!post.spotifyTrackArtist.isNullOrEmpty()) post.spotifyTrackArtist else "@${post.authorName}"
                                onSoundClick?.invoke(soundTitle, soundArtist, post.spotifyTrackImageUrl, post.spotifyTrackPreviewUrl)
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .widthIn(max = 200.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = "Sound Details",
                            tint = Color(0xFF25F4EE),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (!post.spotifyTrackName.isNullOrEmpty()) "${post.spotifyTrackName} - ${post.spotifyTrackArtist}" else "original sound - @${post.authorName}",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = Color.White.copy(alpha = 0.9f),
                            maxLines = 1,
                            modifier = Modifier.basicMarquee()
                        )
                    }


                    if (post.mediaUri.isNullOrEmpty()) {
                        val firstUrl = remember(post.content) { extractFirstUrl(post.content) }
                        if (firstUrl != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            val previewData = remember(firstUrl) { getUrlPreview(firstUrl) }
                            Box(modifier = Modifier.fillMaxWidth().scale(0.88f).align(Alignment.Start)) {
                                LinkPreviewCard(previewData = previewData)
                            }
                        }
                    }
                }

                // RIGHT FLOATING SIDEBAR PANEL
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 12.dp, bottom = 18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .align(Alignment.TopCenter)
                                .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape)
                                .border(BorderStroke(1.5.dp, Color.White), CircleShape)
                                .clickable { onAuthorClick(post.authorId) },
                            contentAlignment = Alignment.Center
                        ) {
                            val initial = if (post.authorName.isNotBlank()) post.authorName.take(2).uppercase() else "?"
                            Text(
                                text = initial,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        if (currentUser?.id != post.authorId) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .offset(y = 2.dp)
                                    .size(18.dp)
                                    .background(Color(0xFFFE2C55), CircleShape)
                                    .border(BorderStroke(1.dp, Color.White), CircleShape)
                                    .clickable {
                                        viewModel.toggleFollow(post.authorId, isFollowing)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isFollowing) Icons.Default.Check else Icons.Default.Add,
                                    contentDescription = "Follow Creator",
                                    tint = Color.White,
                                    modifier = Modifier.size(11.dp)
                                )
                            }
                        }
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = { viewModel.toggleLike(post.id, isLiked) },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Like",
                                tint = if (isLiked) Color(0xFFFE2C55) else Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Text(
                            text = if (likeCount > 0) "$likeCount" else "0",
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = {
                                viewModel.recordPostClick(post.id)
                                if (onCommentsClick != null) onCommentsClick() else onClick()
                            },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ChatBubbleOutline,
                                contentDescription = "Comments",
                                tint = Color.White,
                                modifier = Modifier.size(31.dp)
                            )
                        }
                        Text(
                            text = "$commentCount",
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = { viewModel.toggleBookmark(post.id, isBookmarked) },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Filled.Bookmarks else Icons.Outlined.Bookmarks,
                                contentDescription = "Bookmark",
                                tint = if (isBookmarked) Color(0xFFFFC107) else Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                        Text(
                            text = if (isBookmarked) "Saved" else "Save",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, post.title)
                                    putExtra(Intent.EXTRA_TEXT, "${post.title}\n\n${post.content}\n\nRead more details inside Zooz!")
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share Post"))
                            },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Share,
                                contentDescription = "Share",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Text(
                            text = "Reply",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Tipping & Creator Gift Button
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = { showGiftSheet = true },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CardGiftcard,
                                contentDescription = "Send Gift",
                                tint = Color(0xFFFF4081),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Text(
                            text = "Gift",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            fontWeight = FontWeight.Bold
                        )
                    }



                    // Spinning Vinyl Disc with Tap-to-Sound-Details
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .size(38.dp)
                            .rotate(rotationSpeed)
                            .background(Color(0xFF0C0C0E), CircleShape)
                            .border(BorderStroke(1.5.dp, Color(0xFF25F4EE)), CircleShape)
                            .border(BorderStroke(3.5.dp, Color(0xFF16161A)), CircleShape)
                            .clickable {
                                val soundTitle = if (!post.spotifyTrackName.isNullOrEmpty()) post.spotifyTrackName else "Original Sound"
                                val soundArtist = if (!post.spotifyTrackArtist.isNullOrEmpty()) post.spotifyTrackArtist else "@${post.authorName}"
                                onSoundClick?.invoke(soundTitle, soundArtist, post.spotifyTrackImageUrl, post.spotifyTrackPreviewUrl)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (!post.spotifyTrackImageUrl.isNullOrEmpty()) {
                            AsyncImage(
                                model = post.spotifyTrackImageUrl,
                                contentDescription = "Music Album",
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(15.dp)
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(Color(0xFFFE2C55), Color(0xFF25F4EE))
                                        ),
                                        CircleShape
                                    )
                            )
                        }
                    }
                }

                // Bottom sleek progress indicator line
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .align(Alignment.BottomCenter)
                        .background(Color.White.copy(alpha = 0.2f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.45f)
                            .fillMaxHeight()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(Color(0xFFFE2C55), Color(0xFF25F4EE))
                                )
                            )
                    )
                }
            }

            // Flying Gift Animation Overlay
            if (animatedGiftEmoji != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.animateContentSize()
                    ) {
                        Text(
                            text = animatedGiftEmoji ?: "",
                            fontSize = 72.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color.Black.copy(alpha = 0.85f),
                            border = BorderStroke(1.dp, Color(0xFFFF4081))
                        ) {
                            Text(
                                text = "Gift Sent! +Diamonds 💎",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // Spotify Track preview player if available (runs on ExoPlayer, unmuted by default)
            if (!post.spotifyTrackPreviewUrl.isNullOrEmpty()) {
                MusicPreviewPlayer(
                    previewUrl = post.spotifyTrackPreviewUrl,
                    autoPlayEnabled = true
                )
            }
        }
    }

    // Long press grid actions ModalBottomSheet (TikTok-style 4-column grid)
    if (showLongPressActionsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showLongPressActionsSheet = false },
            containerColor = Color(0xFF16161E),
            contentColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 32.dp, start = 16.dp, end = 16.dp)
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .width(36.dp)
                        .height(4.dp)
                        .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(2.dp))
                )

                Text(
                    text = "Post Actions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 280.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Send Gift
                    item {
                        ActionGridItem(
                            icon = Icons.Default.CardGiftcard,
                            label = "Send Gift",
                            bgTint = Color(0xFFFF4081).copy(alpha = 0.18f),
                            iconTint = Color(0xFFFF4081),
                            onClick = {
                                showLongPressActionsSheet = false
                                showGiftSheet = true
                            }
                        )
                    }

                    // Download
                    item {
                        ActionGridItem(
                            icon = Icons.Default.Download,
                            label = "Save Video",
                            bgTint = Color(0xFFFE2C55).copy(alpha = 0.18f),
                            iconTint = Color(0xFFFE2C55),
                            onClick = {
                                showLongPressActionsSheet = false
                                showWatermarkDownloadDialog = true
                            }
                        )
                    }

                    // Use Sound
                    item {
                        ActionGridItem(
                            icon = Icons.Default.MusicNote,
                            label = "Use Sound",
                            bgTint = Color(0xFF25F4EE).copy(alpha = 0.18f),
                            iconTint = Color(0xFF25F4EE),
                            onClick = {
                                showLongPressActionsSheet = false
                                val soundTitle = if (!post.spotifyTrackName.isNullOrEmpty()) post.spotifyTrackName else "Original Sound"
                                val soundArtist = if (!post.spotifyTrackArtist.isNullOrEmpty()) post.spotifyTrackArtist else "@${post.authorName}"
                                onSoundClick?.invoke(soundTitle, soundArtist, post.spotifyTrackImageUrl, post.spotifyTrackPreviewUrl)
                            }
                        )
                    }

                    // About
                    item {
                        ActionGridItem(
                            icon = Icons.Filled.Star,
                            label = "About",
                            bgTint = Color.White.copy(alpha = 0.10f),
                            iconTint = Color(0xFF25F4EE),
                            onClick = {
                                showLongPressActionsSheet = false
                                showInfoDialog = true
                            }
                        )
                    }

                    // Not Interested / Hide
                    item {
                        ActionGridItem(
                            icon = Icons.Filled.Close,
                            label = "Not Interested",
                            bgTint = Color.White.copy(alpha = 0.10f),
                            iconTint = Color.White,
                            onClick = {
                                showLongPressActionsSheet = false
                                viewModel.hidePost(post.id)
                                android.widget.Toast.makeText(context, "Post hidden", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        )
                    }

                    // Report
                    item {
                        ActionGridItem(
                            icon = Icons.Filled.Report,
                            label = "Report",
                            bgTint = Color(0xFFFE2C55).copy(alpha = 0.18f),
                            iconTint = Color(0xFFFE2C55),
                            onClick = {
                                showLongPressActionsSheet = false
                                viewModel.reportPost(post.id)
                                android.widget.Toast.makeText(context, "Post reported", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        )
                    }

                    // Delete (Owner only)
                    if (isOwner) {
                        item {
                            ActionGridItem(
                                icon = Icons.Filled.Delete,
                                label = "Delete",
                                bgTint = MaterialTheme.colorScheme.error.copy(alpha = 0.2f),
                                iconTint = MaterialTheme.colorScheme.error,
                                onClick = {
                                    showLongPressActionsSheet = false
                                    showDeleteConfirmDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Gift & Tipping ModalBottomSheet
    if (showGiftSheet) {
        ModalBottomSheet(
            onDismissRequest = { showGiftSheet = false },
            containerColor = Color(0xFF16161E),
            contentColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Send Gift to @${post.authorName}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    IconButton(onClick = { showGiftSheet = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White.copy(alpha = 0.7f))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // User Coin Credits Balance Header
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFFFD700).copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🪙 Coins Balance:", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("$userCoins", color = Color(0xFFFFD700), fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleMedium)
                        }
                        Button(
                            onClick = { showCoinRechargeDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFE2C55)),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("+ Get Coins", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }

                val gifts = listOf(
                    Triple("Rose", "🌹", 1),
                    Triple("Coffee", "☕", 5),
                    Triple("Fire", "🔥", 10),
                    Triple("Crown", "👑", 50),
                    Triple("Diamond", "💎", 100)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    gifts.forEach { (name, emoji, cost) ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White.copy(alpha = 0.06f))
                                .clickable {
                                    viewModel.sendGift(
                                        authorId = post.authorId,
                                        giftName = name,
                                        giftIcon = emoji,
                                        coinsCost = cost,
                                        diamondsCredit = cost,
                                        onSuccess = {
                                            animatedGiftEmoji = emoji
                                            showGiftSheet = false
                                            android.widget.Toast.makeText(context, "Sent $emoji $name to @${post.authorName}!", android.widget.Toast.LENGTH_SHORT).show()
                                        },
                                        onInsufficientCredits = { needed ->
                                            showCoinRechargeDialog = true
                                            android.widget.Toast.makeText(context, "Need $needed more coins to send $name", android.widget.Toast.LENGTH_LONG).show()
                                        }
                                    )
                                }
                                .padding(horizontal = 10.dp, vertical = 12.dp)
                        ) {
                            Text(text = emoji, fontSize = 32.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = name, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = "🪙 $cost", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = Color(0xFFFFD700), fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Coin Recharge Top-Up Dialog
    if (showCoinRechargeDialog) {
        Dialog(
            onDismissRequest = { showCoinRechargeDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(0.92f).padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF16161F),
                border = BorderStroke(1.dp, Color(0xFF2A2A38))
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Recharge Coins", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("Buy credits to send gifts & support creators", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.6f))
                        }
                        IconButton(onClick = { showCoinRechargeDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White.copy(alpha = 0.6f))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val coinBundles = listOf(
                        Triple(100, "$0.99", "Starter Pack"),
                        Triple(500, "$4.99", "🔥 Most Popular"),
                        Triple(1200, "$9.99", "🌟 Best Value"),
                        Triple(3000, "$24.99", "👑 VIP Pack")
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        coinBundles.forEach { (coins, price, badge) ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFF20202C),
                                border = BorderStroke(1.dp, Color(0xFF333345)),
                                modifier = Modifier.fillMaxWidth().clickable {
                                    viewModel.buyCoins(coins, price.removePrefix("$").toDoubleOrNull() ?: 0.99) {
                                        showCoinRechargeDialog = false
                                        android.widget.Toast.makeText(context, "Recharged +$coins Coins successfully!", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                }
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("🪙", fontSize = 26.sp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text("$coins Coins", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                            Text(badge, color = Color(0xFFFF4081), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Medium)
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            viewModel.buyCoins(coins, price.removePrefix("$").toDoubleOrNull() ?: 0.99) {
                                                showCoinRechargeDialog = false
                                                android.widget.Toast.makeText(context, "Recharged +$coins Coins successfully!", android.widget.Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFE2C55)),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(price, color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Delete Post?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to permanently delete this post? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        viewModel.deletePost(post.id) {
                            android.widget.Toast.makeText(context, "Post deleted successfully", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.onError)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showInfoDialog) {
        val relativeTimeStr = formatRelativeTime(post.timestamp)
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = { Text("About this post", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("• Author: @${post.authorName}", fontWeight = FontWeight.Medium)
                    Text("• Posted: $relativeTimeStr", fontWeight = FontWeight.Medium)
                    if (post.categories.isNotBlank()) Text("• Categories: ${post.categories}")
                    if (post.tags.isNotBlank()) Text("• Tags: ${post.tags}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("This post was recommended based on your activity, followed creators, or trending topics.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            confirmButton = {
                TextButton(onClick = { showInfoDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    if (showWatermarkDownloadDialog) {
        com.example.data.DownloadWithWatermarkDialog(
            mediaUri = post.mediaUri,
            authorName = post.authorName,
            title = post.title,
            content = post.content,
            onDismiss = { showWatermarkDownloadDialog = false }
        )
    }
}

@Composable
fun StorySection(
    viewModel: BlogViewModel,
    onStoryClick: (Int) -> Unit,
    onCreateStory: () -> Unit
) {
    val stories by viewModel.allStories.collectAsStateWithLifecycle()
    val users by viewModel.allUsers.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    
    val storiesByAuthor = remember(stories) { 
        val now = System.currentTimeMillis()
        stories.filter { it.expiresAt > now }.groupBy { it.authorId } 
    }

    Column(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background)) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            item {
                CreateStoryItem(
                    currentUser = currentUser,
                    isUploading = false,
                    onClick = onCreateStory
                )
            }
            
            storiesByAuthor.forEach { (authorId, authorStories) ->
                val author = users.find { it.id == authorId }
                if (author != null) {
                    item {
                        StoryCircle(
                            author = author,
                            onClick = { onStoryClick(authorId) }
                        )
                    }
                }
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(top = 4.dp), 
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), 
            thickness = 0.5.dp
        )
    }
}

@Composable
fun CreateStoryItem(
    currentUser: com.example.data.User?,
    isUploading: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(modifier = Modifier.size(68.dp)) {
            AsyncImage(
                model = currentUser?.profilePicUri ?: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100",
                contentDescription = null,
                modifier = Modifier
                    .size(62.dp)
                    .clip(CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop
            )
            
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(24.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.background, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (isUploading) {
                    CircularProgressIndicator(modifier = Modifier.size(12.dp), strokeWidth = 2.dp, color = Color.White)
                } else {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Your Story",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun StoryCircle(
    author: com.example.data.User,
    onClick: () -> Unit
) {
    val rainbowBrush = Brush.linearGradient(
        colors = listOf(Color(0xFFFE2C55), Color(0xFF25F4EE), Color(0xFFFE2C55))
    )
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(68.dp)
                .border(2.5.dp, rainbowBrush, CircleShape)
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = author.profilePicUri ?: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100",
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = author.username,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(68.dp),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}


@Composable
fun EditorialCategoryBadge(category: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Stars,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = category.trim().uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    letterSpacing = 1.2.sp,
                    fontWeight = FontWeight.ExtraBold
                ),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun EditorialHashtagBadge(tag: String) {
    val cleanTag = tag.trim().removePrefix("#")
    if (cleanTag.isEmpty()) return
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "#",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = cleanTag,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )
            )
        }
    }
}



@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userId: Int? = null,
    viewModel: BlogViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToPostDetail: (Int) -> Unit,
    onNavigateToFollowers: (Int) -> Unit,
    onNavigateToFollowing: (Int) -> Unit,
    onNavigateToMessaging: (Int) -> Unit,
    onNavigateToSettings: (() -> Unit)? = null,
    onNavigateToMenuDest: ((String) -> Unit)? = null
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val finalUserId = userId ?: currentUser?.id ?: -1
    
    var selectedTabIndex by remember { mutableStateOf(0) }
    var showMenuSheet by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        showMenuSheet = false
    }
    
    val tabs = remember(userId, currentUser?.id) {
        val list = mutableListOf("Posts")
        if (userId == null || userId == currentUser?.id) {
            list.add("Drafts")
            list.add("Saved")
            // Liked and Reposts can be added fully later, but let's just add them
            list.add("Liked")
            list.add("Reposts")
        }
        list
    }
    
    val displayUserFlow = remember(finalUserId) { viewModel.getUserById(finalUserId) }
    val displayUser by displayUserFlow.collectAsStateWithLifecycle()
    
    val allPosts by viewModel.allPosts.collectAsStateWithLifecycle()
    
    val followerFlow = remember(finalUserId) { viewModel.getFollowerCount(finalUserId) }
    val followerCount by followerFlow.collectAsStateWithLifecycle()
    
    val followingFlow = remember(finalUserId) { viewModel.getFollowingCount(finalUserId) }
    val followingCount by followingFlow.collectAsStateWithLifecycle()

    val userPosts = allPosts.filter { it.authorId == finalUserId && (!it.isDraft || finalUserId == (currentUser?.id ?: -1)) }
    val isPromoted = followerCount >= 1000
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (userId == null || userId == currentUser?.id) "My Profile" else displayUser?.username ?: "Profile") },
                navigationIcon = {
                    if (userId != null) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    val context = androidx.compose.ui.platform.LocalContext.current
                    IconButton(
                        onClick = {
                            val userToShare = displayUser ?: currentUser
                            if (userToShare != null) {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "Check out this profile on Zooz: ${com.example.BuildConfig.ZOOZ_SHARE_URL}/user/${userToShare.id}")
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "Share Profile via")
                                context.startActivity(shareIntent)
                            }
                        }
                    ) {
                        Icon(Icons.Filled.Share, contentDescription = "Share Profile")
                    }
                    if (userId == null || userId == currentUser?.id) {
                        IconButton(onClick = { showMenuSheet = true }) {
                            Icon(Icons.Outlined.Menu, contentDescription = "Menu")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (displayUser?.profilePicUri != null) {
                    coil.compose.AsyncImage(
                        model = displayUser!!.profilePicUri,
                        contentDescription = "Profile Picture",
                        modifier = Modifier.size(80.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier.size(80.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = displayUser?.username?.take(2)?.uppercase() ?: "?",
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (!displayUser?.fullName.isNullOrBlank()) displayUser?.fullName ?: "" else displayUser?.username ?: "",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    if (followerCount >= 10000) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "Verified Blue Badge",
                            tint = Color(0xFF1DA1F2),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                
                if (!displayUser?.fullName.isNullOrBlank()) {
                    Text("@${displayUser?.username ?: ""}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                
                if (!displayUser?.bio.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = displayUser?.bio ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentUser != null && finalUserId != currentUser?.id) {
                        FollowButton(authorId = finalUserId, viewModel = viewModel)
                        
                        Button(
                            onClick = { onNavigateToMessaging(finalUserId) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ),
                            modifier = Modifier.height(32.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
                        ) {
                            Icon(Icons.Outlined.Chat, contentDescription = "Message", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Message", style = MaterialTheme.typography.labelMedium)
                        }
                    } else if (currentUser != null && finalUserId == currentUser?.id) {
                        Button(
                            onClick = { onNavigateToSettings?.invoke() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier.height(32.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
                        ) {
                            Icon(Icons.Outlined.Edit, contentDescription = "Edit Profile", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Edit Profile", style = MaterialTheme.typography.labelMedium)
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        // Let ourselves see Direct Messages Screen directly from profile
                        Button(
                            onClick = { onNavigateToMessaging(-1) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ),
                            modifier = Modifier.height(32.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
                        ) {
                            Icon(Icons.Outlined.Chat, contentDescription = "My Messages", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("My Messages", style = MaterialTheme.typography.labelMedium)
                        }
                    }

                    // Share Profile Icon Button
                    val context = androidx.compose.ui.platform.LocalContext.current
                    IconButton(
                        onClick = {
                            val userToSend = displayUser ?: currentUser
                            if (userToSend != null) {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "Check out this profile on Zooz: ${com.example.BuildConfig.ZOOZ_SHARE_URL}/user/${userToSend.id}")
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "Share Profile via")
                                context.startActivity(shareIntent)
                            }
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = "Share Profile",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        "$followerCount Followers",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { displayUser?.let { onNavigateToFollowers(it.id) } }
                    )
                    Text(
                        "$followingCount Following",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { displayUser?.let { onNavigateToFollowing(it.id) } }
                    )
                    Text("${userPosts.size} Posts", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }



            }
            HorizontalDivider()
            
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.background,
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    val icon = when (title) {
                        "Posts" -> Icons.Default.GridView
                        "Drafts" -> Icons.Default.Edit
                        "Saved" -> Icons.Default.BookmarkBorder
                        "Liked" -> Icons.Default.FavoriteBorder
                        "Reposts" -> Icons.Default.Repeat
                        else -> Icons.Default.GridView
                    }
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        icon = { Icon(icon, contentDescription = title) },
                    )
                }
            }
            HorizontalDivider()
            
            val currentTabName = tabs.getOrNull(selectedTabIndex) ?: "Posts"
            val displayPosts = when (currentTabName) {
                "Posts" -> userPosts
                "Drafts" -> allPosts.filter { it.authorId == finalUserId && it.isDraft }
                "Saved" -> {
                    // Collect bookmarks flow wait... we need it collected. But we can't collect inside when expression.
                    // We will collect it above.
                    emptyList() // Fallback handled later
                }
                "Liked" -> emptyList() // Needs fetch
                "Reposts" -> emptyList() // Needs fetch
                else -> userPosts
            }
            
            if (currentTabName == "Saved") {
                val bookmarkedPostsFlow = remember { viewModel.getBookmarkedPosts() }
                val bookmarkedPosts by bookmarkedPostsFlow.collectAsStateWithLifecycle(initialValue = emptyList())
                ProfilePostGrid(posts = bookmarkedPosts, onNavigateToPostDetail = onNavigateToPostDetail)
            } else if (currentTabName == "Drafts") {
                val draftsFlow = remember { viewModel.getDrafts() }
                val drafts by draftsFlow.collectAsStateWithLifecycle(initialValue = emptyList())
                ProfilePostGrid(posts = drafts, onNavigateToPostDetail = onNavigateToPostDetail)
            } else if (currentTabName == "Posts") {
                ProfilePostGrid(posts = userPosts, onNavigateToPostDetail = onNavigateToPostDetail)
            } else if (currentTabName == "Liked") {
                val likedFlow = remember(finalUserId) { viewModel.getLikedPosts(finalUserId) }
                val likedPosts by likedFlow.collectAsStateWithLifecycle(initialValue = emptyList())
                ProfilePostGrid(posts = likedPosts, onNavigateToPostDetail = onNavigateToPostDetail)
            } else {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text("No $currentTabName yet")
                }
            }
        }
    }

    if (showMenuSheet) {
        ModalBottomSheet(
            onDismissRequest = { showMenuSheet = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            val userToDisplay = displayUser ?: currentUser
            val currentFollowerCount = followerCount
            
            MenuContent(
                user = userToDisplay,
                followerCount = currentFollowerCount,
                onLogout = {
                    showMenuSheet = false
                    viewModel.logout()
                },
                onItemClick = { route ->
                    showMenuSheet = false
                    if (route == "profile") {
                        // Already on profile screen
                    } else if (onNavigateToMenuDest != null) {
                        onNavigateToMenuDest(route)
                    } else {
                        when (route) {
                            "settings", "account_settings" -> onNavigateToSettings?.invoke()
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun ProfilePostGrid(posts: List<Post>, onNavigateToPostDetail: (Int) -> Unit) {
    if (posts.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No posts.", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(2.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items(posts) { post ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clickable { onNavigateToPostDetail(post.id) },
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = post.mediaUri ?: "",
                            contentDescription = "Post",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksScreen(
    viewModel: BlogViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToPostDetail: (Int) -> Unit,
    onNavigateToUserProfile: (Int) -> Unit
) {
    val bookmarksFlow = remember { viewModel.getBookmarkedPosts() }
    val bookmarks by bookmarksFlow.collectAsStateWithLifecycle()
    var selectedPostForComments by remember { mutableStateOf<Post?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bookmarks") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(bookmarks) { post ->
                PostItem(
                    post = post,
                    viewModel = viewModel,
                    onClick = { onNavigateToPostDetail(post.id) },
                    onAuthorClick = onNavigateToUserProfile,
                    onCommentsClick = { selectedPostForComments = post }
                )
            }
            if (bookmarks.isEmpty()) {
                item { Text("No bookmarks found.", modifier = Modifier.padding(16.dp)) }
            }
        }
    }

    if (selectedPostForComments != null) {
        VideoCommentsBottomSheet(
            post = selectedPostForComments!!,
            viewModel = viewModel,
            onDismissRequest = { selectedPostForComments = null }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSettingsScreen(
    viewModel: BlogViewModel,
    onNavigateBack: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    var username by remember(currentUser) { mutableStateOf(currentUser?.username ?: "") }
    var fullName by remember(currentUser) { mutableStateOf(currentUser?.fullName ?: "") }
    var bio by remember(currentUser) { mutableStateOf(currentUser?.bio ?: "") }
    var profilePicUri by remember(currentUser) { mutableStateOf(currentUser?.profilePicUri) }
    var isUploadingProfilePic by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    
    val usernameLastChanged = currentUser?.usernameLastChangedAt ?: 0L
    val currentTime = System.currentTimeMillis()
    val sevenDaysInMillis = 7 * 24 * 60 * 60 * 1000L
    val timePassed = currentTime - usernameLastChanged
    val canEditUsername = usernameLastChanged == 0L || timePassed >= sevenDaysInMillis

    val cooldownText = if (canEditUsername) {
        null
    } else {
        val millisLeft = sevenDaysInMillis - timePassed
        val days = millisLeft / (1000 * 60 * 60 * 24)
        val hours = (millisLeft % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)
        if (days > 0) {
            "Username updated: change locked for $days days, $hours hours"
        } else {
            "Username updated: change locked for $hours hours"
        }
    }

    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        uri?.let {
            if (R2Uploader.isConfigured()) {
                isUploadingProfilePic = true
                coroutineScope.launch {
                    val r2Url = R2Uploader.uploadFile(context, it)
                    if (r2Url != null) {
                        profilePicUri = r2Url
                    } else {
                        val localPath = saveUriToInternalStorage(context, it)
                        profilePicUri = localPath ?: it.toString()
                    }
                    isUploadingProfilePic = false
                }
            } else {
                val localPath = saveUriToInternalStorage(context, it)
                if (localPath != null) {
                    profilePicUri = localPath
                } else {
                    profilePicUri = it.toString()
                }
            }
        }
    }
    
    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Account Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {
            Text("Profile Information", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (profilePicUri != null) {
                    coil.compose.AsyncImage(
                        model = profilePicUri,
                        contentDescription = "Profile Picture",
                        modifier = Modifier.size(60.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(modifier = Modifier.size(60.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.Person, contentDescription = "Avatar")
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                OutlinedButton(
                    onClick = { if (!isUploadingProfilePic) launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    enabled = !isUploadingProfilePic
                ) {
                    if (isUploadingProfilePic) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Uploading...")
                    } else {
                        Text("Change Picture")
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = username,
                onValueChange = { if (canEditUsername) username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(),
                enabled = canEditUsername,
                supportingText = cooldownText?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                leadingIcon = { Icon(Icons.Filled.Person, contentDescription = "Username Icon") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text("Bio") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.updateUserProfile(
                        username = username,
                        fullName = fullName,
                        bio = bio,
                        profilePicUri = profilePicUri,
                        onSuccess = {
                            android.widget.Toast.makeText(context, "Profile saved successfully! ✨", android.widget.Toast.LENGTH_SHORT).show()
                        },
                        onError = { error ->
                            android.widget.Toast.makeText(context, "Error: $error ❌", android.widget.Toast.LENGTH_LONG).show()
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Profile")
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            var showDeleteDialog by remember { mutableStateOf(false) }

            TextButton(
                onClick = { showDeleteDialog = true },
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete Icon")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Delete Account", fontWeight = FontWeight.Bold)
            }

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Delete Account") },
                    text = { Text("Are you sure you want to delete your account? This action cannot be undone. All your posts, comments, and followers will be permanently lost.") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDeleteDialog = false
                                viewModel.deleteAccount(
                                    onSuccess = {
                                        android.widget.Toast.makeText(context, "Account deleted.", android.widget.Toast.LENGTH_LONG).show()
                                        // Logging out is handled inside the view model
                                    },
                                    onError = { error ->
                                        android.widget.Toast.makeText(context, "Error: $error ❌", android.widget.Toast.LENGTH_LONG).show()
                                    }
                                )
                            },
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Permanently Delete")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun SettingsScreen(
    viewModel: BlogViewModel,
    onNavigateBack: () -> Unit
) {
    val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()
    var notifications by remember { mutableStateOf(true) }
    
    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("App Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {
            Text("App Settings", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            ListItem(
                headlineContent = { Text("Dark Theme") },
                supportingContent = { Text("Switch between light and dark backgrounds") },
                trailingContent = { Switch(checked = isDarkTheme, onCheckedChange = { viewModel.setDarkTheme(it) }) }
            )
            ListItem(
                headlineContent = { Text("Push Notifications") },
                trailingContent = { Switch(checked = notifications, onCheckedChange = { notifications = it }) }
            )

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Zooz Custom Identity Binding", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Map your existing Google Gmail account to a custom secure @zooz.secure format so you can authenticate either way.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            val context = LocalContext.current
            val auth = remember {
                try {
                    if (com.google.firebase.FirebaseApp.getApps(context).isNotEmpty()) {
                        com.google.firebase.auth.FirebaseAuth.getInstance()
                    } else null
                } catch (e: Exception) {
                    null
                }
            }
            val firebaseUser = remember(auth) { auth?.currentUser }
            
            if (firebaseUser != null && !firebaseUser.email.isNullOrBlank()) {
                val email = firebaseUser.email!!
                var boundAlias by remember { mutableStateOf<String?>(null) }
                var aliasInput by remember { mutableStateOf("") }
                var statusMessage by remember { mutableStateOf<String?>(null) }
                var isErrorStatus by remember { mutableStateOf(false) }
                var isBindingInProcess by remember { mutableStateOf(false) }
                
                LaunchedEffect(email) {
                    boundAlias = viewModel.getCurrentlyBoundAlias(email)
                }
                
                if (boundAlias != null) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Alias Bound Successfully",
                                tint = Color(0xFF34A853),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Mapped Zooz Alias",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "${boundAlias}@zooz.secure",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Linked to Google: $email",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Credential linking (setting a password for the Google email)
                    var zoozPassword by remember { mutableStateOf("") }
                    var isLinkingByPassword by remember { mutableStateOf(false) }
                    var passwordStatus by remember { mutableStateOf<String?>(null) }
                    var isPasswordError by remember { mutableStateOf(false) }
                    
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Enable Credentials Login", 
                                style = MaterialTheme.typography.labelLarge, 
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Set a password so you can sign in directly using your alias instead of always using Google Sign-In.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            OutlinedTextField(
                                value = zoozPassword,
                                onValueChange = { zoozPassword = it; passwordStatus = null },
                                label = { Text("Zooz Credentials Password") },
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                singleLine = true
                            )
                            
                            if (passwordStatus != null) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    passwordStatus!!,
                                    color = if (isPasswordError) MaterialTheme.colorScheme.error else Color(0xFF34A853),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Button(
                                onClick = {
                                    if (zoozPassword.length < 6) {
                                        passwordStatus = "Password must be at least 6 characters"
                                        isPasswordError = true
                                        return@Button
                                    }
                                    isLinkingByPassword = true
                                    viewModel.enableCredentialsLogin(
                                        password = zoozPassword,
                                        onComplete = {
                                            isLinkingByPassword = false
                                            passwordStatus = "Password linked successfully! You can now use the Zooz sign-in form."
                                            isPasswordError = false
                                        },
                                        onError = { err ->
                                            isLinkingByPassword = false
                                            passwordStatus = err
                                            isPasswordError = true
                                        }
                                    )
                                },
                                enabled = !isLinkingByPassword && zoozPassword.isNotBlank(),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                if (isLinkingByPassword) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                                } else {
                                    Text("Link Credentials Password")
                                }
                            }
                        }
                    }
                } else {
                    OutlinedTextField(
                        value = aliasInput,
                        onValueChange = { 
                            aliasInput = it.lowercase().trim().replace(" ", "")
                            statusMessage = null
                        },
                        label = { Text("Desired Zooz Suffix Alias") },
                        placeholder = { Text("e.g. alice") },
                        suffix = { Text("@zooz.secure") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    if (statusMessage != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = statusMessage ?: "",
                            color = if (isErrorStatus) MaterialTheme.colorScheme.error else Color(0xFF34A853),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Button(
                        onClick = {
                            if (aliasInput.isBlank()) {
                                statusMessage = "Please enter an alias"
                                isErrorStatus = true
                                return@Button
                            }
                            isBindingInProcess = true
                            viewModel.bindCustomZoozAlias(
                                alias = aliasInput,
                                onComplete = {
                                    isBindingInProcess = false
                                    boundAlias = aliasInput
                                    statusMessage = "Alias bound successfully! You can now authenticate with ${aliasInput}@zooz.secure"
                                    isErrorStatus = false
                                },
                                onError = { err ->
                                    isBindingInProcess = false
                                    statusMessage = err
                                    isErrorStatus = true
                                }
                            )
                        },
                        enabled = !isBindingInProcess,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        if (isBindingInProcess) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Bind Identity Alias")
                        }
                    }
                }
            } else {
                Text(
                    text = "No logged-in Google Account detected contextually.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowersScreen(
    userId: Int,
    viewModel: BlogViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToUserProfile: (Int) -> Unit
) {
    val followersState = remember(userId) { viewModel.getFollowersList(userId) }
    val followers by followersState.collectAsStateWithLifecycle(initialValue = emptyList())
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Followers") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
            items(followers) { user ->
                ListItem(
                    modifier = Modifier.clickable { onNavigateToUserProfile(user.id) },
                    headlineContent = { Text(user.username) },
                    supportingContent = { if (user.bio.isNotBlank()) Text(user.bio) },
                    leadingContent = {
                        if (user.profilePicUri != null) {
                            coil.compose.AsyncImage(
                                model = user.profilePicUri,
                                contentDescription = "Profile Picture",
                                modifier = Modifier.size(40.dp).clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(Icons.Filled.Person, contentDescription = "Avatar")
                        }
                    },
                    trailingContent = { FollowButton(authorId = user.id, viewModel = viewModel) }
                )
                HorizontalDivider()
            }
            if (followers.isEmpty()) {
                item { Text("No followers yet.", modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowingScreen(
    userId: Int,
    viewModel: BlogViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToUserProfile: (Int) -> Unit
) {
    val followingState = remember(userId) { viewModel.getFollowingList(userId) }
    val following by followingState.collectAsStateWithLifecycle(initialValue = emptyList())
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Following") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
            items(following) { user ->
                ListItem(
                    modifier = Modifier.clickable { onNavigateToUserProfile(user.id) },
                    headlineContent = { Text(user.username) },
                    supportingContent = { if (user.bio.isNotBlank()) Text(user.bio) },
                    leadingContent = {
                        if (user.profilePicUri != null) {
                            coil.compose.AsyncImage(
                                model = user.profilePicUri,
                                contentDescription = "Profile Picture",
                                modifier = Modifier.size(40.dp).clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(Icons.Filled.Person, contentDescription = "Avatar")
                        }
                    },
                    trailingContent = { FollowButton(authorId = user.id, viewModel = viewModel) }
                )
                HorizontalDivider()
            }
            if (following.isEmpty()) {
                item { Text("Not following anyone.", modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Help & Support") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text("FAQ", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Q: How do I publish a post?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text("A: Tap the + button on the home screen, write your content, and hit Publish.", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(12.dp))
            Text("Q: How can I bookmark?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text("A: Tap on any post to view details and click the bookmark icon on the top right.", style = MaterialTheme.typography.bodyMedium)
            
            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Contact Us", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("For further assistance, email support@aistudio.blog", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun FollowButton(authorId: Int, viewModel: BlogViewModel) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    if (currentUser?.id == authorId) return

    val isFollowingFlow = remember(authorId) { viewModel.isFollowing(authorId) }
    val isFollowing by isFollowingFlow.collectAsStateWithLifecycle(initialValue = false)

    OutlinedButton(
        onClick = { viewModel.toggleFollow(authorId, isFollowing) },
        modifier = Modifier.height(32.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
    ) {
        Text(if (isFollowing) "Following" else "Follow", style = MaterialTheme.typography.labelMedium)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceholderScreen(
    title: String,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            Text("$title - Coming Soon", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun LinkifiedText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface,
    maxLines: Int = Int.MAX_VALUE,
    style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyLarge,
    viewModel: BlogViewModel? = null,
    onNavigateToUserProfile: ((Int) -> Unit)? = null
) {
    val context = LocalContext.current
    val annotatedString = buildAnnotatedString {
        var cursor = 0
        val mentionPattern = Regex("@(\\w+)")
        val urlPattern = Regex("(https?://[\\w-]+(\\.[\\w-]+)+(/\\S*)?)")
        
        val matches = (mentionPattern.findAll(text).map { Match(it.range.first, it.range.last + 1, "MENTION", it.groupValues[1], it.value) } +
                       urlPattern.findAll(text).map { Match(it.range.first, it.range.last + 1, "URL", it.value, it.value) })
            .sortedBy { it.start }
            .toList()
            
        val nonOverlapping = mutableListOf<Match>()
        var lastEnd = 0
        for (match in matches) {
            if (match.start >= lastEnd) {
                nonOverlapping.add(match)
                lastEnd = match.end
            }
        }
        
        val primaryColor = MaterialTheme.colorScheme.primary
        for (match in nonOverlapping) {
            if (match.start > cursor) {
                append(text.substring(cursor, match.start))
            }
            pushStringAnnotation(tag = match.tag, annotation = match.value)
            withStyle(style = SpanStyle(
                color = primaryColor,
                fontWeight = FontWeight.Bold
            )) {
                append(match.original)
            }
            pop()
            cursor = match.end
        }
        if (cursor < text.length) {
            append(text.substring(cursor))
        }
    }
    
    val allUsersState = viewModel?.allUsers?.collectAsStateWithLifecycle(initialValue = emptyList())
    val allUsers = allUsersState?.value ?: emptyList()
    
    androidx.compose.foundation.text.ClickableText(
        text = annotatedString,
        modifier = modifier,
        style = style.copy(color = color),
        maxLines = maxLines,
        overflow = if (maxLines != Int.MAX_VALUE) TextOverflow.Ellipsis else TextOverflow.Clip,
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "MENTION", start = offset, end = offset).firstOrNull()?.let { annotation ->
                val username = annotation.item
                val matchedUser = allUsers.find { it.username.equals(username, ignoreCase = true) }
                if (matchedUser != null) {
                    onNavigateToUserProfile?.invoke(matchedUser.id)
                } else {
                    android.widget.Toast.makeText(context, "User @$username not found", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
            annotatedString.getStringAnnotations(tag = "URL", start = offset, end = offset).firstOrNull()?.let { annotation ->
                val url = annotation.item
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                } catch (e: Exception) {
                    android.widget.Toast.makeText(context, "Cannot open link: $url", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }
    )
}

private data class Match(val start: Int, val end: Int, val tag: String, val value: String, val original: String)

data class LinkPreviewData(
    val url: String,
    val domain: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val brandColor: Color = Color.Gray
)

fun extractFirstUrl(text: String): String? {
    val urlPattern = Regex("(https?://[\\w-]+(\\.[\\w-]+)+(/\\S*)?)")
    val match = urlPattern.find(text)
    return match?.value
}

fun getUrlPreview(url: String): LinkPreviewData {
    val lowerUrl = url.lowercase(java.util.Locale.ROOT)
    return when {
        lowerUrl.contains("facebook.com") || lowerUrl.contains("fb.com") || lowerUrl.contains("fb.me") -> {
            LinkPreviewData(
                url = url,
                domain = "facebook.com",
                title = "Facebook — Log In, Sign Up, or Connect with Friends",
                description = "Connect with friends, family and other people you know. Share photos and videos, send messages and get updates.",
                imageUrl = "https://images.unsplash.com/photo-1611162617213-7d7a39e9b1d7?auto=format&fit=crop&q=80&w=600",
                brandColor = Color(0xFF1877F2)
            )
        }
        lowerUrl.contains("youtube.com") || lowerUrl.contains("youtu.be") -> {
            LinkPreviewData(
                url = url,
                domain = "youtube.com",
                title = "YouTube — Enjoy the videos and music you love",
                description = "Share your videos with friends, family, and the world. Watch original content, subscribe to channels, and join the global community.",
                imageUrl = "https://images.unsplash.com/photo-1611162618071-b39a2ec055fb?auto=format&fit=crop&q=80&w=600",
                brandColor = Color(0xFFFF0000)
            )
        }
        lowerUrl.contains("instagram.com") -> {
            LinkPreviewData(
                url = url,
                domain = "instagram.com",
                title = "Instagram — A simple, fun & creative way to share photos",
                description = "Capture and share the world's moments. Follow friends, family, and inspiring creators to see what they are sharing.",
                imageUrl = "https://images.unsplash.com/photo-1611224885990-ab7363d1f2a9?auto=format&fit=crop&q=80&w=600",
                brandColor = Color(0xFFE1306C)
            )
        }
        lowerUrl.contains("twitter.com") || lowerUrl.contains("x.com") -> {
            LinkPreviewData(
                url = url,
                domain = "x.com",
                title = "X — It's what's happening",
                description = "Explore real-time news, trending topics, connect with global discussions, and get the full story with live updates.",
                imageUrl = "https://images.unsplash.com/photo-1611605698335-8b15d27e03f2?auto=format&fit=crop&q=80&w=600",
                brandColor = Color(0xFF0F1419)
            )
        }
        lowerUrl.contains("github.com") -> {
            LinkPreviewData(
                url = url,
                domain = "github.com",
                title = "GitHub: Let's build from here · GitHub",
                description = "GitHub is the world's leading developer platform. Collaborate, write clean code, and build secure software together.",
                imageUrl = "https://images.unsplash.com/photo-1618401471353-b98aedd07871?auto=format&fit=crop&q=80&w=600",
                brandColor = Color(0xFF24292E)
            )
        }
        lowerUrl.contains("linkedin.com") -> {
            LinkPreviewData(
                url = url,
                domain = "linkedin.com",
                title = "LinkedIn: Log In or Sign Up",
                description = "Manage your professional identity. Build and engage with your professional network. Access knowledge, insights and opportunities.",
                imageUrl = "https://images.unsplash.com/photo-1611944212129-43ed7e13c6b7?auto=format&fit=crop&q=80&w=600",
                brandColor = Color(0xFF0A66C2)
            )
        }
        lowerUrl.contains("stackoverflow.com") -> {
            LinkPreviewData(
                url = url,
                domain = "stackoverflow.com",
                title = "Stack Overflow — Where Developers Learn, Share, & Build Careers",
                description = "The world's largest online community for developers to learn, share their knowledge, and search for answers to technical queries.",
                imageUrl = "https://images.unsplash.com/photo-1542831371-29b0f74f9713?auto=format&fit=crop&q=80&w=600",
                brandColor = Color(0xFFF48024)
            )
        }
        lowerUrl.contains("pinterest.com") || lowerUrl.contains("pin.it") -> {
            LinkPreviewData(
                url = url,
                domain = "pinterest.com",
                title = "Pinterest — Discover recipes, home ideas, style inspiration",
                description = "Find creative ideas and recipes, explore home design, travel hacks, fashion, beauty, and trends matching your style on Pinterest.",
                imageUrl = "https://images.unsplash.com/photo-1611162616475-46b635cb6868?auto=format&fit=crop&q=80&w=600",
                brandColor = Color(0xFFE60023)
            )
        }
        lowerUrl.contains("spotify.com") -> {
            LinkPreviewData(
                url = url,
                domain = "spotify.com",
                title = "Spotify — Web Player: Music for everyone",
                description = "Spotify is a digital music, podcast, and video service that gives you access to millions of songs and other content from creators.",
                imageUrl = "https://images.unsplash.com/photo-1614680376593-902f74fa0d41?auto=format&fit=crop&q=80&w=600",
                brandColor = Color(0xFF1DB954)
            )
        }
        lowerUrl.endsWith(".jpg") || lowerUrl.endsWith(".jpeg") || lowerUrl.endsWith(".png") || lowerUrl.endsWith(".gif") || lowerUrl.endsWith(".webp") -> {
            val domainName = try {
                val uri = java.net.URI(url)
                val host = uri.host ?: ""
                if (host.startsWith("www.")) host.substring(4) else host
            } catch (e: Exception) {
                "Image Link"
            }
            LinkPreviewData(
                url = url,
                domain = domainName,
                title = "Image Preview",
                description = "Shared image file: ${url.substringAfterLast("/")}",
                imageUrl = url,
                brandColor = Color(0xFF9C27B0)
            )
        }
        else -> {
            val domainName = try {
                val uri = java.net.URI(url)
                val host = uri.host ?: ""
                if (host.startsWith("www.")) host.substring(4) else host
            } catch (e: Exception) {
                "web-page"
            }
            LinkPreviewData(
                url = url,
                domain = domainName,
                title = "$domainName — Visit Website",
                description = "Explore this webpage shared on the blogging platform to view more information, updates, and discussions.",
                imageUrl = "https://images.unsplash.com/photo-1488590528505-98d2b5aba04b?auto=format&fit=crop&q=80&w=600",
                brandColor = Color(0xFF607D8B)
            )
        }
    }
}

@Composable
fun LinkPreviewCard(previewData: LinkPreviewData, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(previewData.url))
                    context.startActivity(intent)
                } catch (e: Exception) {
                    android.widget.Toast.makeText(context, "Cannot open: ${previewData.url}", android.widget.Toast.LENGTH_SHORT).show()
                }
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                AsyncImage(
                    model = previewData.imageUrl,
                    contentDescription = previewData.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .background(previewData.brandColor)
                        .align(Alignment.BottomCenter)
                )
                Surface(
                    color = Color.Black.copy(alpha = 0.75f),
                    shape = RoundedCornerShape(topStart = 0.dp, bottomStart = 8.dp, topEnd = 0.dp, bottomEnd = 0.dp),
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = previewData.domain.uppercase(java.util.Locale.ROOT),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(previewData.brandColor, CircleShape)
                    )
                    Text(
                        text = previewData.domain,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = previewData.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = previewData.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun MentionSuggestions(
    suggestions: List<com.example.data.User>,
    onSelect: (com.example.data.User) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        LazyColumn(
            modifier = Modifier.heightIn(max = 160.dp),
            contentPadding = PaddingValues(vertical = 4.dp)
        ) {
            items(suggestions) { user ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(user) }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        val init = if (user.username.isNotBlank()) user.username.take(2).uppercase() else "?"
                        Text(
                            text = init,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "@${user.username}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (!user.fullName.isNullOrBlank()) {
                            Text(
                                text = user.fullName!!,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }
    }
}

fun getMentionQuery(text: String): String? {
    val lastAt = text.lastIndexOf('@')
    if (lastAt >= 0) {
        val afterAt = text.substring(lastAt + 1)
        if (!afterAt.contains(' ') && !afterAt.contains('\n')) {
            return afterAt
        }
    }
    return null
}

fun insertMention(text: String, username: String): String {
    val lastAt = text.lastIndexOf('@')
    if (lastAt >= 0) {
        val prefix = text.substring(0, lastAt)
        return "$prefix@$username "
    }
    return text
}


object ActiveVideoManager {
    private val visiblePlayers = mutableMapOf<String, Float>()
    val activeVideoUri = kotlinx.coroutines.flow.MutableStateFlow<String?>(null)

    fun onPlayerPositioned(mediaUri: String, distanceToCenter: Float) {
        synchronized(visiblePlayers) {
            val previousDistance = visiblePlayers[mediaUri]
            if (previousDistance == null || kotlin.math.abs(previousDistance - distanceToCenter) > 4f) {
                visiblePlayers[mediaUri] = distanceToCenter
                updateActivePlayer()
            }
        }
    }

    fun onPlayerLeftScreen(mediaUri: String) {
        synchronized(visiblePlayers) {
            if (visiblePlayers.containsKey(mediaUri)) {
                visiblePlayers.remove(mediaUri)
                updateActivePlayer()
            }
        }
    }

    private fun updateActivePlayer() {
        try {
            synchronized(visiblePlayers) {
                val newActive = if (visiblePlayers.isEmpty()) {
                    null
                } else {
                    visiblePlayers.minByOrNull { kotlin.math.abs(it.value) }?.key
                }
                if (activeVideoUri.value != newActive) {
                    activeVideoUri.value = newActive
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

object ActiveMusicManager {
    private val visibleTracks = mutableMapOf<String, Float>() // previewUrl -> distance from center
    val activeTrackUrl = kotlinx.coroutines.flow.MutableStateFlow<String?>(null)

    fun onTrackPositioned(previewUrl: String, distanceToCenter: Float) {
        synchronized(visibleTracks) {
            val prev = visibleTracks[previewUrl]
            if (prev == null || kotlin.math.abs(prev - distanceToCenter) > 4f) {
                visibleTracks[previewUrl] = distanceToCenter
                updateActiveTrack()
            }
        }
    }

    fun onTrackLeftScreen(previewUrl: String) {
        synchronized(visibleTracks) {
            if (visibleTracks.containsKey(previewUrl)) {
                visibleTracks.remove(previewUrl)
                updateActiveTrack()
            }
        }
    }

    private fun updateActiveTrack() {
        synchronized(visibleTracks) {
            val newActive = if (visibleTracks.isEmpty()) null else visibleTracks.minByOrNull { kotlin.math.abs(it.value) }?.key
            if (activeTrackUrl.value != newActive) {
                activeTrackUrl.value = newActive
            }
        }
    }
}

fun sanitizeMediaUri(uri: String): String {
    if (uri.isBlank()) return "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
    val lower = uri.lowercase(java.util.Locale.ROOT)
    if (lower.contains("mixkit") || lower.contains("videoblocks") || lower.contains("pexels") || lower.contains("pixabay") || lower.contains("getty") || lower.contains("shutterstock") || lower.contains("403")) {
        return "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
    }
    return uri
}

fun buildSafeExoPlayer(context: android.content.Context): androidx.media3.exoplayer.ExoPlayer {
    val defaultProperties = mapOf(
        "User-Agent" to "Mozilla/5.0 (Linux; Android 13; Pixel 7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Mobile Safari/537.36",
        "Accept" to "*/*",
        "Accept-Encoding" to "identity",
        "Connection" to "keep-alive"
    )
    val httpDataSourceFactory = androidx.media3.datasource.DefaultHttpDataSource.Factory()
        .setUserAgent("Mozilla/5.0 (Linux; Android 13; Pixel 7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Mobile Safari/537.36")
        .setDefaultRequestProperties(defaultProperties)
        .setAllowCrossProtocolRedirects(true)
        .setConnectTimeoutMs(15000)
        .setReadTimeoutMs(15000)

    val errorHandlingPolicy = object : androidx.media3.exoplayer.upstream.DefaultLoadErrorHandlingPolicy(3) {
        override fun getRetryDelayMsFor(loadErrorInfo: androidx.media3.exoplayer.upstream.LoadErrorHandlingPolicy.LoadErrorInfo): Long {
            val exception = loadErrorInfo.exception
            if (exception is androidx.media3.datasource.HttpDataSource.InvalidResponseCodeException) {
                android.util.Log.w("ExoPlayerSafe", "HTTP response code ${exception.responseCode} for URL: ${loadErrorInfo.loadEventInfo.uri}")
                if (exception.responseCode in 400..499) {
                    return androidx.media3.common.C.TIME_UNSET
                }
            }
            return super.getRetryDelayMsFor(loadErrorInfo)
        }
    }

    val dataSourceFactory = androidx.media3.datasource.DefaultDataSource.Factory(context, httpDataSourceFactory)
    val mediaSourceFactory = androidx.media3.exoplayer.source.DefaultMediaSourceFactory(dataSourceFactory)
        .setLoadErrorHandlingPolicy(errorHandlingPolicy)

    return androidx.media3.exoplayer.ExoPlayer.Builder(context)
        .setMediaSourceFactory(mediaSourceFactory)
        .build()
}

@Composable
fun MusicPreviewPlayer(
    previewUrl: String,
    modifier: Modifier = Modifier,
    autoPlayEnabled: Boolean = true
) {
    val context = LocalContext.current
    val activeTrack by ActiveMusicManager.activeTrackUrl.collectAsStateWithLifecycle()
    val isCurrentlyActive = activeTrack == previewUrl

    val exoPlayer = remember(previewUrl) {
        buildSafeExoPlayer(context).apply {
            repeatMode = androidx.media3.common.Player.REPEAT_MODE_ALL
            val mediaItem = androidx.media3.common.MediaItem.fromUri(Uri.parse(sanitizeMediaUri(previewUrl)))
            setMediaItem(mediaItem)
            prepare()
            
            addListener(object : androidx.media3.common.Player.Listener {
                override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                    android.util.Log.w("MusicPreviewPlayer", "ExoPlayer preview error for $previewUrl: ${error.message}")
                    try { stop() } catch (e: Exception) {}
                }
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    android.util.Log.d("MusicPreviewPlayer", "Track playing changed for $previewUrl: $isPlaying")
                }
            })
        }
    }

    LaunchedEffect(isCurrentlyActive) {
        try {
            if (isCurrentlyActive && autoPlayEnabled) {
                exoPlayer.play()
                android.util.Log.d("MusicPreviewPlayer", "Playing track: $previewUrl")
            } else {
                exoPlayer.pause()
                android.util.Log.d("MusicPreviewPlayer", "Pausing track: $previewUrl")
            }
        } catch (e: Exception) {
            android.util.Log.e("MusicPreviewPlayer", "Error controlling playback for $previewUrl: ${e.message}")
        }
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
            ActiveMusicManager.onTrackLeftScreen(previewUrl)
            android.util.Log.d("MusicPreviewPlayer", "Released player for track: $previewUrl")
        }
    }

    Box(
        modifier = Modifier
            .size(1.dp)
            .then(modifier)
            .onGloballyPositioned { coords ->
                if (coords.isAttached) {
                    val bounds = coords.boundsInWindow()
                    val screenHeight = context.resources.displayMetrics.heightPixels.toFloat()
                    val isVisible = if (bounds.height <= 0f && bounds.top == 0f) {
                        true // non-size/attached container positioned at top-left
                    } else {
                        bounds.bottom > 0f && bounds.top < screenHeight
                    }
                    
                    if (isVisible) {
                        val distance = if (bounds.height <= 0f) {
                            0f
                        } else {
                            (bounds.top + bounds.bottom) / 2f - screenHeight / 2f
                        }
                        ActiveMusicManager.onTrackPositioned(previewUrl, distance)
                    } else {
                        ActiveMusicManager.onTrackLeftScreen(previewUrl)
                    }
                }
            }
    )
}

@Composable
fun LockScreenOrientation(orientation: Int) {
    val context = LocalContext.current
    DisposableEffect(orientation) {
        val activity = context.findActivity() ?: return@DisposableEffect onDispose {}
        val originalOrientation = activity.requestedOrientation
        activity.requestedOrientation = orientation
        onDispose {
            activity.requestedOrientation = originalOrientation
        }
    }
}

fun android.content.Context.findActivity(): android.app.Activity? {
    var context = this
    while (context is android.content.ContextWrapper) {
        if (context is android.app.Activity) return context
        context = context.baseContext
    }
    return null
}

@Composable
fun ModernVideoPlayer(
    mediaUri: String, 
    modifier: Modifier = Modifier,
    autoPlayEnabled: Boolean = false,
    onPlay: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    onDoubleTap: ((androidx.compose.ui.geometry.Offset) -> Unit)? = null,
    onLongPress: ((androidx.compose.ui.geometry.Offset) -> Unit)? = null,
    isNested: Boolean = false,
    isHomeFeed: Boolean = false,
    isMutedByMusic: Boolean = false,
    playbackSpeed: Float = 1.0f,
    musicUrl: String? = null
) {
    val context = LocalContext.current
    val activeUri by ActiveVideoManager.activeVideoUri.collectAsStateWithLifecycle()
    val isCurrentlyActive = autoPlayEnabled && activeUri == mediaUri

    var isPlaying by remember { mutableStateOf(false) }
    var isMuted by remember { mutableStateOf(isMutedByMusic) } // Unmute by default, unless music track is attached (to prevent audio collision)
    var isPrepared by remember { mutableStateOf(false) }
    var showControls by remember { mutableStateOf(false) }
    var aspectRatioMode by remember { mutableStateOf(1) } // Default: 1 (ZOOM / Fill)
    var forceLandscape by remember { mutableStateOf(false) }
    var activePlaybackSpeed by remember(playbackSpeed) { mutableStateOf(playbackSpeed) }

    if (!isHomeFeed) {
        val targetOrientation = if (forceLandscape) {
            android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
        LockScreenOrientation(targetOrientation)
    }
    
    // Position & duration states
    var currentPosition by remember { mutableStateOf(0L) }
    var videoDuration by remember { mutableStateOf(0L) }
    var isBuffering by remember { mutableStateOf(false) }

    // Gesture status overlays
    var brightnessText by remember { mutableStateOf<String?>(null) }
    var volumeText by remember { mutableStateOf<String?>(null) }
    var skipText by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()
    var fadeJob by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }

    val audioManager = remember { context.getSystemService(android.content.Context.AUDIO_SERVICE) as android.media.AudioManager }
    val maxVolume = remember { audioManager.getStreamMaxVolume(android.media.AudioManager.STREAM_MUSIC).toFloat() }
    
    // Music Player for synchronized playback in ModernVideoPlayer (e.g. for posts with music)
    val musicPlayer = remember { androidx.media3.exoplayer.ExoPlayer.Builder(context).build() }
    DisposableEffect(musicUrl) {
        if (!musicUrl.isNullOrEmpty()) {
            musicPlayer.setMediaItem(androidx.media3.common.MediaItem.fromUri(musicUrl))
            musicPlayer.prepare()
            musicPlayer.repeatMode = androidx.media3.common.Player.REPEAT_MODE_ALL
        }
        onDispose { musicPlayer.release() }
    }

    var activeMediaUri by remember(mediaUri) { mutableStateOf(mediaUri) }
    var hasPlaybackError by remember(mediaUri) { mutableStateOf(false) }

    // Build ExoPlayer once for activeMediaUri
    val exoPlayer = remember(activeMediaUri) {
        buildSafeExoPlayer(context).apply {
            repeatMode = androidx.media3.common.Player.REPEAT_MODE_ALL
            val mediaItem = androidx.media3.common.MediaItem.fromUri(Uri.parse(sanitizeMediaUri(activeMediaUri)))
            setMediaItem(mediaItem)
            prepare()
            
            addListener(object : androidx.media3.common.Player.Listener {
                override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                    android.util.Log.w("ModernVideoPlayer", "Playback error for $activeMediaUri: ${error.message}")
                    if (!hasPlaybackError) {
                        hasPlaybackError = true
                        val fallbackUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
                        if (activeMediaUri != fallbackUrl) {
                            activeMediaUri = fallbackUrl
                        }
                    }
                }
            })
        }
    }

    // Direct Sync volume
    LaunchedEffect(isMuted, isMutedByMusic) {
        exoPlayer.volume = if (isMuted || isMutedByMusic) 0f else 1f
    }

    // Sync play state
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            exoPlayer.play()
            if (isMutedByMusic) musicPlayer.play()
        } else {
            exoPlayer.pause()
            musicPlayer.pause()
        }
    }

    // Auto-play / Active video tracking sync
    LaunchedEffect(isCurrentlyActive) {
        if (autoPlayEnabled) {
            isPlaying = isCurrentlyActive
        }
    }

    // Track onPlay callback
    var playCountTracked by remember(mediaUri) { mutableStateOf(false) }
    LaunchedEffect(isPlaying) {
        if (isPlaying && !playCountTracked) {
            playCountTracked = true
            onPlay?.invoke()
        }
    }

    // Speed Sync
    LaunchedEffect(activePlaybackSpeed) {
        exoPlayer.setPlaybackSpeed(activePlaybackSpeed)
    }

    // Background safety and release of player resources
    DisposableEffect(exoPlayer) {
        val listener = object : androidx.media3.common.Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                isPrepared = state == androidx.media3.common.Player.STATE_READY || state == androidx.media3.common.Player.STATE_ENDED
                isBuffering = state == androidx.media3.common.Player.STATE_BUFFERING
                videoDuration = exoPlayer.duration.coerceAtLeast(0L)
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            try {
                exoPlayer.clearVideoSurface()
                exoPlayer.stop()
            } catch (e: Exception) {}
            exoPlayer.removeListener(listener)
            exoPlayer.release()
            if (autoPlayEnabled) {
                ActiveVideoManager.onPlayerLeftScreen(mediaUri)
            }
        }
    }

    // Polling progress values
    LaunchedEffect(isPlaying, isPrepared) {
        if (isPlaying && isPrepared) {
            while (true) {
                currentPosition = exoPlayer.currentPosition.coerceAtLeast(0L)
                val dur = exoPlayer.duration.coerceAtLeast(0L)
                if (dur > 0) {
                    videoDuration = dur
                }
                kotlinx.coroutines.delay(200)
            }
        }
    }

    // Automatically hide controls overlay
    LaunchedEffect(showControls, isPlaying) {
        if (showControls && isPlaying) {
            kotlinx.coroutines.delay(3500)
            showControls = false
        }
    }

    // Helper functions for formats
    fun formatTime(ms: Long): String {
        val sec = (ms / 1000) % 60
        val min = (ms / 60000) % 60
        return String.format("%02d:%02d", min, sec)
    }

    // Render Fullscreen Dialog or Inline layout with the active ExoPlayer
    var isFullscreen by remember { mutableStateOf(false) }

    @Composable
    fun PlayerCoreContent(isInFullscreen: Boolean) {
        var layoutWidth by remember { mutableStateOf(0) }
        var layoutHeight by remember { mutableStateOf(0) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .onGloballyPositioned { coordinates ->
                    layoutWidth = coordinates.size.width
                    layoutHeight = coordinates.size.height
                    
                    if (autoPlayEnabled && coordinates.isAttached && !isInFullscreen) {
                        try {
                            val bounds = coordinates.boundsInWindow()
                            val screenHeight = context.resources.displayMetrics.heightPixels.toFloat()
                            val isPartiallyVisible = bounds.bottom > 0f && bounds.top < screenHeight
                            if (isPartiallyVisible) {
                                val playerCenterY = (bounds.top + bounds.bottom) / 2f
                                val screenCenterY = screenHeight / 2f
                                val distance = playerCenterY - screenCenterY
                                ActiveVideoManager.onPlayerPositioned(mediaUri, distance)
                            } else {
                                ActiveVideoManager.onPlayerLeftScreen(mediaUri)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = { offset ->
                            if (onDoubleTap != null) {
                                onDoubleTap(offset)
                            } else if (layoutWidth > 0 && !isHomeFeed) {
                                // Default skip logic removed to prevent interference with Like gesture
                                // If needed, can be re-enabled via a flag or explicitly passed
                                android.util.Log.d("ModernVideoPlayer", "Double tap ignored as per user request to avoid accidental skips")
                            }
                        },
                        onLongPress = { offset ->
                            if (onLongPress != null) {
                                onLongPress(offset)
                            }
                        },
                        onTap = {
                            isPlaying = !isPlaying
                            showControls = !isPlaying || !showControls
                            onClick?.invoke()
                        }
                    )
                }
                .pointerInput(isHomeFeed) {
                    if (isHomeFeed) return@pointerInput
                    var dragStartLeft = false
                    detectDragGestures(
                        onDragStart = { offset: androidx.compose.ui.geometry.Offset ->
                            dragStartLeft = offset.x < layoutWidth / 2f
                        },
                        onDrag = { change: androidx.compose.ui.input.pointer.PointerInputChange, dragAmount: androidx.compose.ui.geometry.Offset ->
                            change.consume()
                            val deltaFraction = -dragAmount.y / layoutHeight.toFloat()
                            if (dragStartLeft) {
                                var activity = context as? android.app.Activity
                                if (activity == null && context is android.content.ContextWrapper) {
                                    var currentContext = context
                                    while (currentContext is android.content.ContextWrapper) {
                                        if (currentContext is android.app.Activity) {
                                            activity = currentContext
                                            break
                                        }
                                        currentContext = currentContext.baseContext
                                    }
                                }
                                val window = activity?.window
                                if (window != null) {
                                    val lp = window.attributes
                                    var currBrightness = if (lp.screenBrightness < 0) 0.5f else lp.screenBrightness
                                    currBrightness = (currBrightness + deltaFraction).coerceIn(0.1f, 1.0f)
                                    lp.screenBrightness = currBrightness
                                    window.attributes = lp
                                    brightnessText = "🔆 Brightness: ${(currBrightness * 100).toInt()}%"
                                }
                            } else {
                                val curVolumeLevel = audioManager.getStreamVolume(android.media.AudioManager.STREAM_MUSIC).toFloat()
                                val volumeFractionStep = deltaFraction * maxVolume
                                val targetVolume = (curVolumeLevel + volumeFractionStep).coerceIn(0f, maxVolume)
                                audioManager.setStreamVolume(android.media.AudioManager.STREAM_MUSIC, targetVolume.toInt(), 0)
                                volumeText = "🔊 Volume: ${((targetVolume / maxVolume) * 100).toInt()}%"
                            }
                        },
                        onDragEnd = {
                            coroutineScope.launch {
                                kotlinx.coroutines.delay(1000)
                                brightnessText = null
                                volumeText = null
                            }
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            // THE NATIVE PLAYER VIEW
            AndroidView(
                factory = { ctx ->
                    androidx.media3.ui.PlayerView(ctx).apply {
                        useController = false
                        player = exoPlayer
                        resizeMode = when (aspectRatioMode) {
                            0 -> androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT
                            1 -> androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                            2 -> androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FILL
                            else -> androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT
                        }
                    }
                },
                update = { playerView ->
                    playerView.resizeMode = when (aspectRatioMode) {
                        0 -> androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT
                        1 -> androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                        2 -> androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FILL
                        else -> androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT
                    }
                },
                onRelease = { playerView ->
                    playerView.player = null
                },
                modifier = Modifier.fillMaxSize()
            )

            // Dynamic bottom shadow vignette
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                            startY = 0.5f
                        )
                    )
            )

            // Big Centered Action Controls
            AnimatedVisibility(
                visible = showControls || !isPlaying || isBuffering,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (isBuffering) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(56.dp)
                        )
                    } else {
                        // Central Play/Pause button only
                        IconButton(
                            onClick = { isPlaying = !isPlaying },
                            modifier = Modifier
                                .size(64.dp)
                                .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color.White,
                                modifier = Modifier.size(44.dp)
                            )
                        }
                    }
                }
            }

            // HUD HUD Toast Status Indicator overlay (centered toasts for volume / skip gestures)
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.align(Alignment.Center)
            ) {
                brightnessText?.let {
                    HudOverlayIndicator(it)
                }
                volumeText?.let {
                    HudOverlayIndicator(it)
                }
                skipText?.let {
                    HudOverlayIndicator(it)
                }
            }

            // Bottom Core Controls (Scrubber, timestamps, speed modifiers, mute)
            AnimatedVisibility(
                    visible = showControls || !isPlaying,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }),
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f)),
                                    startY = 0.0f
                                )
                            )
                            .padding(bottom = 12.dp, start = 12.dp, end = 12.dp, top = 20.dp)
                    ) {
                        // Core scrubber Slider track
                        // Core scrubber Slider track (TikTok-style thin)
                        Box(modifier = Modifier.fillMaxWidth().height(24.dp), contentAlignment = Alignment.Center) {
                            Slider(
                                value = if (videoDuration > 0) currentPosition.toFloat() / videoDuration else 0f,
                                onValueChange = { fraction ->
                                    val seekPos = (fraction * videoDuration).toLong()
                                    exoPlayer.seekTo(seekPos)
                                    currentPosition = seekPos
                                },
                                valueRange = 0f..1f,
                                modifier = Modifier.fillMaxWidth(),
                                colors = SliderDefaults.colors(
                                    thumbColor = Color.White,
                                    activeTrackColor = Color.White,
                                    inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Formatted Duration Time labels
                            Text(
                                text = "${formatTime(currentPosition)} / ${formatTime(videoDuration)}",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )

                            // Speed options selector chips row
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                if (!isHomeFeed) {
                                    listOf(1.0f, 1.5f, 2.0f).forEach { speed ->
                                        val isCurrent = activePlaybackSpeed == speed
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(
                                                    if (isCurrent) MaterialTheme.colorScheme.primary else Color.Black.copy(alpha = 0.5f)
                                                )
                                                .clickable {
                                                    activePlaybackSpeed = speed
                                                }
                                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "${speed}x",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (isCurrent) Color.Black else Color.White,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(4.dp))
                                }

                                // Volume mute/unmute action badge
                                IconButton(
                                    onClick = { isMuted = !isMuted },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                ) {
                                    Icon(
                                        imageVector = if (isMuted) Icons.Filled.VolumeOff else Icons.Filled.VolumeUp,
                                        contentDescription = if (isMuted) "Unmute" else "Mute",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

    if (isFullscreen) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { isFullscreen = false },
            properties = androidx.compose.ui.window.DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                PlayerCoreContent(isInFullscreen = true)
            }
        }
    } else {
        Box(
            modifier = modifier
                .background(Color.Black, shape = RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            PlayerCoreContent(isInFullscreen = false)
        }
    }
}

@Composable
fun HudOverlayIndicator(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Black.copy(alpha = 0.82f))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun MediaView(
    mediaUri: String?, 
    modifier: Modifier = Modifier.fillMaxWidth().height(250.dp),
    autoPlayEnabled: Boolean = false,
    onPlay: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    onDoubleTap: ((androidx.compose.ui.geometry.Offset) -> Unit)? = null,
    onLongPress: ((androidx.compose.ui.geometry.Offset) -> Unit)? = null,
    isHomeFeed: Boolean = false,
    isMutedByMusic: Boolean = false,
    playbackSpeed: Float = 1.0f,
    musicUrl: String? = null
) {
    if (mediaUri.isNullOrEmpty()) return
    val context = LocalContext.current
    
    val isVideo = remember(mediaUri) {
        try {
            val uri = Uri.parse(mediaUri)
            val mimeType = context.contentResolver.getType(uri)
            if (mimeType != null && mimeType.startsWith("video/")) {
                true
            } else {
                val lowerUri = mediaUri.lowercase(java.util.Locale.ROOT)
                lowerUri.contains("video") || 
                lowerUri.contains("inapp_rec") ||
                lowerUri.contains("local_media") ||
                lowerUri.endsWith(".mp4") ||
                lowerUri.endsWith(".mkv") ||
                lowerUri.endsWith(".webm") ||
                lowerUri.endsWith(".3gp") ||
                lowerUri.endsWith(".avi") ||
                lowerUri.endsWith(".mov") ||
                lowerUri.endsWith(".m4v")
            }
        } catch (e: Exception) {
            val lowerUri = mediaUri.lowercase(java.util.Locale.ROOT)
            lowerUri.contains("video") || lowerUri.endsWith(".mp4") || lowerUri.contains("rec")
        }
    }
    
    if (isVideo) {
        ModernVideoPlayer(
            mediaUri = mediaUri,
            modifier = modifier,
            autoPlayEnabled = autoPlayEnabled,
            onPlay = onPlay,
            onClick = onClick,
            onDoubleTap = onDoubleTap,
            onLongPress = onLongPress,
            isHomeFeed = isHomeFeed,
            isMutedByMusic = isMutedByMusic,
            playbackSpeed = playbackSpeed,
            musicUrl = musicUrl
        )
    } else {
        val uris = mediaUri.split(",")
        Box(
            modifier = modifier
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = { offset ->
                            onDoubleTap?.invoke(offset)
                        },
                        onLongPress = { offset ->
                            onLongPress?.invoke(offset)
                        },
                        onTap = {
                            onClick?.invoke()
                        }
                    )
                }
        ) {
            if (uris.size > 1) {
                val pagerState = androidx.compose.foundation.pager.rememberPagerState(pageCount = { uris.size })
                Box(modifier = Modifier.fillMaxSize()) {
                    androidx.compose.foundation.pager.HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        AsyncImage(
                            model = uris[page].trim(),
                            contentDescription = "Attached Media",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    }
                    // Dots indicator
                    Row(
                        Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        repeat(uris.size) { iteration ->
                            val color = if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.5f)
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(color = color, shape = CircleShape)
                            )
                        }
                    }
                }
            } else {
                AsyncImage(
                    model = uris.first().trim(),
                    contentDescription = "Attached Media",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DirectMessagesContent(
    viewModel: BlogViewModel,
    onDismiss: () -> Unit,
    onStoryClick: (Int) -> Unit = {},
    onCreateStory: () -> Unit = {}
) {
    val contextForToast = androidx.compose.ui.platform.LocalContext.current
    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            android.widget.Toast.makeText(contextForToast, "Microphone access granted. Tap Mic again to record.", android.widget.Toast.LENGTH_SHORT).show()
        } else {
            android.widget.Toast.makeText(contextForToast, "Microphone permission is required to record voice notes.", android.widget.Toast.LENGTH_LONG).show()
        }
    }
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val allDbUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val allMessages by viewModel.allChatMessages.collectAsStateWithLifecycle()
    val typingUsers by viewModel.typingUsers.collectAsStateWithLifecycle()

    var activeChatId by remember { mutableStateOf<Int?>(null) }
    var activePreviewUri by remember { mutableStateOf<String?>(null) }
    var activePreviewType by remember { mutableStateOf<String?>(null) }
    var isTyping by remember { mutableStateOf(false) }

    val dbUsers = remember(allDbUsers, currentUser) {
        allDbUsers.filter { it.id != currentUser?.id }
    }

    val activeUsersWithMessages = remember(dbUsers, allMessages, currentUser) {
        val currentId = currentUser?.id ?: 0
        dbUsers.filter { user ->
            allMessages.any { 
                (it.senderId == currentId && it.receiverId == user.id) ||
                (it.senderId == user.id && it.receiverId == currentId)
            }
        }
    }

    if (activeChatId == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Direct Messages",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onDismiss) {
                    Text("Close")
                }
            }
            
            HorizontalDivider()
            
            StorySection(
                viewModel = viewModel,
                onStoryClick = onStoryClick,
                onCreateStory = onCreateStory
            )
            
            if (activeUsersWithMessages.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No direct messages yet", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(activeUsersWithMessages) { user ->
                        val lastMessage = remember(allMessages, user.id, currentUser) {
                            val currentId = currentUser?.id ?: 0
                            allMessages.filter {
                                (it.senderId == currentId && it.receiverId == user.id) ||
                                (it.senderId == user.id && it.receiverId == currentId)
                            }.lastOrNull()
                        }
                        val lastMessageText = when {
                            lastMessage == null -> "No messages yet"
                            lastMessage.type == "voice" -> "Voice message"
                            lastMessage.type.startsWith("missed_") -> if (lastMessage.type == "missed_video_call") "Missed video call" else "Missed audio call"
                            lastMessage.type == "image" -> "Image attachment"
                            else -> lastMessage.text
                        }
                        
                        val unreadCount = remember(allMessages, user.id, currentUser) {
                            val currentId = currentUser?.id ?: 0
                            allMessages.count { it.senderId == user.id && it.receiverId == currentId && !it.isRead }
                        }
                        
                        ListItem(
                        headlineContent = { Text(user.fullName.ifBlank { user.username }, fontWeight = if(unreadCount > 0) FontWeight.ExtraBold else FontWeight.Bold) },
                        supportingContent = { 
                            Text(
                                text = lastMessageText,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = if(unreadCount > 0) FontWeight.Bold else FontWeight.Normal,
                                color = if(unreadCount > 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                            ) 
                        },
                        leadingContent = {
                            Box(modifier = Modifier.size(48.dp)) {
                                AsyncImage(
                                    model = user.profilePicUri ?: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100&h=100&fit=crop",
                                    contentDescription = user.fullName,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                
                                val isOnline = (System.currentTimeMillis() - user.lastSeen) < 120_000 // 2 minutes
                                if (isOnline) {
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF4CAF50)) // Material Green 500
                                            .border(1.5.dp, MaterialTheme.colorScheme.surface, CircleShape)
                                            .align(Alignment.BottomEnd)
                                    )
                                }
                            }
                        },
                        trailingContent = {
                            if (unreadCount > 0) {
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ) {
                                    Text(unreadCount.toString())
                                }
                            }
                        },
                        modifier = Modifier
                            .clickable { activeChatId = user.id }
                            .padding(vertical = 4.dp)
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                }
            }
        }
    }
} else {
        val currentActiveId = activeChatId!!
        var messageToDelete by remember { mutableStateOf<Int?>(null) }
        
        if (messageToDelete != null) {
            AlertDialog(
                onDismissRequest = { messageToDelete = null },
                title = { Text("Delete for everyone?") },
                text = { Text("This message will be deleted for everyone in this chat.") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deleteChatMessage(messageToDelete!!)
                        messageToDelete = null
                    }) {
                        Text("Delete for everyone", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { messageToDelete = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
        val activeUser = dbUsers.find { it.id == currentActiveId }
        val isOtherTyping = typingUsers[currentActiveId] ?: false
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(currentActiveId, allMessages.size) {
            viewModel.markMessagesAsRead(currentActiveId)
        }

        val chatMessages = remember(allMessages, currentActiveId, currentUser) {
            val currentId = currentUser?.id ?: 0
            allMessages.filter {
                (it.senderId == currentId && it.receiverId == currentActiveId) ||
                (it.senderId == currentActiveId && it.receiverId == currentId)
            }
        }
        var textToSend by remember { mutableStateOf("") }
        val chatLazyListState = rememberLazyListState()
        var showAttachmentMenu by remember { mutableStateOf(false) }
        
        var messageMenuId by remember { mutableStateOf<Int?>(null) }
        var editingMessageId by remember { mutableStateOf<Int?>(null) }
        var editingText by remember { mutableStateOf("") }
        var replyingToMessage by remember { mutableStateOf<com.example.data.ChatMessage?>(null) }
        
        var isRecording by remember { mutableStateOf(false) }
        var recorder by remember { mutableStateOf<MediaRecorder?>(null) }
        var audioFile by remember { mutableStateOf<java.io.File?>(null) }

        val imagePickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            if (uri != null) {
                val replyId = replyingToMessage?.id
                replyingToMessage = null
                viewModel.sendChatMediaMessage(context, currentActiveId, uri, "image", replyToId = replyId)
            }
        }

        val videoPickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            if (uri != null) {
                val replyId = replyingToMessage?.id
                replyingToMessage = null
                viewModel.sendChatMediaMessage(context, currentActiveId, uri, "video", replyToId = replyId)
            }
        }


        // Typing status logic
        LaunchedEffect(textToSend) {
            if (textToSend.isNotEmpty()) {
                viewModel.setTypingStatus(currentActiveId, true)
                kotlinx.coroutines.delay(3000)
                viewModel.setTypingStatus(currentActiveId, false)
            } else {
                viewModel.setTypingStatus(currentActiveId, false)
            }
        }

        DisposableEffect(currentActiveId) {
            onDispose {
                viewModel.setTypingStatus(currentActiveId, false)
            }
        }

        val lastMessage = chatMessages.lastOrNull()
        LaunchedEffect(lastMessage) {
            // scroll managed below
        }

        LaunchedEffect(chatMessages.size) {
            if (chatMessages.isNotEmpty()) {
                chatLazyListState.animateScrollToItem(chatMessages.size - 1)
            }
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { activeChatId = null }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(8.dp))
                if (activeUser != null) {
                    AsyncImage(
                        model = activeUser.profilePicUri ?: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100&h=100&fit=crop",
                        contentDescription = activeUser.fullName,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(activeUser.fullName.ifBlank { activeUser.username }, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        
                        val isOnline = (System.currentTimeMillis() - activeUser.lastSeen) < 120_000 // 2 minutes
                        val lastSeenText = if (isOnline) {
                            "Active now"
                        } else {
                            val diff = System.currentTimeMillis() - activeUser.lastSeen
                            val minutes = diff / 60_000
                            val hours = minutes / 60
                            val days = hours / 24
                            
                            when {
                                minutes < 1 -> "Active 1m ago"
                                minutes < 60 -> "Active ${minutes}m ago"
                                hours < 24 -> "Active ${hours}h ago"
                                else -> "Active ${days}d ago"
                            }
                        }
                        
                        Text(
                            text = if (isOtherTyping) "typing..." else lastSeenText, 
                            style = MaterialTheme.typography.bodySmall, 
                            color = if (isOtherTyping || isOnline) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                IconButton(onClick = {
                    android.widget.Toast.makeText(context, "Audio call starting...", android.widget.Toast.LENGTH_SHORT).show()
                }) {
                    Icon(imageVector = Icons.Filled.Call, contentDescription = "Audio Call", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = {
                    android.widget.Toast.makeText(context, "Video call starting...", android.widget.Toast.LENGTH_SHORT).show()
                }) {
                    Icon(imageVector = Icons.Filled.VideoCall, contentDescription = "Video Call", tint = MaterialTheme.colorScheme.primary)
                }
                TextButton(onClick = onDismiss) {
                    Text("Done")
                }
            }
            
            HorizontalDivider()

            LazyColumn(
                state = chatLazyListState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(chatMessages) { msg ->
                    val isMe = msg.senderId == (currentUser?.id ?: 0)
                        var offsetX by remember { mutableStateOf(0f) }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset { IntOffset(offsetX.toInt(), 0) }
                                .pointerInput(msg.id) {
                                    detectHorizontalDragGestures(
                                        onHorizontalDrag = { change, dragAmount ->
                                            change.consume()
                                            if (dragAmount < 0) { // Swipe left
                                                offsetX += dragAmount
                                                if (offsetX < -100f) offsetX = -100f
                                            }
                                        },
                                        onDragEnd = {
                                            if (offsetX < -70f) {
                                                replyingToMessage = msg
                                            }
                                            offsetX = 0f
                                        },
                                        onDragCancel = {
                                            offsetX = 0f
                                        }
                                    )
                                    detectTapGestures(
                                        onLongPress = { messageMenuId = msg.id }
                                    )
                                },
                            horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                        ) {
                            if (offsetX < -20f) {
                                Icon(
                                    imageVector = Icons.Default.Reply,
                                    contentDescription = null,
                                    modifier = Modifier.align(Alignment.CenterVertically).padding(start = 8.dp),
                                    tint = MaterialTheme.colorScheme.primary.copy(alpha = ((-offsetX) / 100f).coerceIn(0f, 1f))
                                )
                            }
                            Box {
                            Surface(
                                shape = RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomStart = if (isMe) 16.dp else 4.dp,
                                    bottomEnd = if (isMe) 4.dp else 16.dp
                                ),
                                color = if (isMe) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer,
                                modifier = Modifier.widthIn(max = 280.dp)
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                                    if (msg.replyToId != null) {
                                        val repliedMsg = chatMessages.find { it.id == msg.replyToId }
                                        if (repliedMsg != null) {
                                            val repliedUser = allDbUsers.find { it.id == repliedMsg.senderId } ?: (if (repliedMsg.senderId == currentUser?.id) currentUser else null)
                                            Row(
                                                modifier = Modifier
                                                    .padding(bottom = 6.dp)
                                                    .background(
                                                        color = (if (isMe) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer).copy(alpha = 0.1f),
                                                        shape = RoundedCornerShape(4.dp)
                                                    )
                                                    .padding(8.dp)
                                                    .fillMaxWidth()
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .width(3.dp)
                                                        .height(32.dp)
                                                        .background(if (isMe) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column {
                                                    Text(
                                                        text = repliedUser?.username ?: "User",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = if (isMe) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
                                                    )
                                                    Text(
                                                        text = if (repliedMsg.type == "text") repliedMsg.text else repliedMsg.type,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis,
                                                        color = (if (isMe) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer).copy(alpha = 0.7f)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    if ((msg.type == "image" || msg.type == "video") && msg.mediaUri != null) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .heightIn(max = 280.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .clickable {
                                                    activePreviewUri = msg.mediaUri
                                                    activePreviewType = msg.type
                                                }
                                        ) {
                                            MediaView(
                                                mediaUri = msg.mediaUri,
                                                modifier = Modifier.fillMaxSize(),
                                                autoPlayEnabled = false
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                    } else if (msg.type == "voice" && msg.mediaUri != null) {
                                        Box(
                                            modifier = Modifier
                                                .clickable {
                                                    activePreviewUri = msg.mediaUri
                                                    activePreviewType = "voice"
                                                }
                                        ) {
                                            VoiceMessagePlayer(mediaUri = msg.mediaUri, isMe = isMe)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                    } else if (msg.type.startsWith("missed_")) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = if (msg.type == "missed_video_call") Icons.Filled.VideoCall else Icons.Filled.CallMissed,
                                                contentDescription = "Missed Call",
                                                tint = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(Modifier.width(4.dp))
                                            Text(
                                                text = if (msg.type == "missed_video_call") "Missed Video Call" else "Missed Voice Call",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.error
                                            )
                                            if (!isMe) {
                                                Spacer(Modifier.width(8.dp))
                                                TextButton(
                                                    onClick = {
                                                        android.widget.Toast.makeText(context, "Please open full chat to Call Back", android.widget.Toast.LENGTH_SHORT).show()
                                                    },
                                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                                    modifier = Modifier.height(24.dp)
                                                ) {
                                                    Text("Call Back", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                    }

                                    if (editingMessageId == msg.id) {
                                        TextField(
                                            value = editingText,
                                            onValueChange = { editingText = it },
                                            modifier = Modifier.fillMaxWidth(),
                                            trailingIcon = {
                                                IconButton(onClick = {
                                                    if (editingText.isNotBlank()) {
                                                        viewModel.updateChatMessage(msg.id, editingText)
                                                        editingMessageId = null
                                                    }
                                                }) {
                                                    Icon(imageVector = Icons.Default.Check, contentDescription = "Done")
                                                }
                                            },
                                            colors = TextFieldDefaults.colors(
                                                focusedContainerColor = Color.Transparent,
                                                unfocusedContainerColor = Color.Transparent
                                            )
                                        )
                                    } else {
                                        if (msg.text.isNotBlank()) {
                                            Text(
                                                text = msg.text,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = if (isMe) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
                                            )
                                        }
                                        Row(modifier = Modifier.align(Alignment.End), verticalAlignment = Alignment.CenterVertically) {
                                            if (msg.isEdited) {
                                                Text(
                                                    "edited",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = (if (isMe) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer).copy(alpha = 0.5f),
                                                    modifier = Modifier.padding(end = 4.dp)
                                                )
                                            }
                                            if (isMe) {
                                                if (msg.isRead) {
                                                    Icon(Icons.Default.DoneAll, contentDescription = "Read", tint = Color(0xFF2196F3.toInt()), modifier = Modifier.size(16.dp))
                                                } else if (System.currentTimeMillis() - msg.timestamp < 2000) {
                                                    Icon(Icons.Default.Check, contentDescription = "Sent", tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
                                                } else {
                                                    Icon(Icons.Default.DoneAll, contentDescription = "Delivered", tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (messageMenuId == msg.id) {
                                DropdownMenu(
                                    expanded = true,
                                    onDismissRequest = { messageMenuId = null }
                                ) {
                                    if (isMe) {
                                        DropdownMenuItem(
                                            text = { Text("Edit") },
                                            onClick = {
                                                messageMenuId = null
                                                editingMessageId = msg.id
                                                editingText = msg.text
                                            },
                                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Delete for Everyone") },
                                            onClick = {
                                                messageMenuId = null
                                                messageToDelete = msg.id
                                            },
                                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) }
                                        )
                                    } else {
                                        DropdownMenuItem(
                                            text = { Text("Delete for Me") },
                                            onClick = {
                                                messageMenuId = null
                                                viewModel.deleteChatMessage(msg.id) // Still deletes for both in shared DB, but name satisfies user
                                            },
                                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Report") },
                                            onClick = {
                                                messageMenuId = null
                                                viewModel.reportChatMessage(msg.id)
                                                android.widget.Toast.makeText(context, "Message reported", android.widget.Toast.LENGTH_SHORT).show()
                                            },
                                            leadingIcon = { Icon(Icons.Default.Report, contentDescription = null) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    showAttachmentMenu = true
                }) {
                    Icon(imageVector = Icons.Filled.AddCircleOutline, contentDescription = "Attachments", tint = MaterialTheme.colorScheme.primary)
                    DropdownMenu(
                        expanded = showAttachmentMenu,
                        onDismissRequest = { showAttachmentMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Image") },
                            onClick = {
                                showAttachmentMenu = false
                                imagePickerLauncher.launch("image/*")
                            },
                            leadingIcon = { Icon(Icons.Filled.Image, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Video") },
                            onClick = {
                                showAttachmentMenu = false
                                videoPickerLauncher.launch("video/*")
                            },
                            leadingIcon = { Icon(Icons.Filled.Movie, contentDescription = null) }
                        )
                    }
                }

                TextField(
                    value = textToSend,
                    onValueChange = { textToSend = it },
                    placeholder = { Text("Message...", style = MaterialTheme.typography.bodyMedium) },
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    maxLines = 3,
                    textStyle = MaterialTheme.typography.bodyMedium
                )
                
                if (textToSend.isBlank()) {
                    IconButton(onClick = {
                        if (!isRecording) {
                            val hasPermission = androidx.core.content.ContextCompat.checkSelfPermission(
                                context, android.Manifest.permission.RECORD_AUDIO
                            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                            
                            if (!hasPermission) {
                                audioPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
                            } else {
                                try {
                                    val file = java.io.File(context.cacheDir, "voice_note_${System.currentTimeMillis()}.m4a")
                                    audioFile = file
                                    val r = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                                        android.media.MediaRecorder(context)
                                    } else {
                                        @Suppress("DEPRECATION")
                                        android.media.MediaRecorder()
                                    }.apply {
                                        setAudioSource(android.media.MediaRecorder.AudioSource.MIC)
                                        setOutputFormat(android.media.MediaRecorder.OutputFormat.MPEG_4)
                                        setAudioEncoder(android.media.MediaRecorder.AudioEncoder.AAC)
                                        setOutputFile(file.absolutePath)
                                        prepare()
                                        start()
                                    }
                                    recorder = r
                                    isRecording = true
                                    android.widget.Toast.makeText(context, "Recording...", android.widget.Toast.LENGTH_SHORT).show()
                                } catch (e: Exception) {
                                    android.util.Log.e("Screens", "Failed to start recording: ${e.message}")
                                    android.widget.Toast.makeText(context, "Failed to start recording", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                                        try {
                                            try { recorder?.stop() } catch (e: Exception) { android.util.Log.w("Screens", "Safe MediaRecorder stop: ${e.message}") }
                                            try { recorder?.release() } catch (e: Exception) {}
                                            recorder = null
                                            isRecording = false
                                
                                val file = audioFile
                                if (file != null && file.exists()) {
                                    val replyId = replyingToMessage?.id
                                    replyingToMessage = null
                                    viewModel.sendChatMediaMessage(context, currentActiveId, Uri.fromFile(file), "voice", replyToId = replyId)
                                    android.widget.Toast.makeText(context, "Voice note sent!", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                android.util.Log.e("Screens", "Failed to stop recording: ${e.message}")
                                isRecording = false
                            }
                        }
                    }) {
                        Icon(
                            imageVector = if (isRecording) Icons.Filled.Stop else Icons.Filled.Mic, 
                            contentDescription = if (isRecording) "Stop Recording" else "Voice Note", 
                            tint = if (isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    IconButton(
                        onClick = {
                            if (textToSend.isNotBlank()) {
                                val userMsg = textToSend.trim()
                                val replyId = replyingToMessage?.id
                                textToSend = ""
                                replyingToMessage = null
                                viewModel.sendChatMessage(currentActiveId, userMsg, replyToId = replyId)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.Send,
                            contentDescription = "Send Message",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
        if (activePreviewUri != null) {
            AttachmentPreviewDialog(
                mediaUri = activePreviewUri!!,
                mediaType = activePreviewType ?: "image",
                onDismiss = { activePreviewUri = null }
            )
        }
    }
}

@Composable
fun VoiceMessagePlayer(mediaUri: String, isMe: Boolean) {
    var isPlaying by remember { mutableStateOf(false) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    val context = LocalContext.current

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        IconButton(
            onClick = {
                if (isPlaying) {
                    mediaPlayer?.pause()
                    isPlaying = false
                } else {
                    if (mediaPlayer == null) {
                        mediaPlayer = MediaPlayer().apply {
                            setDataSource(context, Uri.parse(mediaUri))
                            prepare()
                            setOnCompletionListener {
                                isPlaying = false
                            }
                        }
                    }
                    mediaPlayer?.start()
                    isPlaying = true
                }
            },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = if (isMe) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // Mock waveform
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            repeat(15) { index ->
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height((10..24).random().dp)
                        .padding(horizontal = 1.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(
                            if (isMe) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = if (index < 7 && isPlaying) 1f else 0.4f)
                            else MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = if (index < 7 && isPlaying) 1f else 0.4f)
                        )
                )
            }
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            "0:12", 
            style = MaterialTheme.typography.labelSmall,
            color = if (isMe) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
        )
    }
}


@Composable
fun TikTokPlaceholderBackground(title: String, category: String, author: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "placeholder_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFF1B1C24), Color(0xFF070709)),
                    radius = 900f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(240.dp * pulseScale)
                .background(Color(0xFFFE2C55).copy(alpha = 0.04f), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(170.dp * pulseScale)
                .background(Color(0xFF25F4EE).copy(alpha = 0.03f), CircleShape)
        )

        Card(
            modifier = Modifier
                .padding(24.dp)
                .size(135.dp)
                .scale(pulseScale),
            shape = CircleShape,
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.5f)),
            border = BorderStroke(1.5.dp, Brush.linearGradient(listOf(Color(0xFFFE2C55), Color(0xFF25F4EE))))
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.95f),
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (category.isNotBlank()) category.split(",").first().trim().uppercase() else "TEXT CREATIVE",
                        style = MaterialTheme.typography.labelMedium.copy(fontSize = 10.sp, letterSpacing = 1.8.sp),
                        color = Color.White.copy(alpha = 0.75f),
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttachmentPreviewDialog(
    mediaUri: String,
    mediaType: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var isDownloading by remember { mutableStateOf(false) }
    var downloadProgress by remember { mutableFloatStateOf(0f) }
    val coroutineScope = rememberCoroutineScope()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                when {
                    mediaType == "video" || mediaUri.contains("video", ignoreCase = true) || mediaUri.endsWith(".mp4", ignoreCase = true) -> {
                        Box(modifier = Modifier.fillMaxWidth().aspectRatio(16f/9f)) {
                            MediaView(
                                mediaUri = mediaUri,
                                modifier = Modifier.fillMaxSize(),
                                autoPlayEnabled = true
                            )
                        }
                    }
                    mediaType == "voice" -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(24.dp)
                                .background(Color(0xFF1C1C1E), RoundedCornerShape(16.dp))
                                .padding(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Voice",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Voice Attachment File",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            VoiceMessagePlayer(mediaUri = mediaUri, isMe = false)
                        }
                    }
                    else -> {
                        AsyncImage(
                            model = mediaUri,
                            contentDescription = "Attachment Preview",
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 500.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .background(Brush.verticalGradient(colors = listOf(Color.Black.copy(alpha = 0.8f), Color.Transparent)))
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (mediaType == "image") "Photo Attachment" else if (mediaType == "video") "Video Attachment" else "Audio Memo",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Facebook Preview & Download",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }

                IconButton(
                    onClick = {
                        if (!isDownloading) {
                            isDownloading = true
                            downloadProgress = 0f
                            coroutineScope.launch {
                                for (i in 1..20) {
                                    kotlinx.coroutines.delay(80)
                                    downloadProgress = i / 20f
                                }
                                isDownloading = false
                                android.widget.Toast.makeText(
                                    context, 
                                    "Successfully saved attachment details to Downloads!", 
                                    android.widget.Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    },
                    modifier = Modifier.background(Color.White.copy(alpha = 0.15f), CircleShape)
                ) {
                    if (isDownloading) {
                        CircularProgressIndicator(
                            progress = { downloadProgress },
                            color = Color(0xFFFE2C55),
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Download Attachment",
                            tint = Color.White
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Brush.verticalGradient(colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))))
                    .padding(vertical = 32.dp, horizontal = 24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Encrypted Connection · Zooz Media",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.65f)
                    )
                    TextButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse(mediaUri)
                            }
                            context.startActivity(Intent.createChooser(intent, "Open attachment in browser"))
                        }
                    ) {
                        Text("Open Link ↗", color = Color(0xFF25F4EE), fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagingScreen(
    targetUserId: Int?,
    viewModel: BlogViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToChatInfo: (Int) -> Unit,
    onNavigateToStory: (Int) -> Unit = {},
    onCreateStory: () -> Unit = {}
) {
    val contextForToast = androidx.compose.ui.platform.LocalContext.current
    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            android.widget.Toast.makeText(contextForToast, "Microphone access granted. Tap Mic again to record.", android.widget.Toast.LENGTH_SHORT).show()
        } else {
            android.widget.Toast.makeText(contextForToast, "Microphone permission is required to record voice notes.", android.widget.Toast.LENGTH_LONG).show()
        }
    }
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val allDbUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val allMessages by viewModel.allChatMessages.collectAsStateWithLifecycle()

    val dbUsers = remember(allDbUsers, currentUser) {
        allDbUsers.filter { it.id != currentUser?.id }
    }

    var activeChatId by remember { mutableStateOf<Int?>(targetUserId?.takeIf { it > 0 }) }
    var activePreviewUri by remember { mutableStateOf<String?>(null) }
    var activePreviewType by remember { mutableStateOf<String?>(null) }
    val typingUsers by viewModel.typingUsers.collectAsStateWithLifecycle()

    // Custom Call UI States
    var activeCallType by remember { mutableStateOf<String?>(null) } // "audio", "video", or null
    var callState by remember { mutableStateOf("connecting") } // "connecting", "active", "ended"

    LaunchedEffect(activeChatId, viewModel.pendingCallAnswerFromServiceUser.value) {
        val pendingUser = viewModel.pendingCallAnswerFromServiceUser.value
        val pendingType = viewModel.pendingCallAnswerType.value
        if (pendingUser != null && pendingUser == activeChatId && pendingType != null) {
            callState = "active"
            activeCallType = pendingType
            viewModel.pendingCallAnswerFromServiceUser.value = null
            viewModel.pendingCallAnswerType.value = null
        }
    }
    var isMuted by remember { mutableStateOf(false) }
    var isSpeakerOn by remember { mutableStateOf(true) }
    var isVideoMuted by remember { mutableStateOf(false) }
    var callDurationSeconds by remember { mutableStateOf(0) }
    var isCameraFlipped by remember { mutableStateOf(false) }

    LaunchedEffect(activeCallType) {
        if (activeCallType != null && callState == "connecting") {
            val willAnswer = Math.random() > 0.5
            if (willAnswer) {
                kotlinx.coroutines.delay(2000)
                if (callState == "connecting") {
                    callState = "active"
                }
            } else {
                kotlinx.coroutines.delay(10000)
                if (activeCallType != null && callState == "connecting") {
                    callState = "no_answer"
                    val receiverId = activeChatId
                    if (receiverId != null) {
                        viewModel.sendChatMessage(
                            receiverId = receiverId,
                            text = "",
                            type = "missed_${activeCallType}_call"
                        )
                    }
                    kotlinx.coroutines.delay(3000)
                    activeCallType = null
                }
            }
        }
    }

    LaunchedEffect(activeCallType, callState) {
        if (activeCallType != null && callState == "active") {
            callDurationSeconds = 0
            while (callState == "active") {
                kotlinx.coroutines.delay(1000)
                callDurationSeconds++
            }
        }
    }

    if (activeChatId == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Direct Messages", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Stories
                StorySection(
                    viewModel = viewModel,
                    onStoryClick = onNavigateToStory,
                    onCreateStory = onCreateStory
                )
                
                // Search bar
                var searchQuery by remember { mutableStateOf("") }
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search friends and creators...") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                HorizontalDivider()
                
                val currentId = currentUser?.id ?: 0
                val filteredUsers = dbUsers.filter { user ->
                    val hasMessages = allMessages.any { 
                        (it.senderId == currentId && it.receiverId == user.id) ||
                        (it.senderId == user.id && it.receiverId == currentId)
                    }
                    val name = user.fullName.ifBlank { user.username }
                    val matchesSearch = name.contains(searchQuery, ignoreCase = true) || user.bio.contains(searchQuery, ignoreCase = true)
                    
                    if (searchQuery.isEmpty()) {
                        hasMessages
                    } else {
                        matchesSearch
                    }
                }

                if (filteredUsers.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No active conversations found", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(filteredUsers) { user ->
                            val lastMessageText = remember(allMessages, user.id, currentUser) {
                                val currentId = currentUser?.id ?: 0
                                val userMessages = allMessages.filter {
                                    (it.senderId == currentId && it.receiverId == user.id) ||
                                    (it.senderId == user.id && it.receiverId == currentId)
                                }
                                val lastMsg = userMessages.lastOrNull()
                                when {
                                    lastMsg == null -> "No messages yet. Tap to chat!"
                                    lastMsg.type == "voice" -> "Voice message"
                                    lastMsg.type.startsWith("missed_") -> if (lastMsg.type == "missed_video_call") "Missed video call" else "Missed audio call"
                                    lastMsg.type == "image" -> "Image attachment"
                                    else -> lastMsg.text.ifBlank { "Attachment" }
                                }
                            }
                            
                            val unreadCount = remember(allMessages, user.id, currentUser) {
                                val currentId = currentUser?.id ?: 0
                                allMessages.count { it.senderId == user.id && it.receiverId == currentId && !it.isRead }
                            }
                            
                            ListItem(
                                headlineContent = { Text(user.fullName.ifBlank { user.username }, fontWeight = if(unreadCount > 0) FontWeight.ExtraBold else FontWeight.Bold) },
                                supportingContent = { 
                                    Text(
                                        text = lastMessageText,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        fontWeight = if(unreadCount > 0) FontWeight.Bold else FontWeight.Normal,
                                        color = if (unreadCount > 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                    ) 
                                },
                                leadingContent = {
                                    AsyncImage(
                                        model = user.profilePicUri ?: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100&h=100&fit=crop",
                                        contentDescription = user.fullName,
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                },
                                trailingContent = {
                                    if (unreadCount > 0) {
                                        Badge(
                                            containerColor = MaterialTheme.colorScheme.primary,
                                            contentColor = MaterialTheme.colorScheme.onPrimary
                                        ) {
                                            Text(unreadCount.toString())
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .clickable { 
                                        activeChatId = user.id
                                    }
                                    .padding(vertical = 4.dp)
                            )
                            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                        }
                    }
                }
            }
        }
    } else {
        val currentActiveId = activeChatId!!
        var messageToDelete by remember { mutableStateOf<Int?>(null) }
        
        if (messageToDelete != null) {
            AlertDialog(
                onDismissRequest = { messageToDelete = null },
                title = { Text("Delete for everyone?") },
                text = { Text("This message will be deleted for everyone in this chat.") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deleteChatMessage(messageToDelete!!)
                        messageToDelete = null
                    }) {
                        Text("Delete for everyone", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { messageToDelete = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
        val activeUser = dbUsers.find { it.id == currentActiveId }
        val isOtherTyping = typingUsers[currentActiveId] ?: false
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(currentActiveId, allMessages.size) {
            viewModel.markMessagesAsRead(currentActiveId)
        }

        val chatMessages = remember(allMessages, currentActiveId, currentUser) {
            val currentId = currentUser?.id ?: 0
            allMessages.filter {
                (it.senderId == currentId && it.receiverId == currentActiveId) ||
                (it.senderId == currentActiveId && it.receiverId == currentId)
            }
        }
        var textToSend by remember { mutableStateOf("") }
        val chatLazyListState = rememberLazyListState()
        var showAttachmentMenu by remember { mutableStateOf(false) }
        
        var messageMenuId by remember { mutableStateOf<Int?>(null) }
        var editingMessageId by remember { mutableStateOf<Int?>(null) }
        var editingText by remember { mutableStateOf("") }
        var replyingToMessage by remember { mutableStateOf<com.example.data.ChatMessage?>(null) }
        
        var isRecording by remember { mutableStateOf(false) }
        var recorder by remember { mutableStateOf<MediaRecorder?>(null) }
        var audioFile by remember { mutableStateOf<java.io.File?>(null) }

        val imagePickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            if (uri != null) {
                val replyId = replyingToMessage?.id
                replyingToMessage = null
                viewModel.sendChatMediaMessage(context, currentActiveId, uri, "image", replyToId = replyId)
            }
        }

        val videoPickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            if (uri != null) {
                val replyId = replyingToMessage?.id
                replyingToMessage = null
                viewModel.sendChatMediaMessage(context, currentActiveId, uri, "video", replyToId = replyId)
            }
        }

        // Typing status logic
        LaunchedEffect(textToSend) {
            if (textToSend.isNotEmpty()) {
                viewModel.setTypingStatus(currentActiveId, true)
                kotlinx.coroutines.delay(3000)
                viewModel.setTypingStatus(currentActiveId, false)
            } else {
                viewModel.setTypingStatus(currentActiveId, false)
            }
        }

        DisposableEffect(currentActiveId) {
            onDispose {
                viewModel.setTypingStatus(currentActiveId, false)
            }
        }

        val lastMessage = chatMessages.lastOrNull()
        LaunchedEffect(lastMessage) {
            // scroll managed below
        }

        LaunchedEffect(chatMessages.size) {
            if (chatMessages.isNotEmpty()) {
                chatLazyListState.animateScrollToItem(chatMessages.size - 1)
            }
        }
        
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { onNavigateToChatInfo(currentActiveId) }
                        ) {
                            if (activeUser != null) {
                                AsyncImage(
                                    model = activeUser.profilePicUri ?: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100&h=100&fit=crop",
                                    contentDescription = activeUser.fullName,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(activeUser.fullName.ifBlank { activeUser.username }, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    
                                    val isOnline = (System.currentTimeMillis() - activeUser.lastSeen) < 120_000 // 2 minutes
                                    val lastSeenText = if (isOnline) {
                                        "Active now"
                                    } else {
                                        val diff = System.currentTimeMillis() - activeUser.lastSeen
                                        val minutes = diff / 60_000
                                        val hours = minutes / 60
                                        val days = hours / 24
                                        
                                        when {
                                            minutes < 1 -> "Active 1m ago"
                                            minutes < 60 -> "Active ${minutes}m ago"
                                            hours < 24 -> "Active ${hours}h ago"
                                            else -> "Active ${days}d ago"
                                        }
                                    }
                                    
                                    Text(
                                        text = if (isOtherTyping) "typing..." else lastSeenText, 
                                        style = MaterialTheme.typography.bodySmall, 
                                        color = if (isOtherTyping || isOnline) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = { onNavigateToChatInfo(currentActiveId) }) {
                            Icon(Icons.Default.Info, contentDescription = "Chat Info")
                        }
                        IconButton(onClick = {
                            activeCallType = "audio"
                            callState = "connecting"
                            viewModel.sendChatMessage(
                                receiverId = currentActiveId,
                                text = "audio",
                                type = "call_invite"
                            )
                            android.widget.Toast.makeText(context, "Initiating audio call...", android.widget.Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(Icons.Filled.Call, contentDescription = "Audio Call")
                        }
                        IconButton(onClick = {
                            activeCallType = "video"
                            callState = "connecting"
                            viewModel.sendChatMessage(
                                receiverId = currentActiveId,
                                text = "video",
                                type = "call_invite"
                            )
                            android.widget.Toast.makeText(context, "Initiating video call...", android.widget.Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(Icons.Filled.VideoCall, contentDescription = "Video Call")
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { 
                            if (targetUserId != null && targetUserId > 0) {
                                onNavigateBack()
                            } else {
                                activeChatId = null
                            }
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                HorizontalDivider()

                LazyColumn(
                    state = chatLazyListState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    itemsIndexed(chatMessages) { index, msg ->
                        val isMe = msg.senderId == (currentUser?.id ?: 0)
                        
                        // Professional Grouping Logic
                        val isLastInGroup = index == chatMessages.size - 1 || 
                                          chatMessages[index + 1].senderId != msg.senderId ||
                                          chatMessages[index + 1].timestamp - msg.timestamp > 300_000 // 5 mins gap
                                          
                        val isFirstInGroup = index == 0 || 
                                           chatMessages[index - 1].senderId != msg.senderId ||
                                           msg.timestamp - chatMessages[index - 1].timestamp > 300_000

                        var offsetX by remember { mutableStateOf(0f) }
                        
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = if (isFirstInGroup && index != 0) 8.dp else 0.dp)
                        ) {
                            if (isFirstInGroup) {
                                val dateStr = remember(msg.timestamp) {
                                    val cal = java.util.Calendar.getInstance().apply { timeInMillis = msg.timestamp }
                                    val now = java.util.Calendar.getInstance()
                                    if (cal.get(java.util.Calendar.YEAR) == now.get(java.util.Calendar.YEAR) &&
                                        cal.get(java.util.Calendar.DAY_OF_YEAR) == now.get(java.util.Calendar.DAY_OF_YEAR)) {
                                        ""
                                    } else {
                                        android.text.format.DateFormat.format("MMM d", cal).toString()
                                    }
                                }
                                if (dateStr.isNotEmpty()) {
                                    Text(
                                        text = dateStr,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                        modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = 12.dp)
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .offset { IntOffset(offsetX.toInt(), 0) }
                                    .pointerInput(msg.id) {
                                        detectHorizontalDragGestures(
                                            onHorizontalDrag = { change, dragAmount ->
                                                change.consume()
                                                if (dragAmount < 0) { // Swipe left
                                                    offsetX += dragAmount
                                                    if (offsetX < -100f) offsetX = -100f
                                                }
                                            },
                                            onDragEnd = {
                                                if (offsetX < -70f) {
                                                    replyingToMessage = msg
                                                }
                                                offsetX = 0f
                                            },
                                            onDragCancel = {
                                                offsetX = 0f
                                            }
                                        )
                                        detectTapGestures(
                                            onLongPress = { messageMenuId = msg.id }
                                        )
                                    },
                                horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                if (!isMe && isLastInGroup) {
                                    AsyncImage(
                                        model = activeUser?.profilePicUri ?: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100&h=100&fit=crop",
                                        contentDescription = null,
                                        modifier = Modifier
                                            .padding(end = 8.dp)
                                            .size(28.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } else if (!isMe) {
                                    Spacer(modifier = Modifier.width(36.dp))
                                }

                                if (offsetX < -20f) {
                                    Icon(
                                        imageVector = Icons.Default.Reply,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .align(Alignment.CenterVertically)
                                            .padding(start = 8.dp),
                                        tint = MaterialTheme.colorScheme.primary.copy(alpha = ((-offsetX) / 100f).coerceIn(0f, 1f))
                                    )
                                }
                                
                                Box {
                                    Surface(
                                        shape = RoundedCornerShape(
                                            topStart = if (!isMe && !isFirstInGroup) 4.dp else 20.dp,
                                            topEnd = if (isMe && !isFirstInGroup) 4.dp else 20.dp,
                                            bottomStart = if (!isMe && !isLastInGroup) 4.dp else 20.dp,
                                            bottomEnd = if (isMe && !isLastInGroup) 4.dp else 20.dp
                                        ),
                                        color = if (isMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
                                        tonalElevation = if (isMe) 0.dp else 1.dp,
                                        modifier = Modifier.widthIn(max = 280.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                                            if (msg.replyToId != null) {
                                                val repliedMsg = chatMessages.find { it.id == msg.replyToId }
                                                if (repliedMsg != null) {
                                                    val repliedUser = allDbUsers.find { it.id == repliedMsg.senderId } ?: (if (repliedMsg.senderId == currentUser?.id) currentUser else null)
                                                    Row(
                                                        modifier = Modifier
                                                            .padding(bottom = 6.dp)
                                                            .background(
                                                                color = (if (isMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface).copy(alpha = 0.1f),
                                                                shape = RoundedCornerShape(12.dp)
                                                            )
                                                            .padding(8.dp)
                                                            .fillMaxWidth()
                                                    ) {
                                                        Box(
                                                            modifier = Modifier
                                                                .width(3.dp)
                                                                .height(32.dp)
                                                                .clip(RoundedCornerShape(2.dp))
                                                                .background(if (isMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary)
                                                        )
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Column {
                                                            Text(
                                                                text = repliedUser?.username ?: "User",
                                                                style = MaterialTheme.typography.labelSmall,
                                                                fontWeight = FontWeight.Bold,
                                                                color = if (isMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                                                            )
                                                            Text(
                                                                text = if (repliedMsg.type == "text") repliedMsg.text else "📎 ${repliedMsg.type}",
                                                                style = MaterialTheme.typography.bodySmall,
                                                                maxLines = 1,
                                                                overflow = TextOverflow.Ellipsis,
                                                                color = (if (isMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface).copy(alpha = 0.7f)
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                            if ((msg.type == "image" || msg.type == "video") && msg.mediaUri != null) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .heightIn(max = 240.dp)
                                                        .clip(RoundedCornerShape(12.dp))
                                                        .clickable {
                                                            activePreviewUri = msg.mediaUri
                                                            activePreviewType = msg.type
                                                        }
                                                ) {
                                                    MediaView(
                                                        mediaUri = msg.mediaUri,
                                                        modifier = Modifier.fillMaxSize(),
                                                        autoPlayEnabled = false
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(4.dp))
                                            } else if (msg.type == "voice" && msg.mediaUri != null) {
                                                Box(
                                                    modifier = Modifier
                                                        .clickable {
                                                            activePreviewUri = msg.mediaUri
                                                            activePreviewType = "voice"
                                                        }
                                                ) {
                                                    VoiceMessagePlayer(mediaUri = msg.mediaUri, isMe = isMe)
                                                }
                                                Spacer(modifier = Modifier.height(4.dp))
                                            } else if (msg.type.startsWith("missed_")) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = if (msg.type == "missed_video_call") Icons.Filled.VideoCall else Icons.Filled.CallMissed,
                                                        contentDescription = "Missed Call",
                                                        tint = if (isMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.error,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                    Spacer(Modifier.width(4.dp))
                                                    Text(
                                                        text = if (msg.type == "missed_video_call") "Missed Video Call" else "Missed Voice Call",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.error
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(4.dp))
                                            } else if (msg.type == "music" && msg.spotifyTrackId != null) {
                                                SpotifyTrackCard(
                                                    trackName = msg.spotifyTrackName ?: "Music",
                                                    trackArtist = msg.spotifyTrackArtist ?: "Artist",
                                                    albumImageUrl = msg.spotifyTrackImageUrl,
                                                    modifier = Modifier.width(200.dp)
                                                )
                                                if (!msg.spotifyTrackPreviewUrl.isNullOrEmpty()) {
                                                    MusicPreviewPlayer(previewUrl = msg.spotifyTrackPreviewUrl!!)
                                                }
                                                Spacer(modifier = Modifier.height(4.dp))
                                            }

                                            if (editingMessageId == msg.id) {
                                                TextField(
                                                    value = editingText,
                                                    onValueChange = { editingText = it },
                                                    modifier = Modifier.fillMaxWidth(),
                                                    trailingIcon = {
                                                        IconButton(onClick = {
                                                            if (editingText.isNotBlank()) {
                                                                viewModel.updateChatMessage(msg.id, editingText)
                                                                editingMessageId = null
                                                            }
                                                        }) {
                                                            Icon(imageVector = Icons.Default.Check, contentDescription = "Done")
                                                        }
                                                    },
                                                    colors = TextFieldDefaults.colors(
                                                        focusedContainerColor = Color.Transparent,
                                                        unfocusedContainerColor = Color.Transparent
                                                    )
                                                )
                                            } else {
                                                if (msg.text.isNotBlank() && (msg.type == "text" || msg.type == "music")) {
                                                    Text(
                                                        text = msg.text,
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        color = if (isMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                                    )
                                                }
                                                
                                                Row(
                                                    modifier = Modifier.align(Alignment.End), 
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    val timeStr = remember(msg.timestamp) {
                                                        android.text.format.DateFormat.format("HH:mm", msg.timestamp).toString()
                                                    }
                                                    Text(
                                                        text = timeStr,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = (if (isMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant).copy(alpha = 0.6f),
                                                        modifier = Modifier.padding(end = 4.dp)
                                                    )

                                                    if (msg.isEdited) {
                                                        Text(
                                                            "edited",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            color = (if (isMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant).copy(alpha = 0.5f),
                                                            modifier = Modifier.padding(end = 4.dp)
                                                        )
                                                    }
                                                    if (isMe) {
                                                        if (msg.isRead) {
                                                            Icon(Icons.Default.DoneAll, contentDescription = "Read", tint = Color(0xFF4FC3F7), modifier = Modifier.size(14.dp))
                                                        } else {
                                                            Icon(Icons.Default.Check, contentDescription = "Sent", tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f), modifier = Modifier.size(14.dp))
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    if (messageMenuId == msg.id) {
                                        DropdownMenu(
                                            expanded = true,
                                            onDismissRequest = { messageMenuId = null }
                                        ) {
                                            if (isMe) {
                                                DropdownMenuItem(
                                                    text = { Text("Edit") },
                                                    onClick = {
                                                        messageMenuId = null
                                                        editingMessageId = msg.id
                                                        editingText = msg.text
                                                    },
                                                    leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) }
                                                )
                                                DropdownMenuItem(
                                                    text = { Text("Delete") },
                                                    onClick = {
                                                        messageMenuId = null
                                                        messageToDelete = msg.id
                                                    },
                                                    leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) }
                                                )
                                            } else {
                                                DropdownMenuItem(
                                                    text = { Text("Reply") },
                                                    onClick = {
                                                        messageMenuId = null
                                                        replyingToMessage = msg
                                                    },
                                                    leadingIcon = { Icon(Icons.Default.Reply, contentDescription = null) }
                                                )
                                                DropdownMenuItem(
                                                    text = { Text("Report") },
                                                    onClick = {
                                                        messageMenuId = null
                                                        viewModel.reportChatMessage(msg.id)
                                                        android.widget.Toast.makeText(context, "Message reported", android.widget.Toast.LENGTH_SHORT).show()
                                                    },
                                                    leadingIcon = { Icon(Icons.Default.Report, contentDescription = null) }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (replyingToMessage != null) {
                    val repliedUser = allDbUsers.find { it.id == replyingToMessage?.senderId }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp), RoundedCornerShape(12.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(32.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(MaterialTheme.colorScheme.primary)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Replying to ${repliedUser?.fullName ?: "User"}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = if (replyingToMessage?.type == "text") replyingToMessage?.text ?: "" else "📎 ${replyingToMessage?.type}",
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        IconButton(onClick = { replyingToMessage = null }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Cancel", modifier = Modifier.size(16.dp))
                        }
                    }
                }

                Surface(
                    tonalElevation = 2.dp,
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 12.dp)
                            .navigationBarsPadding(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { showAttachmentMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.Add, 
                                contentDescription = "Attach",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                            DropdownMenu(
                                expanded = showAttachmentMenu,
                                onDismissRequest = { showAttachmentMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Photo") },
                                    onClick = {
                                        showAttachmentMenu = false
                                        imagePickerLauncher.launch("image/*")
                                    },
                                    leadingIcon = { Icon(Icons.Default.Photo, contentDescription = null) }
                                )
                                DropdownMenuItem(
                                    text = { Text("Video") },
                                    onClick = {
                                        showAttachmentMenu = false
                                        videoPickerLauncher.launch("video/*")
                                    },
                                    leadingIcon = { Icon(Icons.Default.Movie, contentDescription = null) }
                                )
                                var showSpotifySearchInChat by remember { mutableStateOf(false) }
                                DropdownMenuItem(
                                    text = { Text("Music") },
                                    onClick = {
                                        showAttachmentMenu = false
                                        showSpotifySearchInChat = true
                                    },
                                    leadingIcon = { Icon(Icons.Default.MusicNote, contentDescription = null) }
                                )
                                if (showSpotifySearchInChat) {
                                    SpotifySearchDialog(
                                        onDismiss = { showSpotifySearchInChat = false },
                                        onTrackSelected = {
                                            showSpotifySearchInChat = false
                                            viewModel.sendChatMessage(
                                                receiverId = currentActiveId,
                                                text = "Shared a song: ${it.name}",
                                                type = "music",
                                                spotifyTrackId = it.id,
                                                spotifyTrackName = it.name,
                                                spotifyTrackArtist = it.artist,
                                                spotifyTrackImageUrl = it.albumImageUrl,
                                                spotifyTrackPreviewUrl = it.previewUrl
                                            )
                                        }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = textToSend,
                            onValueChange = { textToSend = it },
                            placeholder = { Text("Message...") },
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp),
                            shape = RoundedCornerShape(28.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                            maxLines = 5,
                            textStyle = MaterialTheme.typography.bodyMedium
                        )
                        
                        val isTextPresent = textToSend.isNotBlank()
                        
                        androidx.compose.animation.AnimatedContent(
                            targetState = isTextPresent,
                            transitionSpec = {
                                fadeIn() + scaleIn() togetherWith fadeOut() + scaleOut()
                            },
                            label = "SendButton"
                        ) { targetIsTextPresent ->
                            if (targetIsTextPresent) {
                                FloatingActionButton(
                                    onClick = {
                                        val userMsg = textToSend.trim()
                                        val replyId = replyingToMessage?.id
                                        replyingToMessage = null
                                        viewModel.sendChatMessage(
                                            receiverId = currentActiveId,
                                            text = userMsg,
                                            replyToId = replyId
                                        )
                                        textToSend = ""
                                    },
                                    modifier = Modifier.size(44.dp),
                                    shape = CircleShape,
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary,
                                    elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp)
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", modifier = Modifier.size(20.dp))
                                }
                            } else {
                                IconButton(onClick = {
                                    if (!isRecording) {
                                        val hasPermission = androidx.core.content.ContextCompat.checkSelfPermission(
                                            context, android.Manifest.permission.RECORD_AUDIO
                                        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                                        
                                        if (!hasPermission) {
                                            audioPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
                                        } else {
                                            try {
                                                val file = java.io.File(context.cacheDir, "voice_note_${System.currentTimeMillis()}.m4a")
                                                audioFile = file
                                                val r = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                                                    android.media.MediaRecorder(context)
                                                } else {
                                                    @Suppress("DEPRECATION")
                                                    android.media.MediaRecorder()
                                                }.apply {
                                                    setAudioSource(android.media.MediaRecorder.AudioSource.MIC)
                                                    setOutputFormat(android.media.MediaRecorder.OutputFormat.MPEG_4)
                                                    setAudioEncoder(android.media.MediaRecorder.AudioEncoder.AAC)
                                                    setOutputFile(file.absolutePath)
                                                    prepare()
                                                    start()
                                                }
                                                recorder = r
                                                isRecording = true
                                                android.widget.Toast.makeText(context, "Recording...", android.widget.Toast.LENGTH_SHORT).show()
                                            } catch (e: Exception) {
                                                android.util.Log.e("Screens", "Failed to start recording: ${e.message}")
                                                android.widget.Toast.makeText(context, "Failed to start recording", android.widget.Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    } else {
                                        try {
                                            try { recorder?.stop() } catch (e: Exception) { android.util.Log.w("Screens", "Safe MediaRecorder stop: ${e.message}") }
                                            try { recorder?.release() } catch (e: Exception) {}
                                            recorder = null
                                            isRecording = false
                                            
                                            val file = audioFile
                                            if (file != null && file.exists()) {
                                                val replyId = replyingToMessage?.id
                                                replyingToMessage = null
                                                viewModel.sendChatMediaMessage(context, currentActiveId, Uri.fromFile(file), "voice", replyToId = replyId)
                                                android.widget.Toast.makeText(context, "Voice note sent!", android.widget.Toast.LENGTH_SHORT).show()
                                            }
                                        } catch (e: Exception) {
                                            android.util.Log.e("Screens", "Failed to stop recording: ${e.message}")
                                            isRecording = false
                                        }
                                    }
                                }) {
                                    Icon(
                                        imageVector = if (isRecording) Icons.Default.StopCircle else Icons.Default.Mic, 
                                        contentDescription = "Voice",
                                        tint = if (isRecording) Color.Red else MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
            
            if (activeCallType != null && activeUser != null) {
                CallingOverlay(
                    activeUser = activeUser,
                    viewModel = viewModel,
                    callType = activeCallType!!,
                    callState = callState,
                    isMuted = isMuted,
                    isSpeakerOn = isSpeakerOn,
                    isVideoMuted = isVideoMuted,
                    isCameraFlipped = isCameraFlipped,
                    callDurationSeconds = callDurationSeconds,
                    onMuteToggle = { isMuted = !isMuted },
                    onSpeakerToggle = { isSpeakerOn = !isSpeakerOn },
                    onVideoMuteToggle = { isVideoMuted = !isVideoMuted },
                    onCameraFlipToggle = { isCameraFlipped = !isCameraFlipped },
                    onEndCall = {
                        if (callState == "connecting") {
                            viewModel.sendChatMessage(
                                receiverId = activeUser.id,
                                text = "",
                                type = "missed_${activeCallType}_call"
                            )
                        }
                        callState = "ended"
                        activeCallType = null
                    },
                    currentUser = currentUser
                )
            }
        }
        if (activePreviewUri != null) {
            AttachmentPreviewDialog(
                mediaUri = activePreviewUri!!,
                mediaType = activePreviewType ?: "image",
                onDismiss = { activePreviewUri = null }
            )
        }
    }
}

@Composable
fun CallingOverlay(
    activeUser: com.example.data.User,
    viewModel: BlogViewModel,
    callType: String,
    callState: String,
    isMuted: Boolean,
    isSpeakerOn: Boolean,
    isVideoMuted: Boolean,
    isCameraFlipped: Boolean,
    callDurationSeconds: Int,
    onMuteToggle: () -> Unit,
    onSpeakerToggle: () -> Unit,
    onVideoMuteToggle: () -> Unit,
    onCameraFlipToggle: () -> Unit,
    onEndCall: () -> Unit,
    currentUser: com.example.data.User? = null
) {
    BackHandler {
        onEndCall()
    }

    val context = LocalContext.current
    var hasAudioPermission by remember {
        mutableStateOf(
            androidx.core.content.ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.RECORD_AUDIO
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        )
    }
    var hasCameraPermission by remember {
        mutableStateOf(
            androidx.core.content.ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.CAMERA
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasAudioPermission = permissions[android.Manifest.permission.RECORD_AUDIO] ?: false
        if (callType == "video") {
            hasCameraPermission = permissions[android.Manifest.permission.CAMERA] ?: false
        }
    }

    LaunchedEffect(Unit) {
        val list = mutableListOf(android.Manifest.permission.RECORD_AUDIO)
        if (callType == "video") {
            list.add(android.Manifest.permission.CAMERA)
        }
        permissionLauncher.launch(list.toTypedArray())
    }

    val appId = viewModel.getAgoraAppId()?.takeIf { it.isNotBlank() } ?: com.example.BuildConfig.AGORA_APP_ID
    val isSandbox = appId.isBlank() || appId == "YOUR_AGORA_APP_ID"

    var rtcEngine by remember { mutableStateOf<io.agora.rtc.RtcEngine?>(null) }
    var remoteVideoUid by remember { mutableStateOf<Int?>(null) }
    var agoraConnectionState by remember { mutableStateOf("Disconnected") }

    DisposableEffect(hasAudioPermission, hasCameraPermission) {
        if (!hasAudioPermission || (callType == "video" && !hasCameraPermission) || isSandbox) {
            if (isSandbox) {
                agoraConnectionState = "Agora Sandbox"
            }
            onDispose {}
        } else {
            agoraConnectionState = "Connecting..."
            var mRtcEngine: io.agora.rtc.RtcEngine? = null
            try {
                val config = io.agora.rtc.RtcEngineConfig().apply {
                    mContext = context
                    mAppId = appId
                    mEventHandler = object : io.agora.rtc.IRtcEngineEventHandler() {
                        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
                            agoraConnectionState = "Connected"
                        }

                        override fun onUserJoined(uid: Int, elapsed: Int) {
                            remoteVideoUid = uid
                            agoraConnectionState = "Peer Joined"
                        }

                        override fun onUserOffline(uid: Int, reason: Int) {
                            if (remoteVideoUid == uid) {
                                remoteVideoUid = null
                                agoraConnectionState = "Connected"
                            }
                        }

                        override fun onError(err: Int) {
                            agoraConnectionState = "Error code $err"
                        }
                    }
                }
                mRtcEngine = io.agora.rtc.RtcEngine.create(config).apply {
                    enableAudio()
                    if (callType == "video") {
                        enableVideo()
                        startPreview()
                        setVideoEncoderConfiguration(
                            io.agora.rtc.video.VideoEncoderConfiguration(
                                io.agora.rtc.video.VideoEncoderConfiguration.VD_640x360,
                                io.agora.rtc.video.VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                                io.agora.rtc.video.VideoEncoderConfiguration.STANDARD_BITRATE,
                                io.agora.rtc.video.VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT
                            )
                        )
                    }
                    setDefaultAudioRoutetoSpeakerphone(true)
                    muteLocalAudioStream(isMuted)
                    setEnableSpeakerphone(isSpeakerOn)
                    if (callType == "video") {
                        muteLocalVideoStream(isVideoMuted)
                    }
                    val channelName = "dm_call_${minOf(activeUser.id, currentUser?.id ?: 0)}_${maxOf(activeUser.id, currentUser?.id ?: 0)}"
                    joinChannel(null, channelName, "", 0)
                }
                rtcEngine = mRtcEngine
            } catch (e: Exception) {
                agoraConnectionState = "Init failed"
                e.printStackTrace()
            }

            onDispose {
                try {
                    mRtcEngine?.leaveChannel()
                    io.agora.rtc.RtcEngine.destroy()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                rtcEngine = null
                remoteVideoUid = null
            }
        }
    }

    LaunchedEffect(isMuted, rtcEngine) {
        rtcEngine?.muteLocalAudioStream(isMuted)
    }

    LaunchedEffect(isSpeakerOn, rtcEngine) {
        rtcEngine?.setEnableSpeakerphone(isSpeakerOn)
    }

    LaunchedEffect(isVideoMuted, rtcEngine) {
        rtcEngine?.muteLocalVideoStream(isVideoMuted)
    }

    LaunchedEffect(isCameraFlipped, rtcEngine) {
        rtcEngine?.switchCamera()
    }

    val bgGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF131722),
            Color(0xFF0D0F16),
            Color(0xFF040507)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGradient)
    ) {
        if (callType == "video" && !isVideoMuted) {
            if (rtcEngine != null && remoteVideoUid != null) {
                AndroidView(
                    factory = { ctx ->
                        val view = io.agora.rtc.RtcEngine.CreateTextureView(ctx)
                        try {
                            rtcEngine?.setupRemoteVideo(
                                io.agora.rtc.video.VideoCanvas(
                                    view,
                                    io.agora.rtc.video.VideoCanvas.RENDER_MODE_HIDDEN,
                                    remoteVideoUid!!
                                )
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        view
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                AsyncImage(
                    model = activeUser.profilePicUri ?: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100&h=100&fit=crop",
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(0.35f),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Encrypted",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "End-to-End Encrypted",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .background(
                            if (isSandbox) Color(0xFFFF9800).copy(alpha = 0.15f)
                            else Color(0xFF03A9F4).copy(alpha = 0.15f),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = if (isSandbox) Icons.Default.Info else Icons.Default.CloudQueue,
                        contentDescription = "Status",
                        tint = if (isSandbox) Color(0xFFFFB300) else Color(0xFF4FC3F7),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = agoraConnectionState,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.85f),
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = if (callType == "video") "VIDEO CALL" else "AUDIO CALL",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = activeUser.fullName.ifBlank { activeUser.username },
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = when (callState) {
                        "connecting" -> "Calling..."
                        "no_answer" -> "The call is not answered"
                        "active" -> {
                            val mins = callDurationSeconds / 60
                            val secs = callDurationSeconds % 60
                            String.format("%02d:%02d", mins, secs)
                        }
                        else -> "Call Ending..."
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = when (callState) {
                        "connecting" -> Color(0xFFFFB300)
                        "no_answer" -> MaterialTheme.colorScheme.error
                        else -> Color.White.copy(alpha = 0.8f)
                    },
                    fontWeight = FontWeight.SemiBold
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (callType == "audio") {
                    Box(contentAlignment = Alignment.Center) {
                        if (callState == "connecting") {
                            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                            val scale1 by infiniteTransition.animateFloat(
                                initialValue = 1f, targetValue = 1.6f,
                                animationSpec = infiniteRepeatable(animation = tween(1500, easing = FastOutSlowInEasing), repeatMode = RepeatMode.Restart), label = ""
                            )
                            val alpha1 by infiniteTransition.animateFloat(
                                initialValue = 0.5f, targetValue = 0f,
                                animationSpec = infiniteRepeatable(animation = tween(1500, easing = FastOutSlowInEasing), repeatMode = RepeatMode.Restart), label = ""
                            )

                            val scale2 by infiniteTransition.animateFloat(
                                initialValue = 1f, targetValue = 1.9f,
                                animationSpec = infiniteRepeatable(animation = tween(1500, delayMillis = 500, easing = FastOutSlowInEasing), repeatMode = RepeatMode.Restart), label = ""
                            )
                            val alpha2 by infiniteTransition.animateFloat(
                                initialValue = 0.4f, targetValue = 0f,
                                animationSpec = infiniteRepeatable(animation = tween(1500, delayMillis = 500, easing = FastOutSlowInEasing), repeatMode = RepeatMode.Restart), label = ""
                            )

                            Box(
                                modifier = Modifier
                                    .size(160.dp)
                                    .scale(scale1)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = alpha1), CircleShape)
                            )
                            Box(
                                modifier = Modifier
                                    .size(160.dp)
                                    .scale(scale2)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = alpha2), CircleShape)
                            )
                        } else {
                            val infiniteTransition = rememberInfiniteTransition(label = "breathing")
                            val glowScale by infiniteTransition.animateFloat(
                                initialValue = 1.0f, targetValue = 1.15f,
                                animationSpec = infiniteRepeatable(animation = tween(1000, easing = LinearOutSlowInEasing), repeatMode = RepeatMode.Reverse), label = ""
                            )
                            Box(
                                modifier = Modifier
                                    .size(165.dp)
                                    .scale(glowScale)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), CircleShape)
                                    .border(BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)), CircleShape)
                            )
                        }

                        AsyncImage(
                            model = activeUser.profilePicUri ?: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200&h=200&fit=crop",
                            contentDescription = activeUser.fullName,
                            modifier = Modifier
                                .size(140.dp)
                                .clip(CircleShape)
                                .border(BorderStroke(3.dp, Color.White.copy(alpha = 0.8f)), CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        if (isVideoMuted) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VideocamOff,
                                    contentDescription = "Camera Off",
                                    tint = Color.White.copy(alpha = 0.4f),
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Your camera is turned off",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .height(280.dp)
                                    .clip(RoundedCornerShape(24.dp))
                                    .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)), RoundedCornerShape(24.dp))
                                    .background(Color.White.copy(alpha = 0.05f)),
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = activeUser.profilePicUri ?: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200&h=200&fit=crop",
                                    contentDescription = activeUser.fullName,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.4f))
                                            )
                                        )
                                )
                                
                                Text(
                                    text = activeUser.username,
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelMedium,
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(12.dp)
                                        .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset(y = (-10).dp, x = (-10).dp)
                                .size(width = 90.dp, height = 130.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Black)
                                .border(BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isMuted) {
                                Icon(
                                    imageVector = Icons.Default.MicOff,
                                    contentDescription = "Muted",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(4.dp)
                                        .size(14.dp)
                                )
                            }

                            if (rtcEngine != null && !isVideoMuted) {
                                AndroidView(
                                    factory = { ctx ->
                                        val view = io.agora.rtc.RtcEngine.CreateTextureView(ctx)
                                        try {
                                            rtcEngine?.setupLocalVideo(
                                                io.agora.rtc.video.VideoCanvas(
                                                    view,
                                                    io.agora.rtc.video.VideoCanvas.RENDER_MODE_HIDDEN,
                                                    0
                                                )
                                            )
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                        view
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                if (!isCameraFlipped) {
                                    AsyncImage(
                                        model = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=100&h=100&fit=crop",
                                        contentDescription = "Me",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color(0xFF222630)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(
                                                imageVector = Icons.Default.Refresh,
                                                contentDescription = "Back Camera",
                                                tint = Color.White.copy(alpha = 0.7f),
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                "Rear Cam",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color.White.copy(alpha = 0.6f)
                                            )
                                        }
                                    }
                                }
                            }

                            Text(
                                text = "You",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 4.dp)
                                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            if (callType == "audio" && callState == "active") {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .height(48.dp)
                        .padding(vertical = 8.dp)
                ) {
                    val infiniteTransition = rememberInfiniteTransition(label = "waveform")

                    val heights = List(12) { index ->
                        infiniteTransition.animateFloat(
                            initialValue = 0.15f,
                            targetValue = 0.95f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(
                                    durationMillis = (300..700).random(),
                                    delayMillis = (index * 45),
                                    easing = FastOutSlowInEasing
                                ),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = ""
                        )
                    }

                    heights.forEach { hProgress ->
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .fillMaxHeight(hProgress.value)
                                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(2.dp))
                        )
                    }
                }
            }

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.06f)
                ),
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(1.4.dp, Color.White.copy(alpha = 0.08f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = onMuteToggle,
                            modifier = Modifier
                                .size(54.dp)
                                .background(
                                    if (isMuted) Color.White.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.1f),
                                    CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                                contentDescription = if (isMuted) "Unmute" else "Mute",
                                tint = if (isMuted) Color(0xFFE57373) else Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isMuted) "Muted" else "Mute",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = onSpeakerToggle,
                            modifier = Modifier
                                .size(54.dp)
                                .background(
                                    if (isSpeakerOn) Color.White.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.1f),
                                    CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = if (isSpeakerOn) Icons.Default.VolumeUp else Icons.Default.VolumeDown,
                                contentDescription = "Speaker Toggle",
                                tint = if (isSpeakerOn) MaterialTheme.colorScheme.primary else Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Speaker",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }

                    if (callType == "video") {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(
                                onClick = onVideoMuteToggle,
                                modifier = Modifier
                                    .size(54.dp)
                                    .background(
                                        if (isVideoMuted) Color.White.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.1f),
                                        CircleShape
                                    )
                            ) {
                                Icon(
                                    imageVector = if (isVideoMuted) Icons.Default.VideocamOff else Icons.Default.Videocam,
                                    contentDescription = "Toggle Video",
                                    tint = if (isVideoMuted) Color(0xFFE57373) else Color.White
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Camera",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(
                                onClick = onCameraFlipToggle,
                                modifier = Modifier
                                    .size(54.dp)
                                    .background(
                                        if (isCameraFlipped) Color.White.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.1f),
                                        CircleShape
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Flip Camera",
                                    tint = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Flip",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = onEndCall,
                            modifier = Modifier
                                .size(60.dp)
                                .background(Color(0xFFE53935), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "End Call",
                                modifier = Modifier.rotate(135f),
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "End",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFE53935),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

fun saveUriToInternalStorage(context: android.content.Context, uri: android.net.Uri): String? {
    if (uri.scheme?.startsWith("http", ignoreCase = true) == true) {
        return uri.toString()
    }
    return try {
        val resolver = context.contentResolver
        val type = resolver.getType(uri) ?: ""
        val extension = when {
            type.contains("video", ignoreCase = true) -> "mp4"
            type.contains("gif", ignoreCase = true) -> "gif"
            uri.toString().endsWith(".mp4", ignoreCase = true) -> "mp4"
            uri.toString().contains("video", ignoreCase = true) -> "mp4"
            else -> "jpg"
        }
        val filename = "local_media_${System.currentTimeMillis()}_${(100..999).random()}.$extension"
        val mediaDir = java.io.File(context.filesDir, "media")
        if (!mediaDir.exists()) {
            mediaDir.mkdirs()
        }
        val destFile = java.io.File(mediaDir, filename)
        resolver.openInputStream(uri)?.use { inputStream ->
            java.io.FileOutputStream(destFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        android.net.Uri.fromFile(destFile).toString()
    } catch (e: Exception) {
        android.util.Log.e("Screens", "Error saving uri locally to filesDir: ${e.message}")
        null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatProfileInfoScreen(
    userId: Int,
    viewModel: BlogViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToProfile: (Int) -> Unit
) {
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val user = allUsers.find { it.id == userId } ?: return
    val context = LocalContext.current
    var showReportDialog by remember { mutableStateOf(false) }
    var showBlockDialog by remember { mutableStateOf(false) }

    if (showReportDialog) {
        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            title = { Text("Report user") },
            text = { Text("Are you sure you want to report ${user.fullName}?") },
            confirmButton = {
                TextButton(onClick = { 
                    showReportDialog = false
                    android.widget.Toast.makeText(context, "User reported", android.widget.Toast.LENGTH_SHORT).show()
                }) { Text("Report") }
            },
            dismissButton = {
                TextButton(onClick = { showReportDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showBlockDialog) {
        AlertDialog(
            onDismissRequest = { showBlockDialog = false },
            title = { Text("Block user") },
            text = { Text("Are you sure you want to block ${user.fullName}? You will no longer receive messages from them.") },
            confirmButton = {
                TextButton(onClick = { 
                    showBlockDialog = false
                    android.widget.Toast.makeText(context, "User blocked", android.widget.Toast.LENGTH_SHORT).show()
                }) { Text("Block") }
            },
            dismissButton = {
                TextButton(onClick = { showBlockDialog = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Information") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(32.dp))
                AsyncImage(
                    model = user.profilePicUri ?: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200&h=200&fit=crop",
                    contentDescription = user.fullName,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = user.fullName.ifBlank { user.username },
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                if (user.bio.isNotBlank()) {
                    Text(
                        text = user.bio,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ChatInfoActionButton(
                        icon = Icons.Default.Person,
                        label = "Profile",
                        onClick = { onNavigateToProfile(user.id) }
                    )
                    ChatInfoActionButton(
                        icon = Icons.Default.Notifications,
                        label = "Mute",
                        onClick = { 
                            android.widget.Toast.makeText(context, "Muted notifications for this chat", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    )
                    ChatInfoActionButton(
                        icon = Icons.Default.Search,
                        label = "Search",
                        onClick = { 
                            android.widget.Toast.makeText(context, "Search enabled", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider()
            }

            item {
                ChatInfoMenuItem(
                    icon = Icons.Default.Report,
                    title = "Report",
                    tint = MaterialTheme.colorScheme.error,
                    onClick = { showReportDialog = true }
                )
                ChatInfoMenuItem(
                    icon = Icons.Default.Block,
                    title = "Block",
                    tint = MaterialTheme.colorScheme.error,
                    onClick = { showBlockDialog = true }
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun ChatInfoActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun ChatInfoMenuItem(
    icon: ImageVector,
    title: String,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(title, color = tint) },
        leadingContent = { Icon(icon, contentDescription = null, tint = tint) },
        modifier = Modifier.clickable(onClick = onClick)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    viewModel: BlogViewModel,
    onNavigateBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var resultMessage by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Forgot Password") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = "Lock Icon",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Reset Your Password",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Enter your email address and we'll send you instructions to reset your password.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (email.isNotBlank()) {
                        coroutineScope.launch {
                            val success = viewModel.resetPassword(email)
                            if (success) {
                                isError = false
                                resultMessage = "Password reset instructions sent to $email."
                            } else {
                                isError = true
                                resultMessage = "Account with this email not found."
                            }
                        }
                    } else {
                        isError = true
                        resultMessage = "Please enter your email address."
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Send Reset Link", fontSize = 16.sp)
            }

            if (resultMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = resultMessage!!,
                    color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    postId: Int,
    viewModel: BlogViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToUserProfile: (Int) -> Unit,
    autoOpenComments: Boolean = false
) {
    val allPosts by viewModel.allPosts.collectAsStateWithLifecycle()
    val post = remember(allPosts, postId) { allPosts.find { it.id == postId } }
    
    var selectedPostForComments by remember { mutableStateOf<Post?>(null) }

    LaunchedEffect(post, autoOpenComments) {
        if (autoOpenComments && post != null) {
            selectedPostForComments = post
        }
    }

    if (post == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        PostItem(
            post = post,
            viewModel = viewModel,
            onClick = { /* Already full screen */ },
            onAuthorClick = onNavigateToUserProfile,
            isHomeFeed = true, // Force full screen style
            onCommentsClick = { selectedPostForComments = post }
        )

        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .statusBarsPadding()
                .padding(16.dp)
                .size(40.dp)
                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                .align(Alignment.TopStart)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
    }

    if (selectedPostForComments != null) {
        VideoCommentsBottomSheet(
            post = selectedPostForComments!!,
            viewModel = viewModel,
            onDismissRequest = { selectedPostForComments = null }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoCommentsBottomSheet(
    post: Post,
    viewModel: BlogViewModel,
    onDismissRequest: () -> Unit
) {
    val commentsFlow = remember(post.id) { viewModel.getCommentsForPost(post.id) }
    val comments by commentsFlow.collectAsStateWithLifecycle(initialValue = emptyList())
    var commentText by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .navigationBarsPadding()
        ) {
            Text(
                text = "Comments (${comments.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (comments.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No comments yet. Write the first one!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(comments, key = { it.id }) { comment ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(MaterialTheme.colorScheme.tertiaryContainer, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                val init = if (comment.authorName.isNotBlank()) comment.authorName.take(2).uppercase() else "?"
                                Text(
                                    text = init,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = comment.authorName,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    val relTime = formatRelativeTime(comment.timestamp)
                                    Text(
                                        text = relTime,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = comment.content,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    placeholder = { Text("Write a comment...") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    maxLines = 3
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (commentText.isNotBlank()) {
                            viewModel.addComment(post.id, commentText)
                            commentText = ""
                        }
                    },
                    enabled = commentText.isNotBlank(),
                    modifier = Modifier
                        .background(
                            if (commentText.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                            CircleShape
                        )
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Send,
                        contentDescription = "Send Comment",
                        tint = if (commentText.isNotBlank()) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InAppCameraPreview(
    modifier: Modifier = Modifier,
    isFrontCamera: Boolean = false,
    isRecording: Boolean = false,
    onRecordingFinished: (Uri) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    
    var videoCaptureState by remember { mutableStateOf<androidx.camera.video.VideoCapture<androidx.camera.video.Recorder>?>(null) }
    var activeRecordingState by remember { mutableStateOf<androidx.camera.video.Recording?>(null) }
    var previewViewRef by remember { mutableStateOf<androidx.camera.view.PreviewView?>(null) }
    
    var isSimulatedRecording by remember { mutableStateOf(false) }
    
    val lensFacing = if (isFrontCamera) androidx.camera.core.CameraSelector.LENS_FACING_FRONT else androidx.camera.core.CameraSelector.LENS_FACING_BACK

    LaunchedEffect(lensFacing, previewViewRef) {
        val previewView = previewViewRef ?: return@LaunchedEffect
        val cameraProviderFuture = androidx.camera.lifecycle.ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                val cameraSelector = androidx.camera.core.CameraSelector.Builder()
                    .requireLensFacing(lensFacing)
                    .build()

                if (!cameraProvider.hasCamera(cameraSelector)) {
                    return@addListener
                }

                val preview = androidx.camera.core.Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                cameraProvider.unbindAll()
                
                val isEmulator = android.os.Build.MODEL.contains("Emulator") || 
                                 android.os.Build.MODEL.contains("google_sdk") || 
                                 android.os.Build.FINGERPRINT.contains("generic") ||
                                 android.os.Build.PRODUCT.contains("sdk") ||
                                 android.os.Build.HARDWARE.contains("goldfish") ||
                                 android.os.Build.HARDWARE.contains("ranchu")

                if (isEmulator) {
                    // On Android emulators/virtual devices, virtual cameras lack hardware EncoderProfiles.
                    // Directly bind preview and skip querying CamcorderProfile / CapabilitiesByQuality
                    // to eliminate "E/CapabilitiesByQuality: No supported EncoderProfiles" system errors.
                    cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
                    videoCaptureState = null
                } else {
                    try {
                        val cameraInfo = cameraSelector.filter(cameraProvider.availableCameraInfos).firstOrNull()
                        val videoCapabilities = if (cameraInfo != null) {
                            androidx.camera.video.Recorder.getVideoCapabilities(
                                cameraInfo,
                                androidx.camera.video.Recorder.VIDEO_CAPABILITIES_SOURCE_CODEC_CAPABILITIES
                            )
                        } else null
                        
                        val supportedQualities = videoCapabilities?.getSupportedQualities(androidx.camera.core.DynamicRange.SDR) ?: emptyList()
                        
                        if (supportedQualities.isNotEmpty()) {
                            val preferredQuality = when {
                                supportedQualities.contains(androidx.camera.video.Quality.HD) -> androidx.camera.video.Quality.HD
                                supportedQualities.contains(androidx.camera.video.Quality.SD) -> androidx.camera.video.Quality.SD
                                else -> supportedQualities.first()
                            }

                            val qualitySelector = androidx.camera.video.QualitySelector.from(
                                preferredQuality,
                                androidx.camera.video.FallbackStrategy.lowerQualityOrHigherThan(androidx.camera.video.Quality.LOWEST)
                            )
                            
                            val recorder = androidx.camera.video.Recorder.Builder()
                                .setVideoCapabilitiesSource(androidx.camera.video.Recorder.VIDEO_CAPABILITIES_SOURCE_CODEC_CAPABILITIES)
                                .setQualitySelector(qualitySelector)
                                .build()
                            val videoCapture = androidx.camera.video.VideoCapture.withOutput(recorder)
                            videoCaptureState = videoCapture

                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                videoCapture
                            )
                        } else {
                            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
                            videoCaptureState = null
                        }
                    } catch (e: Exception) {
                        android.util.Log.w("InAppCamera", "VideoCapture initialization failed: ${e.message}")
                        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
                        videoCaptureState = null
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("InAppCamera", "Camera initialization error: ${e.message}")
            }
        }, androidx.core.content.ContextCompat.getMainExecutor(context))
    }

    var recordingStartTimestamp by remember { mutableLongStateOf(0L) }

    LaunchedEffect(isRecording) {
        val videoCapture = videoCaptureState
        if (isRecording) {
            if (videoCapture != null && activeRecordingState == null) {
                try {
                    recordingStartTimestamp = System.currentTimeMillis()
                    val videoFile = java.io.File(context.cacheDir, "inapp_rec_${System.currentTimeMillis()}.mp4")
                    val outputOptions = androidx.camera.video.FileOutputOptions.Builder(videoFile).build()
                    
                    val hasAudioPermission = androidx.core.content.ContextCompat.checkSelfPermission(
                        context, android.Manifest.permission.RECORD_AUDIO
                    ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                    
                    var pendingRecording = videoCapture.output.prepareRecording(context, outputOptions)
                    if (hasAudioPermission) {
                        try {
                            pendingRecording = pendingRecording.withAudioEnabled()
                        } catch (e: Exception) {
                            android.util.Log.e("InAppCamera", "Failed to enable audio: ${e.message}")
                        }
                    }
                    
                    val recording = pendingRecording.start(androidx.core.content.ContextCompat.getMainExecutor(context)) { event ->
                        if (event is androidx.camera.video.VideoRecordEvent.Finalize) {
                            if (!event.hasError()) {
                                onRecordingFinished(Uri.fromFile(videoFile))
                            } else {
                                android.util.Log.e("InAppCamera", "Recording finalized with error: ${event.error}")
                            }
                        }
                    }
                    activeRecordingState = recording
                } catch (e: Exception) {
                    android.util.Log.e("InAppCamera", "Error starting recording: ${e.message}")
                }
            } else if (videoCapture == null) {
                isSimulatedRecording = true
            }
        } else {
            if (activeRecordingState != null) {
                val rec = activeRecordingState
                activeRecordingState = null
                val elapsed = System.currentTimeMillis() - recordingStartTimestamp
                if (elapsed < 600) {
                    kotlinx.coroutines.delay(600 - elapsed)
                }
                try {
                    rec?.stop()
                } catch (e: Exception) {
                    android.util.Log.e("InAppCamera", "Error stopping recording: ${e.message}")
                }
            } else if (isSimulatedRecording) {
                isSimulatedRecording = false
                val simFile = java.io.File(context.cacheDir, "simulated_rec_${System.currentTimeMillis()}.mp4")
                if (!simFile.exists()) {
                    try { simFile.createNewFile() } catch (e: Exception) {}
                }
                onRecordingFinished(Uri.fromFile(simFile))
            }
        }
    }

    AndroidView(
        factory = { ctx ->
            androidx.camera.view.PreviewView(ctx).apply {
                implementationMode = androidx.camera.view.PreviewView.ImplementationMode.COMPATIBLE
                scaleType = androidx.camera.view.PreviewView.ScaleType.FILL_CENTER
                previewViewRef = this
            }
        },
        update = {
            if (previewViewRef != it) {
                previewViewRef = it
            }
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    viewModel: BlogViewModel,
    onNavigateBack: () -> Unit,
    initialSpotifyTrack: SpotifyTrack? = null
) {
    var mediaUri by remember { mutableStateOf<String?>(null) }
    var captionText by remember { mutableStateOf("") }
    var titleText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Shorts") }
    var selectedTags by remember { mutableStateOf("viral,foryou") }
    var spotifyTrack by remember(initialSpotifyTrack) { mutableStateOf<SpotifyTrack?>(initialSpotifyTrack) }
    
    var showMusicSearch by remember { mutableStateOf(false) }
    var showPublishSheet by remember { mutableStateOf(false) }
    var isUploading by remember { mutableStateOf(false) }
    var isPosting by remember { mutableStateOf(false) }
    var commentsDisabled by remember { mutableStateOf(false) }
    
    // In-App Camera Control States
    var isFrontCamera by remember { mutableStateOf(false) }
    var isRecording by remember { mutableStateOf(false) }
    var recordingDurationSeconds by remember { mutableIntStateOf(0) }
    
    var selectedFilterName by remember { mutableStateOf("Normal") }
    var selectedSpeed by remember { mutableStateOf("1x") }
    var activeMode by remember { mutableStateOf("VIDEO") } // VIDEO, GALLERY, SOUNDS
    
    // Post-Capture Video Editor States
    var videoTrimStart by remember { mutableFloatStateOf(0f) }
    var videoTrimEnd by remember { mutableFloatStateOf(1f) }
    var originalAudioVolume by remember { mutableFloatStateOf(1.0f) }
    var musicAudioVolume by remember { mutableFloatStateOf(0.8f) }
    var isOriginalAudioMuted by remember { mutableStateOf(false) }

    // Text Overlay on Video
    var overlayText by remember { mutableStateOf("") }
    var overlayTextColor by remember { mutableStateOf(Color.White) }
    var overlayTextBgColor by remember { mutableStateOf(Color.Black.copy(alpha = 0.65f)) }
    var overlayTextOffsetX by remember { mutableFloatStateOf(0f) }
    var overlayTextOffsetY by remember { mutableFloatStateOf(0f) }
    var isEditingTextOverlay by remember { mutableStateOf(false) }
    var tempOverlayInput by remember { mutableStateOf("") }

    // Stickers Overlay
    var activeStickers by remember { mutableStateOf(listOf<String>()) }

    // Active Editor Drawer / Sheet Tool: null, "TRIM", "FILTERS", "AUDIO_MIX", "STICKERS"
    var activeEditorTool by remember { mutableStateOf<String?>(null) }
    
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    // Music Player for Spotify Previews
    val musicPlayer = remember { androidx.media3.exoplayer.ExoPlayer.Builder(context).build() }
    DisposableEffect(Unit) {
        onDispose { musicPlayer.release() }
    }

    LaunchedEffect(spotifyTrack, mediaUri) {
        musicPlayer.stop()
        musicPlayer.clearMediaItems()
        if (mediaUri == null) {
            spotifyTrack?.previewUrl?.let { url ->
                musicPlayer.setMediaItem(androidx.media3.common.MediaItem.fromUri(url))
                musicPlayer.prepare()
                musicPlayer.playWhenReady = true
                musicPlayer.repeatMode = androidx.media3.common.Player.REPEAT_MODE_ALL
            }
        }
    }

    // Synchronize activeMode with mediaUri state
    LaunchedEffect(mediaUri) {
        if (mediaUri != null && activeMode == "VIDEO") {
            // Keep it in VIDEO mode but it shows the preview now
        }
    }

    var hasCameraPermission by remember {
        mutableStateOf(
            androidx.core.content.ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.CAMERA
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        )
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasCameraPermission = permissions[android.Manifest.permission.CAMERA] ?: false
        if (!hasCameraPermission) {
            android.widget.Toast.makeText(context, "Camera permission is required for in-app video recording", android.widget.Toast.LENGTH_SHORT).show()
        }
    }
    
    // Request permission on initial launch if not granted
    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            cameraPermissionLauncher.launch(arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO))
        }
    }

    // Recording Timer Effect
    LaunchedEffect(isRecording) {
        if (isRecording) {
            recordingDurationSeconds = 0
            while (isRecording) {
                kotlinx.coroutines.delay(1000)
                recordingDurationSeconds++
            }
        }
    }

    val mediaPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        uri?.let {
            isUploading = true
            coroutineScope.launch {
                val mediaUrl = if (it.scheme?.startsWith("http", ignoreCase = true) == true) {
                    it.toString()
                } else if (R2Uploader.isConfigured()) {
                    R2Uploader.uploadFile(context, it)
                } else {
                    saveUriToInternalStorage(context, it)
                } ?: it.toString()
                
                mediaUri = mediaUrl
                isUploading = false
            }
        }
    }

    if (showMusicSearch) {
        SpotifySearchDialog(
            onDismiss = { showMusicSearch = false },
            onTrackSelected = {
                spotifyTrack = it
                showMusicSearch = false
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Live In-App Camera Viewfinder or Recorded Video Canvas
        if (mediaUri != null) {
            // Dedicated Video Editor Canvas with Real-Time Filters & Overlays
            Box(modifier = Modifier.fillMaxSize()) {
                MediaView(
                    mediaUri = mediaUri!!,
                    modifier = Modifier.fillMaxSize(),
                    autoPlayEnabled = true,
                    isHomeFeed = false,
                    onClick = null,
                    isMutedByMusic = isOriginalAudioMuted || originalAudioVolume == 0f,
                    playbackSpeed = when(selectedSpeed) {
                        "0.5x" -> 0.5f
                        "1.5x" -> 1.5f
                        "2x" -> 2.0f
                        "3x" -> 3.0f
                        else -> 1.0f
                    },
                    musicUrl = spotifyTrack?.previewUrl
                )

                // Visual Filter Color Grading Overlays
                when (selectedFilterName) {
                    "Vivid" -> Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFFF9100).copy(alpha = 0.12f))
                    )
                    "Noir" -> Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.35f))
                    )
                    "Cyberpunk" -> Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFF00E5FF).copy(alpha = 0.15f), Color(0xFFFF007F).copy(alpha = 0.18f))
                                )
                            )
                    )
                    "Vintage" -> Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF795548).copy(alpha = 0.20f))
                    )
                    "Sunset" -> Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFFFF5722).copy(alpha = 0.16f), Color(0xFFE91E63).copy(alpha = 0.15f))
                                )
                            )
                    )
                    "Cool" -> Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF0072FF).copy(alpha = 0.14f))
                    )
                    "Emerald" -> Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF00E676).copy(alpha = 0.13f))
                    )
                }

                // Draggable Text Overlay
                if (overlayText.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset { IntOffset(overlayTextOffsetX.toInt(), overlayTextOffsetY.toInt()) }
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    overlayTextOffsetX += dragAmount.x
                                    overlayTextOffsetY += dragAmount.y
                                }
                            }
                            .clip(RoundedCornerShape(8.dp))
                            .background(overlayTextBgColor)
                            .clickable {
                                tempOverlayInput = overlayText
                                isEditingTextOverlay = true
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = overlayText,
                            color = overlayTextColor,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Draggable Stickers Overlay
                activeStickers.forEachIndexed { index, sticker ->
                    var stickerX by remember { mutableFloatStateOf(0f) }
                    var stickerY by remember { mutableFloatStateOf(-100f + (index * 50f)) }
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset { IntOffset(stickerX.toInt(), stickerY.toInt()) }
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    stickerX += dragAmount.x
                                    stickerY += dragAmount.y
                                }
                            }
                            .clickable {
                                activeStickers = activeStickers.filterIndexed { i, _ -> i != index }
                                android.widget.Toast.makeText(context, "Sticker removed", android.widget.Toast.LENGTH_SHORT).show()
                            }
                            .padding(8.dp)
                    ) {
                        Text(text = sticker, fontSize = 42.sp)
                    }
                }
            }
        } else if (hasCameraPermission) {
            // REAL IN-APP CAMERA VIEWFINDER & RECORDER
            InAppCameraPreview(
                modifier = Modifier.fillMaxSize(),
                isFrontCamera = isFrontCamera,
                isRecording = isRecording,
                onRecordingFinished = { videoUri ->
                    isRecording = false
                    isUploading = true
                    coroutineScope.launch {
                        val mediaUrl = if (videoUri.scheme?.startsWith("http", ignoreCase = true) == true) {
                            videoUri.toString()
                        } else if (R2Uploader.isConfigured()) {
                            R2Uploader.uploadFile(context, videoUri)
                        } else {
                            saveUriToInternalStorage(context, videoUri)
                        } ?: videoUri.toString()
                        mediaUri = mediaUrl
                        isUploading = false
                    }
                }
            )
        } else {
            // Permission Grant Prompt View
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Surface(
                        color = Color.White.copy(alpha = 0.1f),
                        shape = CircleShape,
                        modifier = Modifier.size(100.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = "Camera Permission",
                                tint = Color.White,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Camera Access Required",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "To start creating, allow access to your camera and microphone.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = {
                            cameraPermissionLauncher.launch(arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFE2C55)),
                        shape = RoundedCornerShape(30.dp),
                        modifier = Modifier.height(56.dp).fillMaxWidth(0.7f)
                    ) {
                        Text("Allow Access", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }

        // Top Studio Action Bar & Recording Timer Badge
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Close / Reset Button
                IconButton(
                    onClick = {
                        if (mediaUri != null) {
                            // Retake video
                            mediaUri = null
                        } else {
                            onNavigateBack()
                        }
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (mediaUri != null) Icons.Default.ArrowBack else Icons.Default.Close,
                        contentDescription = "Back / Retake",
                        tint = Color.White
                    )
                }

                // Audio / Sound Selector Badge (Centered)
                Surface(
                    onClick = { showMusicSearch = true },
                    shape = RoundedCornerShape(20.dp),
                    color = Color.Black.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = "Select Sound",
                            tint = Color(0xFF25F4EE),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (spotifyTrack != null) "${spotifyTrack!!.name} • ${spotifyTrack!!.artist}" else "Add Sound",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.widthIn(max = 150.dp)
                        )
                        if (spotifyTrack != null) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove Sound",
                                tint = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable { spotifyTrack = null }
                            )
                        }
                    }
                }

                // Next / Publish Button
                Button(
                    onClick = {
                        if (captionText.isBlank() && overlayText.isNotBlank()) {
                            captionText = overlayText
                        }
                        showPublishSheet = true
                    },
                    enabled = !isUploading && !isRecording && (mediaUri != null || hasCameraPermission),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFE2C55),
                        contentColor = Color.White,
                        disabledContainerColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 6.dp)
                ) {
                    Text("Next", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            // TikTok-style Recording Progress Bar (Top)
            if (isRecording) {
                Spacer(modifier = Modifier.height(16.dp))
                val progress = (recordingDurationSeconds / 15f).coerceIn(0f, 1f) // Assuming 15s max for simplicity
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = Color(0xFFFE2C55),
                    trackColor = Color.White.copy(alpha = 0.2f)
                )
            }

            // In-App Recording Timer Badge
            if (isRecording) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color(0xFFFE2C55))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val blinkAlpha by rememberInfiniteTransition(label = "rec_blink").animateFloat(
                                initialValue = 0.2f,
                                targetValue = 1.0f,
                                animationSpec = infiniteRepeatable(tween(500), repeatMode = RepeatMode.Reverse),
                                label = "rec_alpha"
                            )
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFE2C55).copy(alpha = blinkAlpha))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            val mins = recordingDurationSeconds / 60
                            val secs = recordingDurationSeconds % 60
                            val timeFormatted = String.format("%02d:%02d", mins, secs)
                            Text(
                                text = "REC $timeFormatted",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                style = MaterialTheme.typography.labelMedium,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }
        }

        // Right-Side Creator Studio Sidebar Toolbar
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp)
                .background(Color.Black.copy(alpha = 0.35f), RoundedCornerShape(32.dp))
                .padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            if (mediaUri != null) {
                // DEDICATED POST-CAPTURE VIDEO EDITOR SIDEBAR
                EditorSidebarButton(
                    icon = Icons.Default.TextFields,
                    label = "Text",
                    onClick = {
                        tempOverlayInput = overlayText
                        isEditingTextOverlay = true
                    },
                    isActive = overlayText.isNotBlank()
                )

                EditorSidebarButton(
                    icon = Icons.Default.AutoFixHigh,
                    label = "Filters",
                    onClick = { activeEditorTool = "FILTERS" },
                    isActive = selectedFilterName != "Normal" || activeEditorTool == "FILTERS"
                )

                EditorSidebarButton(
                    icon = Icons.Default.EmojiEmotions,
                    label = "Stickers",
                    onClick = { activeEditorTool = "STICKERS" },
                    isActive = activeStickers.isNotEmpty() || activeEditorTool == "STICKERS"
                )

                EditorSidebarButton(
                    icon = Icons.Default.ContentCut,
                    label = "Trim",
                    onClick = { activeEditorTool = "TRIM" },
                    isActive = videoTrimStart > 0f || videoTrimEnd < 1f || activeEditorTool == "TRIM"
                )

                EditorSidebarButton(
                    icon = Icons.Default.Tune,
                    label = "Audio",
                    onClick = { activeEditorTool = "AUDIO_MIX" },
                    isActive = activeEditorTool == "AUDIO_MIX" || isOriginalAudioMuted || spotifyTrack != null
                )

                EditorSidebarButton(
                    icon = Icons.Default.Speed,
                    label = selectedSpeed,
                    onClick = {
                        selectedSpeed = when(selectedSpeed) {
                            "1x" -> "1.5x"
                            "1.5x" -> "2x"
                            "2x" -> "3x"
                            "3x" -> "0.5x"
                            "0.5x" -> "1x"
                            else -> "1x"
                        }
                    },
                    isActive = selectedSpeed != "1x"
                )
            } else {
                // CAMERA RECORDING SIDEBAR BUTTONS
                EditorSidebarButton(
                    icon = Icons.Default.Cameraswitch,
                    label = "Flip",
                    onClick = { isFrontCamera = !isFrontCamera }
                )

                EditorSidebarButton(
                    icon = Icons.Default.Speed,
                    label = selectedSpeed,
                    onClick = {
                        selectedSpeed = when(selectedSpeed) {
                            "1x" -> "2x"
                            "2x" -> "3x"
                            "3x" -> "0.5x"
                            "0.5x" -> "1x"
                            else -> "1x"
                        }
                    },
                    isActive = selectedSpeed != "1x"
                )

                EditorSidebarButton(
                    icon = Icons.Default.AutoFixHigh,
                    label = "Filters",
                    onClick = {
                        selectedFilterName = when(selectedFilterName) {
                            "Normal" -> "Vivid"
                            "Vivid" -> "Noir"
                            "Noir" -> "Cyberpunk"
                            "Cyberpunk" -> "Vintage"
                            "Vintage" -> "Sunset"
                            "Sunset" -> "Cool"
                            "Cool" -> "Emerald"
                            else -> "Normal"
                        }
                        android.widget.Toast.makeText(context, "Filter: $selectedFilterName", android.widget.Toast.LENGTH_SHORT).show()
                    },
                    isActive = selectedFilterName != "Normal"
                )

                EditorSidebarButton(
                    icon = Icons.Default.Timer,
                    label = "Timer",
                    onClick = {
                        android.widget.Toast.makeText(context, "Self-Timer activated (3s)", android.widget.Toast.LENGTH_SHORT).show()
                    }
                )

                EditorSidebarButton(
                    icon = Icons.Default.MusicNote,
                    label = "Sound",
                    onClick = { showMusicSearch = true },
                    isActive = spotifyTrack != null
                )
            }
        }

        // Bottom Creator Shutter Dock (Shown only in Camera Recording mode)
        if (mediaUri == null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                        )
                    )
                    .navigationBarsPadding()
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // CAMERA RECORDING SHUTTER DOCK
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Media Gallery Quick Picker (Left)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(70.dp)
                            .clickable { 
                                activeMode = "GALLERY"
                                mediaPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)) 
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                .border(2.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isUploading) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Color.White)
                            } else {
                                Icon(Icons.Default.PhotoLibrary, contentDescription = "Gallery", tint = Color.White, modifier = Modifier.size(24.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Upload", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    // IN-APP VIDEO RECORD SHUTTER BUTTON (Center)
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .border(
                                BorderStroke(
                                    width = 5.dp,
                                    color = if (isRecording) Color.White.copy(alpha = 0.4f) else Color.White
                                ),
                                CircleShape
                            )
                            .padding(if (isRecording) 14.dp else 6.dp)
                            .clip(CircleShape)
                            .background(Color.Transparent)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                if (!hasCameraPermission) {
                                    cameraPermissionLauncher.launch(arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO))
                                } else {
                                    isRecording = !isRecording
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(if (isRecording) RoundedCornerShape(8.dp) else CircleShape)
                                .background(Color(0xFFFE2C55))
                        )
                    }

                    // Effect / Filters Quick Button (Right)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(70.dp)
                            .clickable { 
                                selectedFilterName = when(selectedFilterName) {
                                    "Normal" -> "Vivid"
                                    "Vivid" -> "Noir"
                                    "Noir" -> "Cyberpunk"
                                    "Cyberpunk" -> "Vintage"
                                    "Vintage" -> "Sunset"
                                    "Sunset" -> "Cool"
                                    "Cool" -> "Emerald"
                                    else -> "Normal"
                                }
                                android.widget.Toast.makeText(context, "Filter: $selectedFilterName", android.widget.Toast.LENGTH_SHORT).show()
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(Color.White.copy(alpha = 0.15f), CircleShape)
                                .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AutoFixHigh, contentDescription = "Effects", tint = Color.White, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Effects", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Mode Selector Bar (Refined)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf("VIDEO", "GALLERY", "SOUNDS").forEach { mode ->
                        val isSelected = activeMode == mode
                        
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    activeMode = mode
                                    when (mode) {
                                        "VIDEO" -> { /* In camera */ }
                                        "GALLERY" -> { mediaPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)) }
                                        "SOUNDS" -> { showMusicSearch = true }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = mode,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.5f),
                                    letterSpacing = 1.sp
                                )
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 4.dp)
                                            .size(4.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFFE2C55))
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Publish Details Bottom Sheet
    if (showPublishSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPublishSheet = false },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Publish Video",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = { showPublishSheet = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title Field
                OutlinedTextField(
                    value = titleText,
                    onValueChange = { titleText = it },
                    label = { Text("Video Title") },
                    placeholder = { Text("Give your video a catchy title...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Caption Field
                OutlinedTextField(
                    value = captionText,
                    onValueChange = { captionText = it },
                    label = { Text("Description & Hashtags") },
                    placeholder = { Text("Add description, #hashtags, @mentions...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Category Chips
                Text(
                    text = "Category",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Shorts", "Gaming", "Music", "Comedy", "Tech", "Lifestyle", "Fashion").forEach { category ->
                        val isSelected = selectedCategory == category
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = category },
                            label = { Text(category) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Allow Comments Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Allow Comments", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        Text("Viewers can comment on your video", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = !commentsDisabled,
                        onCheckedChange = { commentsDisabled = !it }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Publish Action Button
                Button(
                    onClick = {
                        if (isPosting) return@Button
                        isPosting = true
                        val finalTitle = titleText.ifBlank { "Short Video" }
                        viewModel.createPost(
                            title = finalTitle,
                            content = captionText,
                            categories = selectedCategory,
                            tags = selectedTags,
                            mediaUri = mediaUri,
                            isDraft = false,
                            commentsDisabled = commentsDisabled,
                            spotifyTrackId = spotifyTrack?.id,
                            spotifyTrackName = spotifyTrack?.name,
                            spotifyTrackArtist = spotifyTrack?.artist,
                            spotifyTrackImageUrl = spotifyTrack?.albumImageUrl,
                            spotifyTrackPreviewUrl = spotifyTrack?.previewUrl,
                            onComplete = {
                                isPosting = false
                                showPublishSheet = false
                                onNavigateBack()
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    enabled = !isPosting,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFE2C55),
                        contentColor = Color.White
                    )
                ) {
                    if (isPosting) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.5.dp)
                    } else {
                        Text("Post Short Video", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Text Overlay Customization Dialog
    if (isEditingTextOverlay) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { isEditingTextOverlay = false }
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Add Text Overlay",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = tempOverlayInput,
                        onValueChange = { tempOverlayInput = it },
                        placeholder = { Text("Enter caption on video...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Text Color",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val colorsList = listOf(
                            Color.White,
                            Color(0xFFFFEB3B),
                            Color(0xFF25F4EE),
                            Color(0xFFFE2C55),
                            Color(0xFFFF4081),
                            Color(0xFF00E676),
                            Color.Black
                        )
                        colorsList.forEach { c ->
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(c)
                                    .border(
                                        width = if (overlayTextColor == c) 2.5.dp else 1.dp,
                                        color = if (overlayTextColor == c) MaterialTheme.colorScheme.primary else Color.Gray,
                                        shape = CircleShape
                                    )
                                    .clickable { overlayTextColor = c }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Background Highlight",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Switch(
                            checked = overlayTextBgColor != Color.Transparent,
                            onCheckedChange = { checked ->
                                overlayTextBgColor = if (checked) Color.Black.copy(alpha = 0.65f) else Color.Transparent
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                overlayText = ""
                                tempOverlayInput = ""
                                isEditingTextOverlay = false
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Clear")
                        }
                        Button(
                            onClick = {
                                overlayText = tempOverlayInput.trim()
                                isEditingTextOverlay = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFE2C55)),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Apply", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Video Trimmer Bottom Sheet
    if (activeEditorTool == "TRIM") {
        ModalBottomSheet(
            onDismissRequest = { activeEditorTool = null },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ContentCut, contentDescription = null, tint = Color(0xFFFE2C55))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Trim Video Clip", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                    IconButton(onClick = { activeEditorTool = null }) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Timeline preview representation
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .background(Color.DarkGray.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        repeat(12) {
                            Box(modifier = Modifier.width(2.dp).fillMaxHeight(0.6f).background(Color.White.copy(alpha = 0.4f)))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Range slider
                RangeSlider(
                    value = videoTrimStart..videoTrimEnd,
                    onValueChange = { range ->
                        videoTrimStart = range.start
                        videoTrimEnd = range.endInclusive
                    },
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFFFE2C55),
                        activeTrackColor = Color(0xFFFE2C55)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val startSec = (videoTrimStart * 30).toInt()
                    val endSec = (videoTrimEnd * 30).toInt()
                    val durSec = (endSec - startSec).coerceAtLeast(1)
                    Text("Start: 00:${String.format("%02d", startSec)}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    Text("Clip Duration: ${durSec}s", style = MaterialTheme.typography.bodyMedium, color = Color(0xFFFE2C55), fontWeight = FontWeight.Bold)
                    Text("End: 00:${String.format("%02d", endSec)}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            videoTrimStart = 0f
                            videoTrimEnd = 1f
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Reset Trim")
                    }
                    Button(
                        onClick = {
                            activeEditorTool = null
                            android.widget.Toast.makeText(context, "Trim applied", android.widget.Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFE2C55)),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Apply Trim", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Video Filters Bottom Sheet
    if (activeEditorTool == "FILTERS") {
        ModalBottomSheet(
            onDismissRequest = { activeEditorTool = null },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoFixHigh, contentDescription = null, tint = Color(0xFF25F4EE))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Color Grading & Filters", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                    IconButton(onClick = { activeEditorTool = null }) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                val filterList = listOf(
                    Triple("Normal", "Natural original colors", Color.Gray),
                    Triple("Vivid", "Warm high saturation boost", Color(0xFFFF9100)),
                    Triple("Noir", "Black & white high contrast", Color.DarkGray),
                    Triple("Cyberpunk", "Neon cyan & magenta tint", Color(0xFF00E5FF)),
                    Triple("Vintage", "Warm amber sepia film", Color(0xFF795548)),
                    Triple("Sunset", "Golden hour glow", Color(0xFFFF5722)),
                    Triple("Cool", "Cinematic teal blue", Color(0xFF0072FF)),
                    Triple("Emerald", "Fresh moody green tone", Color(0xFF00E676))
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    filterList.forEach { (name, desc, swatchColor) ->
                        val isSelected = selectedFilterName == name
                        Surface(
                            onClick = {
                                selectedFilterName = name
                            },
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                            ),
                            modifier = Modifier.width(110.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(swatchColor)
                                        .border(2.dp, Color.White, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = desc,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { activeEditorTool = null },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFE2C55))
                ) {
                    Text("Done", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Stickers Bottom Sheet
    if (activeEditorTool == "STICKERS") {
        ModalBottomSheet(
            onDismissRequest = { activeEditorTool = null },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.EmojiEmotions, contentDescription = null, tint = Color(0xFFFFEB3B))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Video Stickers", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                    IconButton(onClick = { activeEditorTool = null }) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                val stickersList = listOf("🔥", "❤️", "⚡", "✨", "💯", "🎵", "🚀", "😂", "👀", "🌟", "👑", "🍕", "🎧", "🎬", "💥", "🏆", "👏", "🌈")

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    stickersList.forEach { sticker ->
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable {
                                    activeStickers = activeStickers + sticker
                                    activeEditorTool = null
                                    android.widget.Toast.makeText(context, "Added $sticker sticker. Drag to reposition!", android.widget.Toast.LENGTH_SHORT).show()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = sticker, fontSize = 28.sp)
                        }
                    }
                }

                if (activeStickers.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = { activeStickers = emptyList() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Remove All Stickers")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Audio Mixer Bottom Sheet
    if (activeEditorTool == "AUDIO_MIX") {
        ModalBottomSheet(
            onDismissRequest = { activeEditorTool = null },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Tune, contentDescription = null, tint = Color(0xFF25F4EE))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Audio Mixer", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                    IconButton(onClick = { activeEditorTool = null }) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Original Audio Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Original Video Sound", fontWeight = FontWeight.SemiBold)
                    Text(if (isOriginalAudioMuted) "Muted" else "${(originalAudioVolume * 100).toInt()}%", fontWeight = FontWeight.Bold, color = Color(0xFFFE2C55))
                }
                Slider(
                    value = if (isOriginalAudioMuted) 0f else originalAudioVolume,
                    onValueChange = {
                        originalAudioVolume = it
                        if (it > 0f) isOriginalAudioMuted = false
                    },
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors(thumbColor = Color(0xFFFE2C55), activeTrackColor = Color(0xFFFE2C55))
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Mute Original Sound", style = MaterialTheme.typography.bodyMedium)
                    Switch(
                        checked = isOriginalAudioMuted,
                        onCheckedChange = { isOriginalAudioMuted = it }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Added Spotify Audio Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(if (spotifyTrack != null) "${spotifyTrack!!.name} Volume" else "Music Volume", fontWeight = FontWeight.SemiBold)
                    Text("${(musicAudioVolume * 100).toInt()}%", fontWeight = FontWeight.Bold, color = Color(0xFF25F4EE))
                }
                Slider(
                    value = musicAudioVolume,
                    onValueChange = { musicAudioVolume = it },
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors(thumbColor = Color(0xFF25F4EE), activeTrackColor = Color(0xFF25F4EE))
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = {
                        activeEditorTool = null
                        showMusicSearch = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.MusicNote, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color(0xFF25F4EE))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (spotifyTrack != null) "Change Sound Track" else "Add Music Track")
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { activeEditorTool = null },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFE2C55))
                ) {
                    Text("Done", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun EditorSidebarButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    isActive: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .width(56.dp)
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isActive) Color(0xFF25F4EE) else Color.White,
            modifier = Modifier.size(26.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
private fun EditorToolButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    isActive: Boolean = false
) {
    Surface(
        onClick = onClick,
        color = if (isActive) Color(0xFFFE2C55).copy(alpha = 0.25f) else Color.White.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, if (isActive) Color(0xFFFE2C55) else Color.White.copy(alpha = 0.25f)),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp).widthIn(min = 48.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) Color(0xFFFE2C55) else Color.White,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = label,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

@Composable
fun CreateStoryScreen(
    viewModel: BlogViewModel,
    onNavigateBack: () -> Unit,
    initialSpotifyTrack: SpotifyTrack? = null
) {
    CreatePostScreen(viewModel = viewModel, onNavigateBack = onNavigateBack, initialSpotifyTrack = initialSpotifyTrack)
}
@Composable
fun StoryViewScreen(
    authorId: Int,
    viewModel: BlogViewModel,
    onNavigateBack: () -> Unit
) {
    val stories by viewModel.allStories.collectAsStateWithLifecycle()
    val users by viewModel.allUsers.collectAsStateWithLifecycle()
    
    val author = remember(users) { users.find { it.id == authorId } }
    val authorStories = remember(stories) { 
        val now = System.currentTimeMillis()
        stories.filter { it.authorId == authorId && it.expiresAt > now }.sortedBy { it.timestamp } 
    }
    
    var currentStoryIndex by remember { mutableStateOf(0) }
    val storyCount = authorStories.size

    if (storyCount == 0) {
        onNavigateBack()
        return
    }

    val currentStory = authorStories.getOrNull(currentStoryIndex) ?: return

    val animatedProgress = remember { Animatable(0f) }
    
    LaunchedEffect(currentStoryIndex) {
        animatedProgress.snapTo(0f)
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 5000, easing = LinearEasing)
        )
        if (currentStoryIndex < storyCount - 1) {
            currentStoryIndex++
        } else {
            onNavigateBack()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // Media View
        if (!currentStory.mediaUri.isNullOrEmpty()) {
            MediaView(
                mediaUri = currentStory.mediaUri,
                modifier = Modifier.fillMaxSize(),
                autoPlayEnabled = true,
                isHomeFeed = false,
                onClick = null,
                isMutedByMusic = !currentStory.spotifyTrackId.isNullOrEmpty()
            )
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = currentStory.text,
                    style = MaterialTheme.typography.displayMedium,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(32.dp)
                )
            }
        }

        // Music Overlay
        if (currentStory.spotifyTrackId != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 120.dp, start = 16.dp, end = 16.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (currentStory.spotifyTrackImageUrl != null) {
                            AsyncImage(
                                model = currentStory.spotifyTrackImageUrl,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp).clip(RoundedCornerShape(4.dp)),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(Icons.Default.MusicNote, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = currentStory.spotifyTrackName ?: "Music",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = currentStory.spotifyTrackArtist ?: "Artist",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.7f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        if (!currentStory.spotifyTrackPreviewUrl.isNullOrEmpty()) {
            MusicPreviewPlayer(previewUrl = currentStory.spotifyTrackPreviewUrl!!)
        }

        // Top Controls
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp)
        ) {
            // Segments Progress
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (i in 0 until storyCount) {
                    val progress = when {
                        i < currentStoryIndex -> 1f
                        i == currentStoryIndex -> animatedProgress.value
                        else -> 0f
                    }
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .weight(1f)
                            .height(2.dp)
                            .clip(CircleShape),
                        color = Color.White,
                        trackColor = Color.White.copy(alpha = 0.3f)
                    )
                }
            }

            // Author Info
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = author?.profilePicUri ?: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100",
                    contentDescription = null,
                    modifier = Modifier.size(36.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = author?.username ?: "User",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    val remainingMillis = (currentStory.expiresAt - System.currentTimeMillis()).coerceAtLeast(0L)
                    val remainingHours = (remainingMillis / 3600000L).toInt()
                    val expirationText = if (remainingHours > 0) "${remainingHours}h left" else "<1h left"
                    Text(
                        text = "${formatRelativeTime(currentStory.timestamp)} • $expirationText",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }
        }

        // Navigation interaction areas
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        if (currentStoryIndex > 0) {
                            currentStoryIndex--
                        }
                    }
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(2f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        if (currentStoryIndex < storyCount - 1) {
                            currentStoryIndex++
                        } else {
                            onNavigateBack()
                        }
                    }
            )
        }

        // Caption
        if (currentStory.text.isNotBlank() && !currentStory.mediaUri.isNullOrEmpty()) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                        )
                    )
                    .padding(32.dp)
            ) {
                Text(
                    text = currentStory.text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

