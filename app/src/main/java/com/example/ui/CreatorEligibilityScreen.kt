package com.example.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatorEligibilityScreen(
    viewModel: BlogViewModel,
    onNavigateBack: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val currentUserId = currentUser?.id ?: -1
    
    val followerFlow = remember(currentUserId) { viewModel.getFollowerCount(currentUserId) }
    val followerCount by followerFlow.collectAsStateWithLifecycle()

    val allPosts by viewModel.allPosts.collectAsStateWithLifecycle()
    val userPosts = allPosts.filter { it.authorId == currentUserId && !it.isDraft }
    val postCount = userPosts.size

    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Creator Rewards", "Verified Badge", "LIVE & Gifts", "Brand Deals")

    // Application state
    var showApplySheet by remember { mutableStateOf(false) }
    var applicationProgramTitle by remember { mutableStateOf("") }
    var applicationFullName by remember { mutableStateOf(currentUser?.fullName ?: "") }
    var applicationTaxCountry by remember { mutableStateOf("United States") }
    var hasAgreedToTerms by remember { mutableStateOf(false) }
    var appliedPrograms by remember { mutableStateOf(setOf<String>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Creator Tools & Eligibility",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Monetization, Verification & Badges",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
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
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Creator Overview Banner
            item {
                CreatorHeaderBanner(
                    user = currentUser,
                    followerCount = followerCount,
                    postCount = postCount
                )
            }

            // Tab Navigation
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    contentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    maxLines = 1
                                )
                            }
                        )
                    }
                }
            }

            // Selected Program Content
            when (selectedTab) {
                0 -> {
                    // CREATOR REWARDS PROGRAM
                    item {
                        ProgramEligibilityCard(
                            title = "Creator Rewards Program",
                            subtitle = "Earn monthly payouts from original short-form video views.",
                            icon = Icons.Default.MonetizationOn,
                            badgeColor = Color(0xFFFE2C55),
                            isApplied = appliedPrograms.contains("Creator Rewards"),
                            isFullyEligible = followerCount >= 10000 && postCount >= 3,
                            onApplyClick = {
                                applicationProgramTitle = "Creator Rewards"
                                showApplySheet = true
                            },
                            requirements = listOf(
                                EligibilityCriterion(
                                    label = "Have at least 10,000 followers",
                                    currentValue = followerCount,
                                    targetValue = 10000,
                                    unit = "followers"
                                ),
                                EligibilityCriterion(
                                    label = "Published at least 3 public videos",
                                    currentValue = postCount,
                                    targetValue = 3,
                                    unit = "videos"
                                ),
                                EligibilityCriterion(
                                    label = "Account in Good Standing",
                                    currentValue = 1,
                                    targetValue = 1,
                                    unit = "No community strikes",
                                    isManualCheck = true
                                ),
                                EligibilityCriterion(
                                    label = "At least 18 years of age",
                                    currentValue = 1,
                                    targetValue = 1,
                                    unit = "Age confirmed",
                                    isManualCheck = true
                                )
                            )
                        )
                    }
                }
                1 -> {
                    // VERIFIED BADGE
                    item {
                        ProgramEligibilityCard(
                            title = "Verified Blue Badge",
                            subtitle = "Get the prestigious checkmark establishing authenticity.",
                            icon = Icons.Default.Verified,
                            badgeColor = Color(0xFF1DA1F2),
                            isApplied = appliedPrograms.contains("Verified Badge"),
                            isFullyEligible = (followerCount >= 10000) || (!currentUser?.bio.isNullOrBlank() && currentUser?.profilePicUri != null),
                            onApplyClick = {
                                applicationProgramTitle = "Verified Badge"
                                showApplySheet = true
                            },
                            requirements = listOf(
                                EligibilityCriterion(
                                    label = "10,000+ Followers OR Notable Public Figure",
                                    currentValue = followerCount,
                                    targetValue = 10000,
                                    unit = "followers"
                                ),
                                EligibilityCriterion(
                                    label = "Complete Profile (Photo, Bio, Real Handle)",
                                    currentValue = if (!currentUser?.bio.isNullOrBlank() && currentUser?.profilePicUri != null) 1 else 0,
                                    targetValue = 1,
                                    unit = "Profile complete",
                                    isManualCheck = true
                                ),
                                EligibilityCriterion(
                                    label = "Official ID / Authenticity Verification",
                                    currentValue = 1,
                                    targetValue = 1,
                                    unit = "Verified Account Holder",
                                    isManualCheck = true
                                )
                            )
                        )
                    }
                }
                2 -> {
                    // LIVE & VIRTUAL GIFTS
                    item {
                        ProgramEligibilityCard(
                            title = "LIVE Streaming & Virtual Gifts",
                            subtitle = "Collect animated diamonds and gift tips directly from viewers in real time.",
                            icon = Icons.Default.CardGiftcard,
                            badgeColor = Color(0xFFFF9800),
                            isApplied = appliedPrograms.contains("LIVE & Gifts"),
                            isFullyEligible = followerCount >= 1000 && postCount >= 1,
                            onApplyClick = {
                                applicationProgramTitle = "LIVE & Gifts"
                                showApplySheet = true
                            },
                            requirements = listOf(
                                EligibilityCriterion(
                                    label = "Have at least 1,000 followers",
                                    currentValue = followerCount,
                                    targetValue = 1000,
                                    unit = "followers"
                                ),
                                EligibilityCriterion(
                                    label = "Shared at least 1 video post",
                                    currentValue = postCount,
                                    targetValue = 1,
                                    unit = "video post"
                                ),
                                EligibilityCriterion(
                                    label = "Community Guidelines Compliance",
                                    currentValue = 1,
                                    targetValue = 1,
                                    unit = "Eligible for LIVE gifting",
                                    isManualCheck = true
                                )
                            )
                        )
                    }
                }
                3 -> {
                    // BRAND DEALS & CREATOR MARKETPLACE
                    item {
                        ProgramEligibilityCard(
                            title = "Creator Marketplace",
                            subtitle = "Connect directly with brands for sponsored sponsorships and product placements.",
                            icon = Icons.Default.Handshake,
                            badgeColor = Color(0xFF00E676),
                            isApplied = appliedPrograms.contains("Brand Deals"),
                            isFullyEligible = followerCount >= 5000 && postCount >= 5,
                            onApplyClick = {
                                applicationProgramTitle = "Brand Deals"
                                showApplySheet = true
                            },
                            requirements = listOf(
                                EligibilityCriterion(
                                    label = "Have at least 5,000 followers",
                                    currentValue = followerCount,
                                    targetValue = 5000,
                                    unit = "followers"
                                ),
                                EligibilityCriterion(
                                    label = "Published at least 5 video posts",
                                    currentValue = postCount,
                                    targetValue = 5,
                                    unit = "videos"
                                ),
                                EligibilityCriterion(
                                    label = "Active Engagement Rate above 3%",
                                    currentValue = 1,
                                    targetValue = 1,
                                    unit = "Eligible for brand campaigns",
                                    isManualCheck = true
                                )
                            )
                        )
                    }
                }
            }

            // Trust & Safety Policy Footer
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Safety",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Fair Monetization Guarantee",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelLarge
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Zooz reviews eligibility milestones dynamically. Once you meet the criteria, submit your application for review.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    // Application Modal Sheet
    if (showApplySheet) {
        ModalBottomSheet(
            onDismissRequest = { showApplySheet = false },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Stars,
                        contentDescription = null,
                        tint = Color(0xFFFE2C55),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Apply: $applicationProgramTitle",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Verify your creator details to register for official payouts and creator program privileges.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = applicationFullName,
                    onValueChange = { applicationFullName = it },
                    label = { Text("Legal Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = applicationTaxCountry,
                    onValueChange = { applicationTaxCountry = it },
                    label = { Text("Country of Residence") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    onClick = { hasAgreedToTerms = !hasAgreedToTerms },
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.material3.Checkbox(
                            checked = hasAgreedToTerms,
                            onCheckedChange = { hasAgreedToTerms = it }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "I certify that I am at least 18 years old and agree to the Zooz Creator Monetization Terms & Agreement.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        appliedPrograms = appliedPrograms + applicationProgramTitle
                        showApplySheet = false
                        Toast.makeText(
                            context,
                            "Application submitted for $applicationProgramTitle! Status: Under Review",
                            Toast.LENGTH_LONG
                        ).show()
                    },
                    enabled = applicationFullName.isNotBlank() && hasAgreedToTerms,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFE2C55))
                ) {
                    Text("Submit Application", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun CreatorHeaderBanner(
    user: com.example.data.User?,
    followerCount: Int,
    postCount: Int
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (user?.profilePicUri != null) {
                    AsyncImage(
                        model = user.profilePicUri,
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user?.username?.take(2)?.uppercase() ?: "CR",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = user?.username ?: "Creator",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (followerCount >= 10000) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Verified",
                                tint = Color(0xFF1DA1F2),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Text(
                        text = when {
                            followerCount >= 10000 -> "Tier 3: Elite Partner Creator"
                            followerCount >= 1000 -> "Tier 2: Promoted Rising Creator"
                            else -> "Tier 1: Emerging Creator"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatColumn(label = "Followers", value = String.format("%,d", followerCount))
                StatColumn(label = "Video Posts", value = postCount.toString())
                StatColumn(label = "Standing", value = "100% Good")
            }
        }
    }
}

@Composable
private fun StatColumn(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

data class EligibilityCriterion(
    val label: String,
    val currentValue: Int,
    val targetValue: Int,
    val unit: String,
    val isManualCheck: Boolean = false
)

@Composable
private fun ProgramEligibilityCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    badgeColor: Color,
    isApplied: Boolean,
    isFullyEligible: Boolean,
    onApplyClick: () -> Unit,
    requirements: List<EligibilityCriterion>
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = badgeColor.copy(alpha = 0.12f),
                    shape = CircleShape,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = icon, contentDescription = null, tint = badgeColor, modifier = Modifier.size(26.dp))
                    }
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Status Banner
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = when {
                    isApplied -> Color(0xFF2196F3).copy(alpha = 0.12f)
                    isFullyEligible -> Color(0xFF4CAF50).copy(alpha = 0.12f)
                    else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = when {
                                isApplied -> Icons.Default.TrendingUp
                                isFullyEligible -> Icons.Default.CheckCircle
                                else -> Icons.Default.Lock
                            },
                            contentDescription = null,
                            tint = when {
                                isApplied -> Color(0xFF1976D2)
                                isFullyEligible -> Color(0xFF388E3C)
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when {
                                isApplied -> "Application Submitted • Under Review"
                                isFullyEligible -> "You are Eligible to Join!"
                                else -> "Criteria In Progress"
                            },
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelMedium,
                            color = when {
                                isApplied -> Color(0xFF1976D2)
                                isFullyEligible -> Color(0xFF388E3C)
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Eligibility Requirements",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Criteria Items
            requirements.forEach { criterion ->
                CriterionRow(criterion)
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Button
            if (isApplied) {
                OutlinedButton(
                    onClick = { /* Already submitted */ },
                    enabled = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Under Review", fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = onApplyClick,
                    enabled = isFullyEligible,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFullyEligible) Color(0xFFFE2C55) else Color.Gray
                    )
                ) {
                    Text(
                        text = if (isFullyEligible) "Apply Now" else "Complete Requirements to Apply",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun CriterionRow(criterion: EligibilityCriterion) {
    val isMet = criterion.currentValue >= criterion.targetValue
    val progress = (criterion.currentValue.toFloat() / criterion.targetValue.toFloat()).coerceIn(0f, 1f)

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    color = if (isMet) Color(0xFF4CAF50).copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                    shape = CircleShape,
                    modifier = Modifier.size(24.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isMet) Icons.Default.Check else Icons.Default.Lock,
                            contentDescription = null,
                            tint = if (isMet) Color(0xFF388E3C) else Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = criterion.label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isMet) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isMet) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (!criterion.isManualCheck) {
                Text(
                    text = "${criterion.currentValue}/${criterion.targetValue}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isMet) Color(0xFF388E3C) else MaterialTheme.colorScheme.primary
                )
            } else {
                Text(
                    text = if (isMet) "Passed" else "Pending",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isMet) Color(0xFF388E3C) else Color.Gray
                )
            }
        }

        if (!criterion.isManualCheck) {
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (isMet) Color(0xFF4CAF50) else Color(0xFFFE2C55),
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}
