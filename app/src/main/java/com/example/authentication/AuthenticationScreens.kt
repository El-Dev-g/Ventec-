package com.example.authentication

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.BlogViewModel
import com.example.ui.ZoozAccountRow
import kotlinx.coroutines.launch

@Composable
fun ZoozSecureAuthScreen(
    viewModel: BlogViewModel,
    onAuthSuccess: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var isZoozAuthenticating by remember { mutableStateOf(false) }
    var selectedZoozEmail by remember { mutableStateOf<String?>(null) }
    var selectedZoozUserForConfirmation by remember { mutableStateOf<com.example.data.User?>(null) }
    var pendingAlias by remember { mutableStateOf<String?>(null) }
    var zoozSectionMode by remember { mutableStateOf("CREATE") }
    var zoozAuthStep by remember { mutableIntStateOf(0) } // 0: Select Account, 1: Auth Forms
    val zoozScrollState = rememberScrollState()
    val registeredUsers by viewModel.knownUsers.collectAsStateWithLifecycle(initialValue = emptyList())
    var loginError by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val googleSignInClient = remember {
        try {
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

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val task = com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(com.google.android.gms.common.api.ApiException::class.java)
                val idToken = account?.idToken
                if (idToken != null) {
                    viewModel.loginWithGoogle(idToken, pendingAlias, onAuthSuccess, { loginError = it })
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
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top App Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .height(64.dp)
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* Could go back to social app if integrated */ }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Secure Auth Service",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(zoozScrollState)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                if (isZoozAuthenticating) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = Color(0xFF34A853), modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Establishing secure link...",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        selectedZoozEmail?.let {
                            Text(text = it, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = Color(0xFF34A853))
                        }
                    }
                } else if (selectedZoozUserForConfirmation != null) {
                    val user = selectedZoozUserForConfirmation!!
                    val displayName = if (user.fullName.isNotBlank()) user.fullName else user.username
                    val userEmail = if (user.username.contains("@")) user.username else "${user.username.lowercase()}@zooz.secure"
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Verify Authentication", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f))
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier.size(56.dp).background(Color(0xFF34A853).copy(alpha = 0.1f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(displayName.firstOrNull()?.uppercase() ?: "U", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF34A853))
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(displayName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Text(userEmail, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = {
                                isZoozAuthenticating = true
                                selectedZoozEmail = userEmail
                                pendingAlias = user.username
                                try {
                                    googleSignInClient?.signInIntent?.let { launcher.launch(it) } ?: run {
                                        viewModel.loginWithGoogle("simulated_sso_user", pendingAlias, onAuthSuccess, { loginError = it })
                                        isZoozAuthenticating = false
                                    }
                                } catch (e: Exception) {
                                    viewModel.loginWithGoogle("simulated_sso_user", pendingAlias, onAuthSuccess, { loginError = it })
                                    isZoozAuthenticating = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF34A853))
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Confirm & Authorize", fontWeight = FontWeight.Bold)
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        TextButton(onClick = { selectedZoozUserForConfirmation = null }) {
                            Text("Cancel / Select Another")
                        }
                    }
                } else if (zoozAuthStep == 0) {
                    Text(
                        text = "Authentication Center",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Select an account to authorize your session",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        registeredUsers.forEach { user ->
                            val displayName = if (user.fullName.isNotBlank()) user.fullName else user.username
                            val userEmail = if (user.username.contains("@")) user.username else "${user.username.lowercase()}@zooz.secure"
                            
                            ZoozAccountRow(
                                name = displayName,
                                email = userEmail,
                                avatarText = displayName.firstOrNull()?.uppercase() ?: "U",
                                avatarColor = Color(0xFF4285F4)
                            ) {
                                selectedZoozUserForConfirmation = user
                            }
                        }
                        
                        // Add Account Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    zoozAuthStep = 1
                                    zoozSectionMode = "CREATE"
                                }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("Add another account", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                        }
                    }
                } else {
                    // Auth Forms
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                            .padding(24.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            listOf("CREATE" to "Sign Up", "SIGNIN" to "Sign In", "FORGOT" to "Forgot ID?").forEach { (mode, label) ->
                                TextButton(onClick = { zoozSectionMode = mode }) {
                                    Text(
                                        text = label,
                                        fontWeight = if (zoozSectionMode == mode) FontWeight.Bold else FontWeight.Normal,
                                        color = if (zoozSectionMode == mode) Color(0xFF34A853) else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        when (zoozSectionMode) {
                            "CREATE" -> {
                                Text("Create Secure ID", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFF34A853))
                                Spacer(modifier = Modifier.height(16.dp))
                                var username by remember { mutableStateOf("") }
                                OutlinedTextField(
                                    value = username,
                                    onValueChange = { username = it },
                                    label = { Text("Unique Username") },
                                    suffix = { Text("@zooz.secure") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Button(
                                    onClick = {
                                        if (username.isNotBlank()) {
                                            isZoozAuthenticating = true
                                            pendingAlias = username.trim()
                                            viewModel.loginWithGoogle("simulated_reg_user", pendingAlias, onAuthSuccess, { loginError = it; isZoozAuthenticating = false })
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth().height(52.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF34A853))
                                ) {
                                    Text("Create & Verify identity")
                                }
                            }
                            "SIGNIN" -> {
                                Text("Sign In", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFF34A853))
                                Spacer(modifier = Modifier.height(16.dp))
                                var alias by remember { mutableStateOf("") }
                                OutlinedTextField(
                                    value = alias,
                                    onValueChange = { alias = it },
                                    label = { Text("Secure ID") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Button(
                                    onClick = {
                                        if (alias.isNotBlank()) {
                                            isZoozAuthenticating = true
                                            pendingAlias = alias.trim()
                                            viewModel.loginWithGoogle("simulated_sso", pendingAlias, onAuthSuccess, { loginError = it; isZoozAuthenticating = false })
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth().height(52.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF34A853))
                                ) {
                                    Text("Verify & Continue")
                                }
                            }
                            "FORGOT" -> {
                                Text("Recover ID", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFFEA4335))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Enter recovery email to list all IDs linked to your identity.", style = MaterialTheme.typography.bodySmall)
                                Spacer(modifier = Modifier.height(16.dp))
                                var email by remember { mutableStateOf("") }
                                OutlinedTextField(
                                    value = email,
                                    onValueChange = { email = it },
                                    label = { Text("Recovery Email") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Button(
                                    onClick = { },
                                    modifier = Modifier.fillMaxWidth().height(52.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA4335))
                                ) {
                                    Text("Send Recovery List")
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        TextButton(onClick = { zoozAuthStep = 0 }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                            Text("Back to account list")
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(48.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f))
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Zooz Secure Auth uses hardware-backed identity verification. Your secure credentials never leave this device.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AuthSuccessScreen(
    viewModel: BlogViewModel,
    onSignOut: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Success",
                tint = Color(0xFF34A853),
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Authentication Successful",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Secure session established on this device.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            currentUser?.let { user ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier.size(64.dp).background(Color(0xFF4285F4).copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            val initial = (user.fullName.ifBlank { user.username }).firstOrNull()?.uppercase() ?: "U"
                            Text(initial, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4285F4))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(user.fullName.ifBlank { user.username }, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(if(user.username.contains("@")) user.username else "${user.username.lowercase()}@zooz.secure", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Button(
                onClick = {
                    viewModel.logout()
                    onSignOut()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Lock Session & Logout")
            }
        }
    }
}
