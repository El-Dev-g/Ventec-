# Zooz Web (Next.js Companion)

Welcome to **Zooz Web**, the official web-based counterpart of the **Zooz Android Application**. Built with Next.js, React, Tailwind CSS, and TypeScript, this repository ensures users experience a seamless, feature-for-feature recreation of the mobile application on desktop, tablet, and mobile browsers.

---

## 📱 Replicating the Android Experience

To deliver a truly cohesive cross-platform product, **Zooz Web** directly mirrors the architectural screens, capabilities, and navigation flows of the Android application:

1. **Feature Alignment**:
   - **Local C2C Marketplace**: Allows users to discover, list, and coordinate peer trades in their neighborhoods on modern web browsers, matching the mobile `/marketplace` functionality.
   - **Commerce Integration**: Facilitates the direct checkout and brand redirect modalities managed by `/commerce_manager`.
   - **Unified Authentication**: Uses stateful web cookies/sessions matching the Android app's OAuth 2.0 and Zooz Login tokens under `/authentication`.
   - **Dynamic Payments**: Incorporates the web SDK variant of **ZoozPay** to process direct payments and secure micro-escrow holds from `/payments`.

2. **Design Language Synchronization**:
   - Web layout rules inherit the design principles of the Android Material 3 Design system.
   - Component spacing, typography pairings, color scheme palettes, and interactive transitions (ripples, hover transformations) resemble jetpack-compose patterns.
   - Fully fluid and adaptive canvas screens designed for both horizontal viewport displays (Desktops, Foldables) and vertical screens (Mobile web wrapper context).

---

## 🔗 How Web & Android Interconnect

The Web and Android clients do not run in isolation; they are bound together through shared architecture:

```
                  +====================+
                  |  Shared API Core   |
                  +=========+==========+
                            |
           +----------------+----------------+
           |                                 |
           v                                 v
   [ Official Android App ]           [ Zooz Web / Next.js ]
     - Kotlin / Jetpack Compose         - React / Next.js / Tailwind
     - Native UI Rendering              - Responsive Browser Rendering
     - Local Room DB                    - LocalStorage/Session State
```

- **Shared State & Database Sync**: Actions completed on the Web client instantly update the user's Android workspace and native SQLite databases via real-time WebSocket signals and notification channels (`/notification_service`).
- **Web-Views & Progressive Web App (PWA)**: Key dynamic pages in Zooz Web are optimized to function both as individual web browser modules and as embedded high-performance, edge-to-edge Web-views inside the native Android client wrapper (`/app`).

