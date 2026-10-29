# Implementation Plan - TikTok-Style Social Media Transformation

Transform the Zooz social app into a TikTok-style short-form video platform featuring a full-screen vertical video feed, caption overlays, creator tools with music integration, and a dual-column discovery grid, while removing text-only blog posts.

## User Review Required
> [!IMPORTANT]
> Please review the proposed changes below. Once you click Proceed, the implementation will commence.

- **Main Feed**: Full-screen vertical video feed with swipe navigation and caption overlay.
- **Discovery**: Dual-column grid view for exploring short videos.
- **Content Model**: Video-centric posts with optional Spotify background track, removing text-only posts.
- **Scope**: Client app focus (no server updates needed per user preference).

---

## Proposed Changes

### 1. Feed & Navigation Architecture
- **VerticalVideoFeedScreen**: Full-screen vertical pager / lazy list displaying video posts with overlay UI (creator profile, caption, likes, comments, share, audio ticker).
- **DiscoveryGridScreen**: Dual-column grid for exploring trending videos and categories.
- **Navigation Update**: Streamline bottom navigation bar to match TikTok style (Home Feed, Discover, Create, Inbox/Activity, Profile).

### 2. Content Creation & Media
- **CreateVideoScreen**: Focused short video capture or video picker with Spotify track attachment.
- **Post Model Update**: Ensure all posts require media (video/image) and/or music track, omitting text-only posts.

### 3. Profile & Menu Fixes
- Fix profile menu routing so clicking menu options (like settings, view profile) correctly toggles and navigates without state desync on subsequent opens.

---

## Verification Plan

### Automated Tests
- Run `gradle :app:testDebugUnitTest` to verify repository and viewmodel logic.
- Run `compile_applet` to ensure successful compilation of all Jetpack Compose screens and dependencies.
