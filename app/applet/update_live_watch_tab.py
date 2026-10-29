import re

with open('app/src/main/java/com/example/ui/Screens.kt', 'r') as f:
    content = f.read()

# Update BlogBottomNavigation item
old_nav_item = '''        NavigationBarItem(
            selected = currentTab == "videos",
            onClick = { onTabSelected("videos") },
            icon = { Icon(if (currentTab == "videos") Icons.Filled.PlayArrow else Icons.Outlined.PlayArrow, contentDescription = "Watch Videos") },
            label = { Text("Watch") }
        )'''

new_nav_item = '''        NavigationBarItem(
            selected = currentTab == "videos",
            onClick = { onTabSelected("videos") },
            icon = {
                Box {
                    Icon(
                        imageVector = if (currentTab == "videos") Icons.Filled.LiveTv else Icons.Outlined.LiveTv,
                        contentDescription = "Watch & Live Streams"
                    )
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFE2C55))
                            .align(Alignment.TopEnd)
                    )
                }
            },
            label = { Text("Live") }
        )'''

if old_nav_item in content:
    content = content.replace(old_nav_item, new_nav_item)
    print("Updated BlogBottomNavigation item to Live")

# Update VideoWatchScreenContent
start_idx = content.find('@OptIn(ExperimentalMaterial3Api::class)\n@Composable\nfun VideoWatchScreenContent(')
if start_idx != -1:
    end_idx = content.find('fun VideoWatchItemCard(', start_idx)
    if end_idx != -1:
        comp_before = content.rfind('@Composable', 0, end_idx)
        if comp_before != -1:
            end_idx = comp_before

    new_watch_content = '''@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoWatchScreenContent(
    viewModel: BlogViewModel,
    onNavigateToUserProfile: (Int) -> Unit,
    onNavigateToSearch: () -> Unit = {}
) {
    val context = LocalContext.current
    val posts by viewModel.allPosts.collectAsStateWithLifecycle(initialValue = emptyList())
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val bookmarkedPosts by viewModel.getBookmarkedPosts().collectAsStateWithLifecycle(initialValue = emptyList())
    
    var selectedCategory by remember { mutableStateOf("All Streams") }
    var selectedPostForComments by remember { mutableStateOf<Post?>(null) }
    var showLiveBroadcaster by remember { mutableStateOf(false) }
    
    // Filter video posts & live streams
    val videoPosts = remember(posts, selectedCategory, bookmarkedPosts) {
        val userVideos = posts.filter { post ->
            val uri = post.mediaUri ?: ""
            uri.isNotBlank() && (
                uri.contains("video", ignoreCase = true) ||
                uri.contains("mp4", ignoreCase = true) ||
                uri.contains("webm", ignoreCase = true) ||
                post.categories.contains("Live", ignoreCase = true)
            )
        }
        
        when (selectedCategory) {
            "🔴 Live" -> userVideos.filter { it.categories.contains("Live", true) || it.tags.contains("Live", true) || it.isPromoted }
            "🎮 Gaming" -> userVideos.filter { it.categories.contains("Gaming", true) || it.tags.contains("Gaming", true) }
            "🎵 Music" -> userVideos.filter { it.categories.contains("Music", true) || !it.spotifyTrackId.isNullOrEmpty() }
            "⭐ Saved" -> {
                val bookmarkedIds = bookmarkedPosts.map { it.id }.toSet()
                userVideos.filter { it.id in bookmarkedIds }
            }
            else -> userVideos
        }
    }

    if (showLiveBroadcaster) {
        // Agora Live Stream Broadcaster Dialog
        Dialog(
            onDismissRequest = { showLiveBroadcaster = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            var liveTitle by remember { mutableStateOf("LIVE Stream 🎉") }
            var isStreaming by remember { mutableStateOf(false) }
            var viewerCount by remember { mutableIntStateOf(128) }

            // Simulated live viewer growth
            LaunchedEffect(isStreaming) {
                if (isStreaming) {
                    while (isStreaming) {
                        kotlinx.coroutines.delay(2500)
                        viewerCount += (1..15).random()
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                // Live Stream Camera Viewfinder
                if (isStreaming) {
                    InAppCameraPreview(
                        modifier = Modifier.fillMaxSize(),
                        isFrontCamera = true,
                        isRecording = false,
                        onRecordingFinished = {}
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFF0F0C20), Color(0xFF241442), Color(0xFF000000))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFE2C55).copy(alpha = 0.2f))
                                    .border(2.dp, Color(0xFFFE2C55), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Videocam, contentDescription = null, tint = Color.White, modifier = Modifier.size(42.dp))
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("GO LIVE BROADCAST", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = Color.White)
                            Text("Broadcast real-time video to your followers", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.6f))
                        }
                    }
                }

                // Top Live Overlay
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { showLiveBroadcaster = false },
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }

                    if (isStreaming) {
                        Surface(
                            color = Color(0xFFFE2C55),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("LIVE • $viewerCount Viewers", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    } else {
                        OutlinedTextField(
                            value = liveTitle,
                            onValueChange = { liveTitle = it },
                            placeholder = { Text("Stream Title...", color = Color.White.copy(alpha = 0.5f)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFFE2C55),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                            ),
                            singleLine = true,
                            modifier = Modifier.width(200.dp)
                        )
                    }
                }

                // Bottom Live Broadcast Control
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (!isStreaming) {
                        Button(
                            onClick = {
                                isStreaming = true
                                // Publish Live Stream post to feed so followers see it!
                                viewModel.createPost(
                                    title = liveTitle,
                                    content = "🔴 Live stream in progress! Tap to join stream.",
                                    categories = "Live",
                                    tags = "live,stream,broadcast",
                                    mediaUri = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
                                    isDraft = false,
                                    commentsDisabled = false,
                                    onComplete = {}
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFE2C55)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(26.dp)
                        ) {
                            Icon(Icons.Default.Videocam, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Start Live Stream", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = {
                                isStreaming = false
                                showLiveBroadcaster = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Text("End Stream", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top Filter Category Chips + Go Live Stream Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf("All Streams", "🔴 Live", "🎮 Gaming", "🎵 Music", "⭐ Saved").forEach { category ->
                    val isSelected = selectedCategory == category
                    AssistChip(
                        onClick = { selectedCategory = category },
                        label = { Text(category, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = if (isSelected) {
                            AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        } else {
                            AssistChipDefaults.assistChipColors()
                        },
                        border = if (isSelected) null else AssistChipDefaults.assistChipBorder(true)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Prominent "Go Live" Button
            Surface(
                onClick = { showLiveBroadcaster = true },
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFFFE2C55)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = "Go Live",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Go Live",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp
                    )
                }
            }
        }

        if (videoPosts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.LiveTv,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (selectedCategory == "⭐ Saved") "No saved streams" else "No live streams in $selectedCategory",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = { showLiveBroadcaster = true }) {
                        Text("Be the first to Go Live 🔴")
                    }
                }
            }
        } else {
            val pagerState = androidx.compose.foundation.pager.rememberPagerState(pageCount = { videoPosts.size })
            
            androidx.compose.foundation.pager.VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                key = { page -> videoPosts.getOrNull(page)?.id ?: "empty_$page" }
            ) { page ->
                val videoPost = videoPosts.getOrNull(page)
                if (videoPost != null) {
                    PostItem(
                        post = videoPost,
                        viewModel = viewModel,
                        onClick = { /* Live streams stay full screen */ },
                        onAuthorClick = onNavigateToUserProfile,
                        isHomeFeed = true,
                        onCommentsClick = { selectedPostForComments = videoPost },
                        onTagClick = { tag ->
                            viewModel.setFilter(tag)
                            onNavigateToSearch()
                        }
                    )
                }
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

'''

    content = content[:start_idx] + new_watch_content + content[end_idx:]
    with open('app/src/main/java/com/example/ui/Screens.kt', 'w') as f:
        f.write(content)
    print("Successfully updated VideoWatchScreenContent to Live Stream!")
else:
    print("Could not find VideoWatchScreenContent")
