# Zooz Notification Service

Welcome to the **Zooz Notification Microservice**. This directory controls the orchestration, aggregation, and routing of transactional and marketing notifications across the entire ecosystem.

## Protocols
- **FCM (Firebase Cloud Messaging)**: Secure payloads providing immediate real-time notifications for Android devices.
- **APNs (Apple Push Notification service)**: Encrypted structures for instant client notifications on iOS hardware.
- **Transactional Mail Carrier**: Outlines HTML e-commerce receipts (orders from the **Marketplace**), credential resets, and dispute notices.
- **In-App Message Core**: Coordinates system feeds, comment threads, and ad notification counters dynamically.

## Directory Structure
- `/payloads` - Structured payloads for transactional notifications.
- `/templates` - Liquid or Handlebar layout cards for visually polished visual mail templates.
- `/dispatchers` - Rate-limiting systems safeguarding users from repetitive notification spam.
