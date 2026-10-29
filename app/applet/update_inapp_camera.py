import sys

with open('app/src/main/java/com/example/ui/Screens.kt', 'r') as f:
    content = f.read()

start_idx = content.find('fun CreatePostScreen(')
if start_idx != -1:
    composable_idx = content.rfind('@Composable', 0, start_idx)
    if composable_idx != -1:
        start_idx = composable_idx

    end_idx = content.find('fun StoryViewScreen(')
    if end_idx != -1:
        comp_before = content.rfind('@Composable', 0, end_idx)
        if comp_before != -1:
            end_idx = comp_before

    new_code = '''@Composable
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
    
    val lensFacing = if (isFrontCamera) androidx.camera.core.CameraSelector.LENS_FACING_FRONT else androidx.camera.core.CameraSelector.LENS_FACING_BACK

    LaunchedEffect(isRecording, videoCaptureState) {
        val videoCapture = videoCaptureState
        if (isRecording && videoCapture != null && activeRecordingState == null) {
            try {
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
                            val uri = Uri.fromFile(videoFile)
                            onRecordingFinished(uri)
                        } else {
                            android.util.Log.e("InAppCamera", "Recording finalized with error: ${event.error}")
                        }
                    }
                }
                activeRecordingState = recording
            } catch (e: Exception) {
                android.util.Log.e("InAppCamera", "Error starting recording: ${e.message}")
            }
        } else if (!isRecording && activeRecordingState != null) {
            try {
                activeRecordingState?.stop()
            } catch (e: Exception) {
                android.util.Log.e("InAppCamera", "Error stopping recording: ${e.message}")
            }
            activeRecordingState = null
        }
    }

    AndroidView(
        factory = { ctx ->
            val previewView = androidx.camera.view.PreviewView(ctx).apply {
                implementationMode = androidx.camera.view.PreviewView.ImplementationMode.COMPATIBLE
                scaleType = androidx.camera.view.PreviewView.ScaleType.FILL_CENTER
            }
            
            val cameraProviderFuture = androidx.camera.lifecycle.ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener({
                try {
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = androidx.camera.core.Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    
                    val recorder = androidx.camera.video.Recorder.Builder()
                        .setQualitySelector(androidx.camera.video.QualitySelector.from(androidx.camera.video.Quality.HIGHEST))
                        .build()
                    val videoCapture = androidx.camera.video.VideoCapture.withOutput(recorder)
                    videoCaptureState = videoCapture
                    
                    val cameraSelector = androidx.camera.core.CameraSelector.Builder()
                        .requireLensFacing(lensFacing)
                        .build()
                    
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        videoCapture
                    )
                } catch (e: Exception) {
                    android.util.Log.e("InAppCamera", "Camera binding error: ${e.message}")
                }
            }, androidx.core.content.ContextCompat.getMainExecutor(ctx))
            
            previewView
        },
        update = { previewView ->
            val cameraProviderFuture = androidx.camera.lifecycle.ProcessCameraProvider.getInstance(context)
            cameraProviderFuture.addListener({
                try {
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = androidx.camera.core.Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    
                    val recorder = androidx.camera.video.Recorder.Builder()
                        .setQualitySelector(androidx.camera.video.QualitySelector.from(androidx.camera.video.Quality.HIGHEST))
                        .build()
                    val videoCapture = androidx.camera.video.VideoCapture.withOutput(recorder)
                    videoCaptureState = videoCapture
                    
                    val cameraSelector = androidx.camera.core.CameraSelector.Builder()
                        .requireLensFacing(lensFacing)
                        .build()
                    
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        videoCapture
                    )
                } catch (e: Exception) {
                    android.util.Log.e("InAppCamera", "Re-bind camera error: ${e.message}")
                }
            }, androidx.core.content.ContextCompat.getMainExecutor(context))
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
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
    
    // In-App Camera Control States
    var isFrontCamera by remember { mutableStateOf(false) }
    var isRecording by remember { mutableStateOf(false) }
    var recordingDurationSeconds by remember { mutableIntStateOf(0) }
    
    var selectedFilterName by remember { mutableStateOf("Normal") }
    var selectedSpeed by remember { mutableStateOf("1x") }
    var activeMode by remember { mutableStateOf("CAM") } // CAM, GALLERY, TEXT
    
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
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
        // Live In-App Camera Viewfinder or Recorded Video Canvas
        if (mediaUri != null) {
            // Preview recorded / selected video in loop
            MediaView(
                mediaUri = mediaUri!!,
                modifier = Modifier.fillMaxSize(),
                autoPlayEnabled = true,
                isHomeFeed = false,
                onClick = null,
                isMutedByMusic = spotifyTrack != null
            )
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
                        val mediaUrl = if (R2Uploader.isConfigured()) {
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
                    .background(Color(0xFF121216)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = "Camera Permission",
                        tint = Color(0xFFFE2C55),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Camera Access Required",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Zooz requires camera permission to record short videos directly inside the app.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            cameraPermissionLauncher.launch(arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFE2C55))
                    ) {
                        Text("Enable Camera", fontWeight = FontWeight.Bold)
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
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (mediaUri != null) Icons.Default.Refresh else Icons.Default.Close,
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
                    onClick = { showPublishSheet = true },
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
                .padding(end = 16.dp)
                .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
                .padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // In-App Flip Camera Toggle
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { isFrontCamera = !isFrontCamera }
            ) {
                Icon(Icons.Default.Cameraswitch, contentDescription = "Flip Camera", tint = Color.White, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.height(2.dp))
                Text("Flip", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }

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
                    android.widget.Toast.makeText(context, "Filter: $selectedFilterName", android.widget.Toast.LENGTH_SHORT).show()
                }
            ) {
                Icon(Icons.Default.AutoFixHigh, contentDescription = "Filters", tint = if (selectedFilterName != "Normal") Color(0xFF25F4EE) else Color.White, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.height(2.dp))
                Text(selectedFilterName, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
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
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Upload Gallery Media", tint = Color.White)
                    }
                }

                // IN-APP VIDEO RECORD SHUTTER BUTTON
                Box(
                    modifier = Modifier
                        .size(84.dp)
                        .clip(CircleShape)
                        .border(
                            BorderStroke(
                                width = if (isRecording) 6.dp else 4.dp,
                                color = if (isRecording) Color(0xFFFE2C55) else Color.White
                            ),
                            CircleShape
                        )
                        .clickable {
                            if (!hasCameraPermission) {
                                cameraPermissionLauncher.launch(arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO))
                            } else {
                                if (mediaUri != null) {
                                    // Retake first
                                    mediaUri = null
                                } else {
                                    // Toggle in-app video recording directly inside the viewfinder!
                                    isRecording = !isRecording
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(if (isRecording) 36.dp else 64.dp)
                            .clip(if (isRecording) RoundedCornerShape(8.dp) else CircleShape)
                            .background(Color(0xFFFE2C55))
                    )
                }

                // Camera Flip Quick Button
                IconButton(
                    onClick = { isFrontCamera = !isFrontCamera },
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(Icons.Default.Cameraswitch, contentDescription = "Flip Camera", tint = Color.White)
                }
            }

            // Mode Selector Bar
            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf("IN-APP VIDEO", "GALLERY", "SOUNDS").forEach { mode ->
                    val isSelected = activeMode == mode || (mode == "IN-APP VIDEO" && activeMode == "CAM")
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
print('Successfully updated Screens.kt with InAppCameraPreview and CreatePostScreen!')
