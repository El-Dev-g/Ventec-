package com.example.ui

import android.net.Uri
import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import java.text.NumberFormat
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
    val giftHistory by viewModel.giftHistory.collectAsStateWithLifecycle()
    val withdrawalHistory by viewModel.withdrawalHistory.collectAsStateWithLifecycle()
    val userCoins by viewModel.userCoins.collectAsStateWithLifecycle()
    val userDiamonds by viewModel.userDiamonds.collectAsStateWithLifecycle()
    val boostCampaigns by viewModel.boostCampaigns.collectAsStateWithLifecycle()

    val creatorPosts = remember(allPosts, currentUser?.id) {
        allPosts.filter { it.authorId == (currentUser?.id ?: 1) || it.authorName == (currentUser?.username ?: "Creator") }
    }
    
    val total30DayViews = remember(creatorPosts) {
        val sum = creatorPosts.sumOf { it.viewsCount }
        if (sum < 12450) 12450 else sum
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
                            icon = { Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, modifier = Modifier.size(20.dp)) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            icon = { Icon(Icons.Default.MonetizationOn, contentDescription = null, modifier = Modifier.size(20.dp)) }
                        )
                        Tab(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            icon = { Icon(Icons.Default.Group, contentDescription = null, modifier = Modifier.size(20.dp)) }
                        )
                        Tab(
                            selected = selectedTab == 3,
                            onClick = { selectedTab = 3 },
                            icon = { Icon(Icons.Default.RocketLaunch, contentDescription = null, modifier = Modifier.size(20.dp)) }
                        )
                    }

                    Box(modifier = Modifier.fillMaxSize()) {
                        when (selectedTab) {
                            0 -> VideoAnalyticsTab(creatorPosts = creatorPosts, onNavigateToPostDetail = onNavigateToPostDetail)
                            1 -> MonetizationTab(
                                userDiamonds = userDiamonds,
                                walletBalance = walletBalance,
                                giftHistory = giftHistory,
                                withdrawalHistory = withdrawalHistory,
                                onCashOutClick = { showCashOutDialog = true }
                            )
                            2 -> AudienceDemographicsTab(followerCount = followerCount)
                            3 -> AdBoostTab(
                                creatorPosts = creatorPosts,
                                userCoins = userCoins,
                                campaigns = boostCampaigns,
                                onLaunchBoost = { showBoostDialog = true },
                                onCampaignClick = { viewModel.recordCampaignClick(it) }
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
    userDiamonds: Int,
    walletBalance: Double,
    giftHistory: List<com.example.ui.GiftTransaction>,
    withdrawalHistory: List<com.example.ui.WithdrawalRecord>,
    onCashOutClick: () -> Unit
) {
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("💎 $userDiamonds", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold, color = Color(0xFFFFD700))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("$${String.format("%.2f", walletBalance)} USD", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)

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

        // Section 1: Payout Withdrawal History
        item {
            Text("Withdrawal History", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        if (withdrawalHistory.isEmpty()) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(12.dp)) {
                    Text("No payout withdrawals yet.", modifier = Modifier.padding(14.dp), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            items(withdrawalHistory) { record ->
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
                            Text("Payout via ${record.method}", fontWeight = FontWeight.Bold)
                            Text(record.status, style = MaterialTheme.typography.labelSmall, color = if (record.status == "Completed") Color(0xFF4CAF50) else Color(0xFFFF9100))
                        }
                        Text("-$${String.format("%.2f", record.amountUsd)} USD", fontWeight = FontWeight.Bold, color = Color(0xFFFF5252))
                    }
                }
            }
        }

        // Section 2: Gift Earnings History
        item {
            Text("Gift Earnings", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        if (giftHistory.isEmpty()) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(12.dp)) {
                    Text("No gifts received yet.", modifier = Modifier.padding(14.dp), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            items(giftHistory) { gift ->
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
                            Text("${gift.giftIcon} ${gift.giftName} Gift", fontWeight = FontWeight.Bold)
                            Text("from @${gift.senderName}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text("💎 +${gift.diamonds}", fontWeight = FontWeight.Bold, color = Color(0xFFFFD700))
                    }
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
    onLaunchBoost: () -> Unit,
    onCampaignClick: (String) -> Unit = {}
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Overview Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color(0xFF25F4EE).copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = Color(0xFFFE2C55), modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Promotions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Balance: $userCoins 🪙 Coins", style = MaterialTheme.typography.bodySmall, color = Color(0xFF25F4EE), fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = onLaunchBoost,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFE2C55)),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New Boost", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // Promoted Campaigns List
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Promoted Campaigns", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("${campaigns.size} active", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        if (campaigns.isEmpty()) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(12.dp)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No active campaigns found", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Tap 'New Boost' above to promote videos, images, links or affiliate products!", textAlign = TextAlign.Center, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            items(campaigns) { campaign ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Asset Badge
                            val (badgeBg, badgeText, badgeIcon) = when (campaign.assetType) {
                                "AFFILIATE" -> Triple(Color(0xFFFFB300).copy(alpha = 0.18f), "🛍️ Affiliate Product", Color(0xFFFFB300))
                                "PLATFORM" -> Triple(Color(0xFF4285F4).copy(alpha = 0.18f), "🌐 ${campaign.platformName ?: "Platform"}", Color(0xFF4285F4))
                                "IMAGE" -> Triple(Color(0xFF25F4EE).copy(alpha = 0.18f), "🖼️ Photo Post", Color(0xFF25F4EE))
                                else -> Triple(Color(0xFFFE2C55).copy(alpha = 0.18f), "🎬 Video Reel", Color(0xFFFE2C55))
                            }
                            Surface(color = badgeBg, shape = RoundedCornerShape(8.dp)) {
                                Text(badgeText, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = badgeIcon, fontWeight = FontWeight.Bold)
                            }

                            // Status Chip
                            Surface(
                                color = if (campaign.status == "ACTIVE") Color(0xFF4CAF50).copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = campaign.status,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (campaign.status == "ACTIVE") Color(0xFF4CAF50) else Color.LightGray,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(campaign.postTitle, fontWeight = FontWeight.Bold, fontSize = 16.sp)

                        // Affiliate details if applicable
                        if (campaign.assetType == "AFFILIATE") {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                campaign.affiliateNetwork?.let {
                                    Text("🏪 $it", style = MaterialTheme.typography.labelSmall, color = Color(0xFFFFB300), fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(10.dp))
                                }
                                campaign.affiliatePrice?.let {
                                    Text("🏷️ $$it USD", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                }
                            }
                            campaign.affiliateCommission?.let {
                                Spacer(modifier = Modifier.height(4.dp))
                                Surface(color = Color(0xFFFFD700).copy(alpha = 0.15f), shape = RoundedCornerShape(6.dp)) {
                                    Text("💰 $it", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = Color(0xFFFFD700))
                                }
                            }
                        }

                        // Platform link if applicable
                        if (campaign.assetType == "PLATFORM" && !campaign.externalUrl.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("🔗 ${campaign.externalUrl}", style = MaterialTheme.typography.labelSmall, color = Color(0xFF4285F4), maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text("🎯 Goal: ${campaign.goal} • 🪙 ${campaign.budgetCoins} Coins • 📅 ${campaign.durationDays} Days", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        Spacer(modifier = Modifier.height(10.dp))
                        val progress = (campaign.currentImpressions.toFloat() / campaign.targetImpressions.coerceAtLeast(1)).coerceIn(0f, 1f)
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = Color(0xFF25F4EE)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("👁️ ${NumberFormat.getNumberInstance().format(campaign.currentImpressions)} / ${NumberFormat.getNumberInstance().format(campaign.targetImpressions)} Views Delivered", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Medium)
                            Text("🖱️ ${campaign.currentClicks} Clicks", style = MaterialTheme.typography.labelSmall, color = Color(0xFF25F4EE), fontWeight = FontWeight.Bold)
                        }

                        // CTA Button & Live Preview
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Active in Main Feed",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF4CAF50),
                                fontWeight = FontWeight.Bold
                            )
                            Button(
                                onClick = {
                                    onCampaignClick(campaign.id)
                                    if (!campaign.externalUrl.isNullOrBlank()) {
                                        try {
                                            val uri = if (!campaign.externalUrl.startsWith("http")) Uri.parse("https://${campaign.externalUrl}") else Uri.parse(campaign.externalUrl)
                                            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                                        } catch (e: Exception) {
                                            android.widget.Toast.makeText(context, "Target URL: ${campaign.externalUrl}", android.widget.Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        android.widget.Toast.makeText(context, "Action: ${campaign.callToAction} recorded", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25F4EE).copy(alpha = 0.15f)),
                                border = BorderStroke(1.dp, Color(0xFF25F4EE)),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text(
                                    campaign.callToAction,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF25F4EE)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    Icons.Default.OpenInNew,
                                    contentDescription = null,
                                    tint = Color(0xFF25F4EE),
                                    modifier = Modifier.size(11.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// 6. MODALS: MULTI-ASSET BOOST CAMPAIGN & CASH-OUT
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

    // Promotion Asset Category: "VIDEO", "IMAGE", "PLATFORM", "AFFILIATE"
    var selectedAssetType by remember { mutableStateOf("VIDEO") }

    // Video & Image asset selection
    var selectedPost by remember { mutableStateOf(creatorPosts.firstOrNull()) }
    var customAssetTitle by remember { mutableStateOf("") }
    var customAssetMediaUri by remember { mutableStateOf("") }

    // Platform fields
    var selectedPlatform by remember { mutableStateOf("YouTube") }
    var platformUrlInput by remember { mutableStateOf("https://youtube.com/@channel") }

    // Affiliate fields
    var affiliateProductName by remember { mutableStateOf("Sony WH-1000XM5 Wireless Headphones") }
    var selectedAffiliateNetwork by remember { mutableStateOf("Amazon Associates") }
    var affiliateProductPriceInput by remember { mutableStateOf("49.99") }
    var affiliateReferralUrl by remember { mutableStateOf("https://amzn.to/3example?tag=zooz-20") }
    var affiliatePromoCode by remember { mutableStateOf("15% OFF with code ZOOZ15") }

    // Campaign settings
    var selectedGoal by remember { mutableStateOf("More Views & FYP Reach") }
    var selectedCta by remember { mutableStateOf("Learn More") }
    var selectedDurationDays by remember { mutableIntStateOf(3) }
    var budgetCoinsInput by remember { mutableStateOf("100") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Real-Time Dynamic Views Projection Calculator
    val parsedCoins = budgetCoinsInput.toIntOrNull() ?: 100
    val minViews = parsedCoins * 25
    val maxViews = parsedCoins * 35
    val avgViews = (minViews + maxViews) / 2
    val minClicks = (parsedCoins * 1.6).toInt().coerceAtLeast(1)
    val maxClicks = (parsedCoins * 2.5).toInt().coerceAtLeast(1)
    val dailyViews = (avgViews / selectedDurationDays.coerceAtLeast(1)).coerceAtLeast(1)
    val costPerThousandViews = String.format("%.2f", (parsedCoins * 0.05) / (avgViews / 1000.0).coerceAtLeast(0.001))

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
        ) {
            Text("Launch Promotion & Ad Boost", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Promote videos, photos, external platforms or affiliate products.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(16.dp))

            // Step 1: Select What to Promote
            Text("1. What do you want to promote?", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Triple("VIDEO", "🎬 Video Reel", Color(0xFFFE2C55)),
                    Triple("IMAGE", "🖼️ Photo Post", Color(0xFF25F4EE)),
                    Triple("PLATFORM", "🌐 Social Channel", Color(0xFF4285F4)),
                    Triple("AFFILIATE", "🛍️ Affiliate Product", Color(0xFFFFB300))
                ).forEach { (type, label, color) ->
                    FilterChip(
                        selected = selectedAssetType == type,
                        onClick = {
                            selectedAssetType = type
                            selectedCta = when (type) {
                                "VIDEO" -> "Watch Reel"
                                "IMAGE" -> "View Photo"
                                "PLATFORM" -> "Visit Channel"
                                "AFFILIATE" -> "Shop Deal"
                                else -> "Learn More"
                            }
                            selectedGoal = when (type) {
                                "AFFILIATE" -> "Affiliate Sales & Conversions"
                                "PLATFORM" -> "More Website / Profile Clicks"
                                else -> "More Views & FYP Reach"
                            }
                        },
                        label = { Text(label, fontWeight = if (selectedAssetType == type) FontWeight.Bold else FontWeight.Normal) },
                        border = if (selectedAssetType == type) BorderStroke(1.dp, color) else null
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Sub-Form Based on Selected Asset Type
            when (selectedAssetType) {
                "VIDEO" -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Select Video to Boost", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            if (creatorPosts.isEmpty()) {
                                OutlinedTextField(
                                    value = customAssetTitle,
                                    onValueChange = { customAssetTitle = it },
                                    label = { Text("Video Title / Reel Caption") },
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = { Text("e.g. Summer Sunset Reel") }
                                )
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    creatorPosts.take(4).forEach { post ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (selectedPost?.id == post.id) Color(0xFFFE2C55).copy(alpha = 0.2f) else Color.Transparent)
                                                .clickable { selectedPost = post }
                                                .padding(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            RadioButton(
                                                selected = selectedPost?.id == post.id,
                                                onClick = { selectedPost = post },
                                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFFE2C55))
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Column {
                                                Text(post.title.ifBlank { "Reel #${post.id}" }, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                                Text("👁️ ${post.viewsCount} views • ❤️ ${post.likesCount} likes", style = MaterialTheme.typography.labelSmall)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                "IMAGE" -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Image / Photo Post Details", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = customAssetTitle,
                                onValueChange = { customAssetTitle = it },
                                label = { Text("Photo Title / Description") },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("e.g. Photography Lookbook Vol. 2") }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = customAssetMediaUri,
                                onValueChange = { customAssetMediaUri = it },
                                label = { Text("Image Link / Banner URL (Optional)") },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("https://example.com/photo.jpg") }
                            )
                        }
                    }
                }

                "PLATFORM" -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Select Third-Party Platform", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("YouTube", "Instagram", "Spotify", "TikTok", "X / Twitter", "Website", "Discord").forEach { platform ->
                                    FilterChip(
                                        selected = selectedPlatform == platform,
                                        onClick = {
                                            selectedPlatform = platform
                                            platformUrlInput = when (platform) {
                                                "YouTube" -> "https://youtube.com/@zoozcreator"
                                                "Instagram" -> "https://instagram.com/zoozcreator"
                                                "Spotify" -> "https://open.spotify.com/artist/zoozcreator"
                                                "TikTok" -> "https://tiktok.com/@zoozcreator"
                                                "X / Twitter" -> "https://x.com/zoozcreator"
                                                else -> "https://mycreatorstore.com"
                                            }
                                        },
                                        label = { Text(platform) }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = customAssetTitle,
                                onValueChange = { customAssetTitle = it },
                                label = { Text("Campaign Headline / Hook") },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("e.g. Follow my $selectedPlatform for exclusive daily content!") }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = platformUrlInput,
                                onValueChange = { platformUrlInput = it },
                                label = { Text("$selectedPlatform Destination URL") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                "AFFILIATE" -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Affiliate Product Details", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = affiliateProductName,
                                onValueChange = { affiliateProductName = it },
                                label = { Text("Product Title") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Affiliate Network / Marketplace", style = MaterialTheme.typography.labelMedium)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("Amazon Associates", "TikTok Shop", "Shopify Store", "ClickBank", "ShareASale").forEach { net ->
                                    FilterChip(
                                        selected = selectedAffiliateNetwork == net,
                                        onClick = { selectedAffiliateNetwork = net },
                                        label = { Text(net) }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = affiliateProductPriceInput,
                                    onValueChange = { affiliateProductPriceInput = it },
                                    label = { Text("Price ($)") },
                                    modifier = Modifier.weight(1f),
                                    prefix = { Text("$") }
                                )
                                OutlinedTextField(
                                    value = affiliatePromoCode,
                                    onValueChange = { affiliatePromoCode = it },
                                    label = { Text("Commission / Promo") },
                                    modifier = Modifier.weight(1.4f)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = affiliateReferralUrl,
                                onValueChange = { affiliateReferralUrl = it },
                                label = { Text("Affiliate Referral URL (with your tag)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Step 2: Campaign Objective
            Text("2. Campaign Objective", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "More Views & FYP Reach",
                    "More Website / Profile Clicks",
                    "Affiliate Sales & Conversions",
                    "More Followers & Subscribers"
                ).forEach { goal ->
                    FilterChip(
                        selected = selectedGoal == goal,
                        onClick = { selectedGoal = goal },
                        label = { Text(goal) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Step 3: Campaign Duration
            Text("3. Campaign Duration", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(1, 3, 7, 14).forEach { days ->
                    FilterChip(
                        selected = selectedDurationDays == days,
                        onClick = { selectedDurationDays = days },
                        label = { Text("$days ${if (days == 1) "Day" else "Days"}") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Step 4: Budget & Dynamic Reach Projection
            Text("4. Budget (Coins 🪙) & Estimated Views", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))

            // Preset Coins Quick Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(50, 100, 250, 500, 1000).forEach { coins ->
                    FilterChip(
                        selected = budgetCoinsInput == coins.toString(),
                        onClick = { budgetCoinsInput = coins.toString() },
                        label = { Text("$coins 🪙 ($${String.format("%.2f", coins * 0.05)})") }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = budgetCoinsInput,
                onValueChange = { budgetCoinsInput = it.filter { char -> char.isDigit() } },
                modifier = Modifier.fillMaxWidth(),
                suffix = { Text("🪙 Coins (~$${String.format("%.2f", parsedCoins * 0.05)} USD)") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            // DYNAMIC VIEWS & RESULTS ESTIMATION CARD (Based on Budget)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF141722)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFF25F4EE).copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("📊 Estimated Results from Budget", fontWeight = FontWeight.Bold, color = Color(0xFF25F4EE))
                        Text("$parsedCoins Coins", fontWeight = FontWeight.Bold, color = Color(0xFFFFD700))
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2232)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("👁️ Estimated Views", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "${NumberFormat.getNumberInstance().format(minViews)} – ${NumberFormat.getNumberInstance().format(maxViews)}",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp,
                                    color = Color(0xFF25F4EE)
                                )
                            }
                        }

                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2232)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(if (selectedAssetType == "AFFILIATE") "🛍️ Est. Orders/Clicks" else "🖱️ Est. Clicks", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "${NumberFormat.getNumberInstance().format(minClicks)} – ${NumberFormat.getNumberInstance().format(maxClicks)}",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp,
                                    color = Color(0xFFFFD700)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("⚡ Daily Pace: ~${NumberFormat.getNumberInstance().format(dailyViews)} views/day for $selectedDurationDays days", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                    Text("💡 Cost Efficiency: ~$${costPerThousandViews} CPM • Targeted directly to FYP audience feeds", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.6f))
                }
            }

            // Coin Balance Check & Quick Top-Up
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Your Balance: $userCoins 🪙 Coins", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = if (userCoins >= parsedCoins) Color(0xFF4CAF50) else Color(0xFFFF5252))
                if (userCoins < parsedCoins) {
                    TextButton(
                        onClick = {
                            viewModel.buyCoins(100, 5.00)
                            android.widget.Toast.makeText(context, "+100 Coins Added!", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Text("+ Add 100 Coins", color = Color(0xFF25F4EE), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            errorMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Launch Button
            Button(
                onClick = {
                    val coins = budgetCoinsInput.toIntOrNull() ?: 100
                    if (coins <= 0) {
                        errorMessage = "Please enter a valid coin budget."
                        return@Button
                    }

                    val title = when (selectedAssetType) {
                        "VIDEO" -> selectedPost?.title?.ifBlank { "Creator Video Reel" } ?: customAssetTitle.ifBlank { "Reel Promotion" }
                        "IMAGE" -> customAssetTitle.ifBlank { "Featured Photo Banner" }
                        "PLATFORM" -> customAssetTitle.ifBlank { "Promoted $selectedPlatform Channel" }
                        "AFFILIATE" -> affiliateProductName.ifBlank { "Affiliate Product Deal" }
                        else -> "Promotion"
                    }

                    val externalUrl = when (selectedAssetType) {
                        "PLATFORM" -> platformUrlInput
                        "AFFILIATE" -> affiliateReferralUrl
                        "IMAGE" -> customAssetMediaUri.ifBlank { null }
                        else -> null
                    }

                    viewModel.createBoostCampaign(
                        postId = if (selectedAssetType == "VIDEO") (selectedPost?.id ?: 1) else null,
                        postTitle = title,
                        assetType = selectedAssetType,
                        assetMediaUri = if (selectedAssetType == "VIDEO") selectedPost?.mediaUri else customAssetMediaUri.ifBlank { null },
                        externalUrl = externalUrl,
                        platformName = if (selectedAssetType == "PLATFORM") selectedPlatform else null,
                        affiliateNetwork = if (selectedAssetType == "AFFILIATE") selectedAffiliateNetwork else null,
                        affiliatePrice = if (selectedAssetType == "AFFILIATE") affiliateProductPriceInput.toDoubleOrNull() else null,
                        affiliateCommission = if (selectedAssetType == "AFFILIATE") affiliatePromoCode else null,
                        callToAction = selectedCta,
                        goal = selectedGoal,
                        budgetCoins = coins,
                        durationDays = selectedDurationDays,
                        onSuccess = {
                            android.widget.Toast.makeText(context, "Promotion launched successfully! FYP boost active.", android.widget.Toast.LENGTH_SHORT).show()
                            onDismiss()
                        },
                        onError = { err -> errorMessage = err }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFE2C55)),
                shape = RoundedCornerShape(26.dp)
            ) {
                Icon(Icons.Default.RocketLaunch, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Confirm & Launch Promotion ($parsedCoins 🪙)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
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
    var accountDetailsInput by remember { mutableStateOf("creator@example.com") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
        ) {
            Text("Cash-Out Diamond Earnings", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Minimum Payout Threshold: $50.00 USD (1 Diamond = $0.05)", style = MaterialTheme.typography.bodySmall, color = Color(0xFFFFD700))
            Spacer(modifier = Modifier.height(16.dp))

            Text("Select Payout Provider", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("PayPal", "Stripe Connect", "Bank Transfer").forEach { method ->
                    FilterChip(
                        selected = selectedMethod == method,
                        onClick = {
                            selectedMethod = method
                            accountDetailsInput = when (method) {
                                "PayPal" -> "creator@example.com"
                                "Stripe Connect" -> "acct_1XYZ99abcdef"
                                else -> "Routing: 021000021, Acc: ****8942"
                            }
                        },
                        label = { Text(method) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = when (selectedMethod) {
                    "PayPal" -> "PayPal Email Address"
                    "Stripe Connect" -> "Stripe Connected Account ID"
                    else -> "Bank ACH / SWIFT Details (Routing & Account)"
                },
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = accountDetailsInput,
                onValueChange = { accountDetailsInput = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = {
                    Text(
                        when (selectedMethod) {
                            "PayPal" -> "e.g. creator@paypal.com"
                            "Stripe Connect" -> "e.g. acct_123456789"
                            else -> "e.g. Routing: 1234, Account: 5678"
                        }
                    )
                }
            )

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
                        accountDetails = accountDetailsInput,
                        onComplete = {
                            android.widget.Toast.makeText(context, "Cash-out of $$amount initiated via $selectedMethod ($accountDetailsInput)!", android.widget.Toast.LENGTH_LONG).show()
                            onDismiss()
                        },
                        onError = { err -> errorMessage = err }
                    )
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700), contentColor = Color.Black),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text("Confirm Payout Withdrawal ($50 min)", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
