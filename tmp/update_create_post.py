import sys

with open('app/src/main/java/com/example/ui/Screens.kt', 'r') as f:
    content = f.read()

start_idx = content.find('fun CreateStoryScreen(')
if start_idx != -1:
    composable_idx = content.rfind('@Composable', 0, start_idx)
    if composable_idx != -1:
        start_idx = composable_idx

    end_idx = content.find('fun StoryViewScreen(')
    if end_idx != -1:
        comp_before = content.rfind('@Composable', 0, end_idx)
        if comp_before != -1:
            end_idx = comp_before

    new_code = '''@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    viewModel: BlogViewModel,
    onNavigateBack: () -> Unit
) {
    var mediaUri by remember { mutableStateOf<String?>(null) }
    var captionText by remember { mutableStateOf("") }
    var titleText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Shorts") }
    var selectedTags by remember { mutableStateOf("viral,foryou") }
    var spotifyTrack by remember { mutableStateOf<SpotifyTrack?>(null) }
    
    var showMusicSearch by remember { mutableStateOf(false) }
    var showPublishSheet by remember { mutableStateOf(false) }
    var isUploading by remember { mutableStateOf(false) }
    var isPosting by remember { mutableStateOf(false) }
    var commentsDisabled by remember { mutableStateOf(false) }
    
    // Video / FX Control States
    var selectedFilterName by remember { mutableStateOf("Normal") }
    var selectedSpeed by remember { mutableStateOf("1x") }
    var activeMode by remember { mutableStateOf("CAM") } // CAM, GALLERY, TEXT
    
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    var cameraFile by remember { mutableStateOf<java.io.File?>(null) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraUri != null) {
            isUploading = true
            coroutineScope.launch {
                val mediaUrl = if (R2Uploader.isConfigured()) {
                    R2Uploader.uploadFile(context, tempCameraUri!!)
                } else {
                    saveUriToInternalStorage(context, tempCameraUri!!)
                } ?: tempCameraUri.toString()
                mediaUri = mediaUrl
                isUploading = false
            }
        }
    }

    val videoCaptureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CaptureVideo()
    ) { success ->
        if (success && tempCameraUri != null) {
            isUploading = true
            coroutineScope.launch {
                val mediaUrl = if (R2Uploader.isConfigured()) {
                    R2Uploader.uploadFile(context, tempCameraUri!!)
                } else {
                    saveUriToInternalStorage(context, tempCameraUri!!)
                } ?: tempCameraUri.toString()
                mediaUri = mediaUrl
                isUploading = false
            }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraGranted = permissions[android.Manifest.permission.CAMERA] ?: false
        if (!cameraGranted) {
            android.widget.Toast.makeText(context, "Camera permission is required to capture video", android.widget.Toast.LENGTH_SHORT).show()
        }
    }
    
    val mediaPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        uri?.let {
            isUploading = true
            coroutineScope.launch {
                val mediaUrl = if (R2Uploader.isConfigured()) {
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
        // Full-Bleed Viewfinder / Video Canvas Preview
        if (mediaUri != null) {
            MediaView(
                mediaUri = mediaUri!!,
                modifier = Modifier.fillMaxSize(),
                autoPlayEnabled = true,
                isHomeFeed = false,
                onClick = null,
                isMutedByMusic = spotifyTrack != null
            )
        } else {
            // Ambient Live Studio Camera Viewfinder Canvas
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF141E30),
                                Color(0xFF243B55),
                                Color(0xFF0F2027)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val pulseTransition = rememberInfiniteTransition(label = "studio_pulse")
                    val pulseScale by pulseTransition.animateFloat(
                        initialValue = 0.95f,
                        targetValue = 1.05f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1200, easing = EaseInOutSine),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "studio_pulse_scale"
                    )
                    
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.08f))
                            .border(BorderStroke(2.dp, Color(0xFF25F4EE).copy(alpha = 0.6f)), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Videocam,
                            contentDescription = "Studio Camera",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(18.dp))
                    
                    Text(
                        text = "SHORT VIDEO STUDIO",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Record or select a video to share with your audience",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        }

        // Top Studio Action Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Close Button
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.Black.copy(alpha = 0.45f), CircleShape)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close Studio", tint = Color.White)
            }

            // Audio / Sound Selector Badge (Centered)
            Surface(
                onClick = { showMusicSearch = true },
                shape = RoundedCornerShape(20.dp),
                color = Color.Black.copy(alpha = 0.55f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
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
                        modifier = Modifier.widthIn(max = 160.dp)
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

            // Next / Post Step Button
            Button(
                onClick = { showPublishSheet = true },
                enabled = !isUploading,
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

        // Right-Side Creator Studio Sidebar Toolbar
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(trailing = 16.dp)
                .background(Color.Black.copy(alpha = 0.35f), RoundedCornerShape(24.dp))
                .padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Speed Toggle
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    selectedSpeed = when(selectedSpeed) {
                        "1x" -> "2x"
                        "2x" -> "0.5x"
                        else -> "1x"
                    }
                }
            ) {
                Icon(Icons.Default.Speed, contentDescription = "Speed", tint = Color.White, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.height(2.dp))
                Text(selectedSpeed, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }

            // Filters FX Toggle
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    selectedFilterName = when(selectedFilterName) {
                        "Normal" -> "Neon"
                        "Neon" -> "Cyber"
                        "Cyber" -> "Warm"
                        else -> "Normal"
                    }
                    android.widget.Toast.makeText(context, "Filter applied: $selectedFilterName", android.widget.Toast.LENGTH_SHORT).show()
                }
            ) {
                Icon(Icons.Default.AutoFixHigh, contentDescription = "Filters", tint = if (selectedFilterName != "Normal") Color(0xFF25F4EE) else Color.White, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.height(2.dp))
                Text(selectedFilterName, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }

            // Timer Toggle
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    android.widget.Toast.makeText(context, "3s Timer set", android.widget.Toast.LENGTH_SHORT).show()
                }
            ) {
                Icon(Icons.Default.Timer, contentDescription = "Timer", tint = Color.White, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.height(2.dp))
                Text("Timer", color = Color.White, fontSize = 10.sp)
            }

            // Sound Toggle
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { showMusicSearch = true }
            ) {
                Icon(Icons.Default.MusicNote, contentDescription = "Music", tint = Color.White, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.height(2.dp))
                Text("Sound", color = Color.White, fontSize = 10.sp)
            }
        }

        // Bottom Creator Shutter Dock & Mode Switcher
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Caption preview overlay on video
            if (captionText.isNotBlank()) {
                Surface(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = captionText,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Studio Shutter Controls Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Media Gallery Quick Picker
                IconButton(
                    onClick = { mediaPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)) },
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                ) {
                    if (isUploading) {
                        CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp, color = Color.White)
                    } else {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Upload Media", tint = Color.White)
                    }
                }

                // Main Shutter / Video Capture Button
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .border(BorderStroke(4.dp, Color.White), CircleShape)
                        .clickable {
                            val hasCameraPermission = androidx.core.content.ContextCompat.checkSelfPermission(
                                context, android.Manifest.permission.CAMERA
                            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                            val hasAudioPermission = androidx.core.content.ContextCompat.checkSelfPermission(
                                context, android.Manifest.permission.RECORD_AUDIO
                            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                            
                            if (!hasCameraPermission || !hasAudioPermission) {
                                cameraPermissionLauncher.launch(arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO))
                            } else {
                                try {
                                    val file = java.io.File(context.cacheDir, "studio_vid_${System.currentTimeMillis()}.mp4")
                                    cameraFile = file
                                    val fileUri = androidx.core.content.FileProvider.getUriForFile(
                                        context,
                                        "${context.packageName}.fileprovider",
                                        file
                                    )
                                    tempCameraUri = fileUri
                                    videoCaptureLauncher.launch(fileUri)
                                } catch (e: Exception) {
                                    android.util.Log.e("Screens", "Error launching camera: ${e.message}")
                                    android.widget.Toast.makeText(context, "Cannot open video camera", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(62.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFE2C55))
                    )
                }

                // Live Camera Photo Shutter
                IconButton(
                    onClick = {
                        val hasCameraPermission = androidx.core.content.ContextCompat.checkSelfPermission(
                            context, android.Manifest.permission.CAMERA
                        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                        if (!hasCameraPermission) {
                            cameraPermissionLauncher.launch(arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO))
                        } else {
                            try {
                                val file = java.io.File(context.cacheDir, "studio_pic_${System.currentTimeMillis()}.jpg")
                                cameraFile = file
                                val fileUri = androidx.core.content.FileProvider.getUriForFile(
                                    context,
                                    "${context.packageName}.fileprovider",
                                    file
                                )
                                tempCameraUri = fileUri
                                cameraLauncher.launch(fileUri)
                            } catch (e: Exception) {
                                android.util.Log.e("Screens", "Error launching photo camera: ${e.message}")
                            }
                        }
                    },
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(Icons.Default.PhotoCamera, contentDescription = "Take Photo", tint = Color.White)
                }
            }

            // Mode Selector Bar
            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf("CAM", "GALLERY", "FX / SOUNDS").forEach { mode ->
                    val isSelected = activeMode == mode
                    Text(
                        text = mode,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.5f),
                        letterSpacing = 1.sp,
                        modifier = Modifier.clickable { activeMode = mode }
                    )
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
}

@Composable
fun CreateStoryScreen(
    viewModel: BlogViewModel,
    onNavigateBack: () -> Unit
) {
    CreatePostScreen(viewModel = viewModel, onNavigateBack = onNavigateBack)
}

'''

content = content[:start_idx] + new_code + content[end_idx:]
with open('app/src/main/java/com/example/ui/Screens.kt', 'w') as f:
    f.write(content)
print('Successfully updated Screens.kt with CreatePostScreen!')
