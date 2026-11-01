# Zooz Ad Management

Welcome to the **Zooz Ad Management** (Ads Manager) system directory. This hub outlines the advertising protocols, target segmentation engines, custom tracking pixels, and analytics dashboards that power commercial reach on the Zooz social ecosystem.

## Core Features
1. **Ad Campaign Builder**: Configuration structures for Objective-Based Campaigns (Awareness, Traffic, Engagement, Sales, Lead Generation).
2. **Multi-Asset Creator Promotions**:
   - **Videos**: FYP reels and short-form video boosting with interactive overlays.
   - **Images**: High-resolution photo posts, flyers, and event banner promotions.
   - **Third-Party Platforms**: Direct channel traffic generation for YouTube, Instagram, Spotify, TikTok, Twitter/X, and custom websites.
   - **Affiliate Products**: Commission-based item promotion with retail pricing, affiliate partner tracking (Amazon, TikTok Shop, Shopify), and promo code discounts.
3. **Dynamic Budget & Views Projection Engine**:
   - Real-time CPM algorithms calculating projected views (`budgetCoins * 25` to `budgetCoins * 35`), CTRs (`~6.5%`), and daily pacing across 1, 3, 7, and 14-day flight schedules.
4. **Zooz Pixel (Tracking)**: A lightweight client-side script for tracking conversions, page views, and specific custom actions on external merchant sites (e.g. Next.js Marketplace).
5. **Audience Demographics Segmenter**: Algorithms to group users by local preference, interest indices, and social activity tags.
6. **Billing & Budget Orchestrator**: Interface guidelines for daily/lifetime budget caps, bidding algorithms, and micro-billing transaction pipelines with Coin-to-USD conversion ($0.05 / Coin).

## Feed Placement & Delivery Pipeline
1. **FYP & Reels Native Mixed-Post Delivery**:
   - Promoted campaigns are delivered seamlessly directly among organic posts in the main feed: slot 1 (after the initial opening post) and continuing every 3 organic posts.
   - Clean, unified native post styling with a subtle `SPONSORED` badge and embedded action CTA button ensures high immersion and organic feel without intrusive specialized ad blocks.
2. **Elimination of Separate Promotions Page & Header Promo Banners**:
   - The separate "Promotions" tab and "PROMO" banner badge have been eliminated from the home feed navigation header. All commercial and creator campaigns live organically mixed into the main feed with no separate page.
3. **No Public View Counts on Ads**:
   - To preserve an authentic viewer experience and clean content focus, impression and view count metrics are eliminated from public ad cards and displayed only in creator management analytics.
4. **Attribution & Interaction Tracking**:
   - Every campaign card impression is automatically logged via `recordCampaignImpression(id)`.
   - All interactive CTA taps (`Shop Deal`, `Visit Channel`, `Listen on Spotify`, `Learn More`) trigger `recordCampaignClick(id)` and launch deep-links.

## Folder Structure
- `/pixel` - Embedding code block scripts and server-side webhook endpoints.
- `/pixel-helper` - Developer chrome-extension simulation resources for pixel validation.
- `/campaigns` - Schema formats for storing and modifying reactive ad-sets (`schema.json`).

