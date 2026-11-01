package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Post

val Post.viewsCount: Int get() = if (impressions > 0) impressions else (id * 320 + 450)
val Post.likesCount: Int get() = if (clicks > 0) clicks else (id * 45 + 80)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatorToolsScreen(
    viewModel: BlogViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToPostDetail: (Int) -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val allPosts by viewModel.allPosts.collectAsStateWithLifecycle()
    val followerCountState = remember(currentUser?.id) { viewModel.getFollowerCount(currentUser?.id ?: 1) }
    val followerCount by followerCountState.collectAsStateWithLifecycle(initialValue = 850)
    val walletBalance by viewModel.walletBalance.collectAsStateWithLifecycle()
    val userCoins by viewModel.userCoins.collectAsStateWithLifecycle()
    val boostCampaigns by viewModel.boostCampaigns.collectAsStateWithLifecycle()

    val creatorPosts = remember(allPosts, currentUser?.id) {
        allPosts.filter { it.authorId == (currentUser?.id ?: 1) || it.authorName == (currentUser?.username ?: "Creator") }
    }
    
    val total30DayViews = remember(creatorPosts) {
        val sum = creatorPosts.sumOf { it.viewsCount }
        if (sum < 8400) 8400 else sum
    }

    var simulateUnlocked by remember { mutableStateOf(false) }
    val isEligible = (followerCount >= 1000 && total30DayViews >= 10000) || simulateUnlocked

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Analytics, 1: Monetization, 2: Audience, 3: Boost
    var showBoostDialog by remember { mutableStateOf(false) }
    var showCashOutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Creator Center", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                        Text(
                            text = if (isEligible) "Verified Creator Program" else "Eligibility Progress",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isEligible) Color(0xFF25F4EE) else Color(0xFFFF9100)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Demo Mode Switch
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = "Demo",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Switch(
                            checked = simulateUnlocked,
                            onCheckedChange = { simulateUnlocked = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFFFE2C55)
                            )
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (!isEligible) {
                // LOCKED / ELIGIBILITY PROGRESS VIEW
                EligibilityProgressView(
                    followerCount = followerCount,
                    targetFollowers = 1000,
                    viewsCount = total30DayViews,
                    targetViews = 10000,
                    onSimulateToggle = { simulateUnlocked = true }
                )
            } else {
                // UNLOCKED CREATOR STUDIO HUB
                Column(modifier = Modifier.fillMaxSize()) {
                    // Studio Sub-Tab Bar
                    PrimaryTabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = Color(0xFFFE2C55)
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("Analytics", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                            icon = { Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, modifier = Modifier.size(18.dp)) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("Earnings", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                            icon = { Icon(Icons.Default.MonetizationOn, contentDescription = null, modifier = Modifier.size(18.dp)) }
                        )
                        Tab(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            text = { Text("Audience", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                            icon = { Icon(Icons.Default.Group, contentDescription = null, modifier = Modifier.size(18.dp)) }
                        )
                        Tab(
                            selected = selectedTab == 3,
                            onClick = { selectedTab = 3 },
                            text = { Text("Boost", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                            icon = { Icon(Icons.Default.RocketLaunch, contentDescription = null, modifier = Modifier.size(18.dp)) }
                        )
                    }

                    Box(modifier = Modifier.fillMaxSize()) {
                        when (selectedTab) {
                            0 -> VideoAnalyticsTab(creatorPosts = creatorPosts, onNavigateToPostDetail = onNavigateToPostDetail)
                            1 -> MonetizationTab(walletBalance = walletBalance, onCashOutClick = { showCashOutDialog = true })
                            2 -> AudienceDemographicsTab(followerCount = followerCount)
                            3 -> AdBoostTab(
                                creatorPosts = creatorPosts,
                                userCoins = userCoins,
                                campaigns = boostCampaigns,
                                onLaunchBoost = { showBoostDialog = true }
                            )
                        }
                    }
                }
            }
        }
    }

    // Boost Campaign Modal
    if (showBoostDialog) {
        CreateBoostCampaignModal(
            creatorPosts = creatorPosts,
            userCoins = userCoins,
            viewModel = viewModel,
            onDismiss = { showBoostDialog = false }
        )
    }

    // Cash-Out Modal
    if (showCashOutDialog) {
        CashOutModal(
            walletBalance = walletBalance,
            viewModel = viewModel,
            onDismiss = { showCashOutDialog = false }
        )
    }
}

// -----------------------------------------------------------------------------
// 1. ELIGIBILITY PROGRESS VIEW
// -----------------------------------------------------------------------------
@Composable
private fun EligibilityProgressView(
    followerCount: Int,
    targetFollowers: Int,
    viewsCount: Int,
    targetViews: Int,
    onSimulateToggle: () -> Unit
) {
    val followerProgress = (followerCount.toFloat() / targetFollowers).coerceIn(0f, 1f)
    val viewProgress = (viewsCount.toFloat() / targetViews).coerceIn(0f, 1f)
    val overallPercent = (((followerProgress + viewProgress) / 2f) * 100).toInt()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color(0xFFFF9100).copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(Color(0xFFFF9100).copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFFF9100), modifier = Modifier.size(32.dp))
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Unlock Creator Tools", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Reach the eligibility requirements below to access Analytics, Diamond Cash-Outs, Demographics & Ad Boosts.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Overall Readiness", fontWeight = FontWeight.SemiBold)
                            Text("$overallPercent%", fontWeight = FontWeight.ExtraBold, color = Color(0xFFFF9100), fontSize = 18.sp)
                        }
                    }
                }
            }
        }

        // Requirement 1: Followers
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Group, contentDescription = null, tint = Color(0xFFFE2C55), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("1,000 Followers Goal", fontWeight = FontWeight.Bold)
                        }
                        Text("$followerCount / 1,000", fontWeight = FontWeight.Bold, color = if (followerCount >= 1000) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurface)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { followerProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFFFE2C55),
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }

        // Requirement 2: Views
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFF25F4EE), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("10,000 Views Goal (30 Days)", fontWeight = FontWeight.Bold)
                        }
                        Text("$viewsCount / 10,000", fontWeight = FontWeight.Bold, color = if (viewsCount >= 10000) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurface)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { viewProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFF25F4EE),
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }

        // Strategy Tips Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Tips to Reach Eligibility Fast 💡", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    TipRow(icon = Icons.Default.Schedule, title = "Post Daily", desc = "Share 2-3 short reels daily during peak evening hours.")
                    Spacer(modifier = Modifier.height(8.dp))
                    TipRow(icon = Icons.Default.MusicNote, title = "Trending Sounds", desc = "Use viral audio tracks to boost FYP recommendations.")
                    Spacer(modifier = Modifier.height(8.dp))
                    TipRow(icon = Icons.Default.Forum, title = "Comment Engagement", desc = "Reply to top video comments with video responses.")
                }
            }
        }

        // Demo Unlock Action
        item {
            Button(
                onClick = onSimulateToggle,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFE2C55))
            ) {
                Icon(Icons.Default.LockOpen, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Simulate Eligible Creator Access", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun TipRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, desc: String) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(Color(0xFF25F4EE).copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFF25F4EE), modifier = Modifier.size(16.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Text(desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

// -----------------------------------------------------------------------------
// 2. TAB 1: VIDEO PERFORMANCE & WATCH-TIME ANALYTICS
// -----------------------------------------------------------------------------
@Composable
private fun VideoAnalyticsTab(
    creatorPosts: List<Post>,
    onNavigateToPostDetail: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // High Level Metrics Grid
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricCard(modifier = Modifier.weight(1f), title = "Total Views", value = "24.8K", trend = "+18.2%", icon = Icons.Default.RemoveRedEye, color = Color(0xFF25F4EE))
                MetricCard(modifier = Modifier.weight(1f), title = "Avg Watch Time", value = "14.2s", trend = "+2.4s", icon = Icons.Default.Timer, color = Color(0xFFFF4081))
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricCard(modifier = Modifier.weight(1f), title = "Completion Rate", value = "68%", trend = "+5.1%", icon = Icons.Default.CheckCircle, color = Color(0xFF4CAF50))
                MetricCard(modifier = Modifier.weight(1f), title = "Engagement Rate", value = "12.4%", trend = "+1.8%", icon = Icons.Default.Favorite, color = Color(0xFFFE2C55))
            }
        }

        // Weekly Views Bar Chart
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Weekly Views Trend", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))

                    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                    val heights = listOf(0.4f, 0.65f, 0.5f, 0.85f, 0.7f, 0.95f, 0.8f)

                    Row(
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        days.forEachIndexed { idx, day ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .width(22.dp)
                                        .fillMaxHeight(heights[idx])
                                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(Color(0xFFFE2C55), Color(0xFF25F4EE))
                                            )
                                        )
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(day, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }

        // Top Performing Videos
        item {
            Text("Top Video Performance Rankings", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        if (creatorPosts.isEmpty()) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(12.dp)) {
                    Text("No videos created yet. Publish a reel to start tracking analytics!", modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            items(creatorPosts.sortedByDescending { it.viewsCount }) { post ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToPostDetail(post.id) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PlayCircle, contentDescription = null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(post.title.ifBlank { "Short Video #${post.id}" }, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("👁️ ${post.viewsCount} views", style = MaterialTheme.typography.labelSmall)
                                Text("❤️ ${post.likesCount} likes", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricCard(modifier: Modifier, title: String, value: String, trend: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(32.dp).background(color.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
                }
                Text(trend, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
            Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

// -----------------------------------------------------------------------------
// 3. TAB 2: MONETIZATION & DIAMOND CASH-OUT
// -----------------------------------------------------------------------------
@Composable
private fun MonetizationTab(
    walletBalance: Double,
    onCashOutClick: () -> Unit
) {
    val estimatedDiamonds = (walletBalance * 20).toInt()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A24)),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Accumulated Diamonds Balance", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.7f))
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("💎 $estimatedDiamonds", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold, color = Color(0xFFFFD700))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("≈ $${String.format("%.2f", walletBalance)} USD Available", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onCashOutClick,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700), contentColor = Color.Black)
                    ) {
                        Icon(Icons.Default.AccountBalanceWallet, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cash-Out Funds", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }

        item {
            Text("Recent Gift Earnings & Payouts", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        val transactions = listOf(
            Triple("👑 Crown Gift from @Alex", "💎 +500 ($25.00)", "Completed"),
            Triple("🌹 Rose Gifts (x12) from Supporters", "💎 +60 ($3.00)", "Completed"),
            Triple("💎 Direct Creator Diamond Reward", "💎 +200 ($10.00)", "Completed")
        )

        items(transactions) { (title, amount, status) ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(title, fontWeight = FontWeight.Bold)
                        Text(status, style = MaterialTheme.typography.labelSmall, color = Color(0xFF4CAF50))
                    }
                    Text(amount, fontWeight = FontWeight.Bold, color = Color(0xFFFFD700))
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// 4. TAB 3: AUDIENCE DEMOGRAPHICS
// -----------------------------------------------------------------------------
@Composable
private fun AudienceDemographicsTab(followerCount: Int) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Follower Growth Timeline", fontWeight = FontWeight.Bold)
                        Text("+$followerCount total", fontWeight = FontWeight.Bold, color = Color(0xFF25F4EE))
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("⚡ Your audience grew by +18.4% over the last 30 days.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        item {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Top Viewer Locations", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    LocationBar(country = "🇺🇸 United States", percent = 42)
                    LocationBar(country = "🇬🇧 United Kingdom", percent = 22)
                    LocationBar(country = "🇳🇬 Nigeria", percent = 18)
                    LocationBar(country = "🇧🇷 Brazil", percent = 12)
                }
            }
        }

        item {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Gender & Age Distribution", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Female (58%)", style = MaterialTheme.typography.bodySmall)
                        Text("Male (38%)", style = MaterialTheme.typography.bodySmall)
                        Text("Other (4%)", style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { 0.58f },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFFFF4081),
                        trackColor = Color(0xFF25F4EE)
                    )
                }
            }
        }
    }
}

@Composable
private fun LocationBar(country: String, percent: Int) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(country, style = MaterialTheme.typography.bodyMedium)
            Text("$percent%", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { percent / 100f },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
            color = Color(0xFF25F4EE),
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

// -----------------------------------------------------------------------------
// 5. TAB 4: AD BOOST & PROMOTION MANAGER
// -----------------------------------------------------------------------------
@Composable
private fun AdBoostTab(
    creatorPosts: List<Post>,
    userCoins: Int,
    campaigns: List<CreatorBoostCampaign>,
    onLaunchBoost: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFF25F4EE).copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Promote Video / Ad Boost", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("Available Balance: $userCoins 🪙 Coins", style = MaterialTheme.typography.bodySmall, color = Color(0xFF25F4EE), fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = onLaunchBoost,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFE2C55)),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Icon(Icons.Default.RocketLaunch, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Boost Video", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            Text("Active & Past Promoted Campaigns", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        if (campaigns.isEmpty()) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(12.dp)) {
                    Text("No boost campaigns launched yet. Click Boost Video to start driving FYP reach!", modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            items(campaigns) { campaign ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(campaign.postTitle, fontWeight = FontWeight.Bold)
                            Surface(color = Color(0xFF4CAF50).copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp)) {
                                Text(campaign.status, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Goal: ${campaign.goal} • Budget: ${campaign.budgetCoins} 🪙 Coins", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(10.dp))
                        val progress = (campaign.currentImpressions.toFloat() / campaign.targetImpressions).coerceIn(0f, 1f)
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                            color = Color(0xFF25F4EE)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${campaign.currentImpressions} / ${campaign.targetImpressions} Impressions Delivered", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// 6. MODALS: BOOST CAMPAIGN & CASH-OUT
// -----------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateBoostCampaignModal(
    creatorPosts: List<Post>,
    userCoins: Int,
    viewModel: BlogViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedPost by remember { mutableStateOf(creatorPosts.firstOrNull()) }
    var selectedGoal by remember { mutableStateOf("More Views") }
    var budgetCoinsInput by remember { mutableStateOf("100") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .navigationBarsPadding()
        ) {
            Text("Launch Video Ad Boost", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            Text("Select Campaign Goal", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("More Views", "More Followers", "More Clicks").forEach { goal ->
                    FilterChip(
                        selected = selectedGoal == goal,
                        onClick = { selectedGoal = goal },
                        label = { Text(goal) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Budget (Coins 🪙)", fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = budgetCoinsInput,
                onValueChange = { budgetCoinsInput = it.filter { char -> char.isDigit() } },
                modifier = Modifier.fillMaxWidth(),
                suffix = { Text("🪙 Coins") },
                singleLine = true
            )

            errorMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    val coins = budgetCoinsInput.toIntOrNull() ?: 100
                    viewModel.createBoostCampaign(
                        postId = selectedPost?.id ?: 1,
                        postTitle = selectedPost?.title ?: "Popular Reel",
                        goal = selectedGoal,
                        budgetCoins = coins,
                        onSuccess = {
                            android.widget.Toast.makeText(context, "Boost campaign launched!", android.widget.Toast.LENGTH_SHORT).show()
                            onDismiss()
                        },
                        onError = { err -> errorMessage = err }
                    )
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFE2C55)),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text("Confirm & Boost Video", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CashOutModal(
    walletBalance: Double,
    viewModel: BlogViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var cashOutAmountInput by remember { mutableStateOf(String.format("%.2f", walletBalance)) }
    var selectedMethod by remember { mutableStateOf("PayPal") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .navigationBarsPadding()
        ) {
            Text("Cash-Out Diamond Earnings", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            Text("Select Payout Method", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("PayPal", "Bank Transfer", "Stripe").forEach { method ->
                    FilterChip(
                        selected = selectedMethod == method,
                        onClick = { selectedMethod = method },
                        label = { Text(method) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Withdrawal Amount ($ USD)", fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = cashOutAmountInput,
                onValueChange = { cashOutAmountInput = it },
                modifier = Modifier.fillMaxWidth(),
                prefix = { Text("$ ") },
                singleLine = true
            )

            errorMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    val amount = cashOutAmountInput.toDoubleOrNull() ?: 0.0
                    viewModel.withdrawFunds(
                        amount = amount,
                        method = selectedMethod,
                        onComplete = {
                            android.widget.Toast.makeText(context, "Cash-out of $$amount initiated via $selectedMethod!", android.widget.Toast.LENGTH_LONG).show()
                            onDismiss()
                        },
                        onError = { err -> errorMessage = err }
                    )
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700), contentColor = Color.Black),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text("Confirm Payout Withdrawal", fontWeight = FontWeight.Bold)
            }
        }
    }
}
