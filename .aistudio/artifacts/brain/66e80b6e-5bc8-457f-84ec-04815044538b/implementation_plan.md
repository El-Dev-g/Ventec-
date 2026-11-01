# Creator Tools & Eligibility Hub

A comprehensive Creator Center for short-video creators featuring real-time eligibility tracking, detailed video performance analytics, diamond monetization cash-outs, audience demographic insights, and video promotion ad boost tools.

## User Review & Critical Decisions

> [!IMPORTANT]
> The following product decisions were confirmed during the interactive interview:

- **Eligibility Threshold**: 1,000 followers and 10,000 video views in the last 30 days.
- **Creator Tools Features**:
  1. **Video Performance & Watch-Time Analytics**: Views, completion rate, average watch duration, and top performing posts.
  2. **Diamond Gifts & Coin Monetization Cash-Out**: Live diamond balance, USD conversion calculator ($0.05 per diamond), and withdrawal history.
  3. **Audience Demographics & Follower Growth**: Follower growth timeline, top viewer locations, active creator hours, and gender breakdown.
  4. **Promoted Video & Ad Boost Management**: Boost campaigns for posts with target budget, campaign objective, and real-time impression tracking.
- **Access & Unlock Flow**: Automatic unlock with dynamic dual progress bars in the Profile settings menu ("Creator Center"). When locked, creators see live progress bars toward 1,000 followers and 10,000 views with an "Estimate Readiness" indicator; when unlocked, full access to all 4 Creator Tools is granted.

---

## 1. Overview & Core Concept

The **Creator Tools Hub** empowers content creators on the platform to monitor their account growth, track video metrics, monetize gifts, and boost post reach:
- **Locked State (Under Eligibility)**: Displays an interactive "Eligibility Dashboard" with live progress bars, criteria checklists, and tips on how to grow views and followers.
- **Unlocked State (Eligible Creator)**: Grants access to 4 rich analytical and monetization tabs:
  1. **Analytics Dashboard**: Daily view counts, watch-time charts, engagement rates, and video performance cards.
  2. **Monetization & Payouts**: Diamond earnings from viewer gifts, coin conversion rates, and cash-out request modal.
  3. **Audience Insights**: Follower growth curves, peak active hours heatmaps, and viewer demographic breakdown.
  4. **Ad Boost & Promotion**: Create ad campaigns to boost video reach using creator coins/credits with targeted audiences.

---

## 2. User Experience & Visual Design

### Key User Flows

1. **Accessing Creator Tools**:
   - User taps the Profile menu / Settings bar -> "Creator Center / Creator Tools".
2. **Progress & Eligibility View (If Locked)**:
   - Displays real-time progress bars:
     - **Followers**: e.g., `850 / 1,000 Followers` (85% completed).
     - **30-Day Views**: e.g., `8,400 / 10,000 Views` (84% completed).
   - Includes a "Simulate Unlock" toggle in development/demo mode to test unlocked tools.
3. **Creator Hub Tabs (If Unlocked)**:
   - **Tab 1: Overview & Video Analytics**: Interactive charts showing total views, engagement, watch duration, and top video rankings.
   - **Tab 2: Monetization & Diamond Cash-Out**: Displays accumulated Diamonds, USD value, Cash-Out button, and transaction history.
   - **Tab 3: Audience Demographics**: Visual breakdown of top viewer cities/countries, age groups, and peak activity times.
   - **Tab 4: Promoted Video & Ad Boost**: Campaign launcher allowing creators to spend Coins to boost specific posts for extra impressions.

### Visual Identity & Theme

- **Aesthetic Direction**: High-energy, dark-themed creator studio with neon cyan (`#25F4EE`), vibrant pink (`#FE2C55`), and luxury gold (`#FFD700`) accents over a sleek `#12121A` background.
- **Component Design**: Glassmorphism cards (`RoundedCornerShape(16.dp)`), animated progress bars, gradient metric badges, and interactive segment tabs.

---

## 3. Key Product Decisions & Trade-Offs

- **Decision 1: Real-time Eligibility Tracking vs Manual Approval**:
  - *Chosen Approach*: Automatic real-time eligibility evaluation based on live ViewModel data (`followerCount` and aggregated 30-day post view counts).
  - *Why*: Instant gratification and motivation for creators to reach milestones without administrative delays.
- **Decision 2: Integrated Ad Boost Campaign Simulator**:
  - *Chosen Approach*: Deducts Coin credits from the user's balance to launch active "Boost Campaigns" that artificially increase target video impressions and views over time.
  - *Why*: Provides a complete end-to-end promotional tool experience inside the app.

---

## 4. Technical Architecture & Data Strategy

```
┌─────────────────────────────────────────────────────────────────────────┐
│                          Profile / Settings                             │
│                           ("Creator Center")                            │
└────────────────────────────────────┬────────────────────────────────────┘
                                     │
                                     ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                       CreatorEligibilityEvaluator                        │
│         Checks: Follower Count >= 1,000 && 30-Day Views >= 10,000       │
└──────────────────┬──────────────────────────────────┬───────────────────┘
                   │                                  │
         [Locked / Ineligible]                        │ [Unlocked / Eligible]
                   │                                  │
                   ▼                                  ▼
┌──────────────────────────────────────┐  ┌───────────────────────────────┐
│      Eligibility Progress View       │  │       Creator Studio Hub      │
│  - Progress Bar: Followers (850/1000)│  ├───────────────────────────────┤
│  - Progress Bar: Views (8.4k/10k)    │  │ 1. Video & Watch-Time Metrics │
│  - Unlock Tips & Fast Track          │  │ 2. Diamond Cash-Out Portal    │
│  - Demo Toggle: "Simulate Unlock"    │  │ 3. Audience Demographics      │
└──────────────────────────────────────┘  │ 4. Ad Boost Campaign Manager  │
                                          └───────────────────────────────┘
```

### Key Data Entities (`BlogViewModel.kt` & `DataModels.kt`)

```kotlin
data class CreatorEligibilityState(
    val currentFollowers: Int,
    val requiredFollowers: Int = 1000,
    val currentViews30Days: Int,
    val requiredViews30Days: Int = 10000,
    val isEligible: Boolean = currentFollowers >= requiredFollowers && currentViews30Days >= requiredViews30Days
)

data class BoostCampaign(
    val id: String,
    val postId: Int,
    val postTitle: String,
    val budgetCoins: Int,
    val targetImpressions: Int,
    val currentImpressions: Int,
    val status: String // "ACTIVE", "COMPLETED"
)
```

---

## Implementation Steps

1. **State & Analytics Engine (`BlogViewModel.kt`)**:
   - Add `getCreatorEligibility()` StateFlow calculating follower count and 30-day views.
   - Add `creatorBoostCampaigns` StateFlow and `createBoostCampaign(postId, coinsBudget)` action.
   - Add `requestDiamondCashOut(diamondAmount)` action.

2. **Creator Studio Screens (`CreatorToolsScreen.kt`)**:
   - Create `CreatorToolsScreen` with dual mode: **Eligibility Progress Screen** (when locked) and **Creator Studio Hub** (when unlocked).
   - Implement Tab Bar with 4 sub-views: *Analytics*, *Monetization*, *Audience*, and *Promote/Boost*.
   - Add **Promoted Video Campaign Builder** modal.
   - Add **Diamond Payout Cash-Out Modal**.

3. **Navigation & Profile Entry Point (`MainActivity.kt` & `Screens.kt`)**:
   - Register route `"creator_tools"` in `MainActivity.kt`.
   - Add "Creator Center" menu tile with a status badge in `ProfileScreen` and `MenuContent`.
