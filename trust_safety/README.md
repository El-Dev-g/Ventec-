# Zooz Trust & Safety

Welcome to **Zooz Trust & Safety**. This workspace houses guidelines, reporting forms, content moderation models, and community standards used to make the Zooz network a safe, respectful environment.

## Main Safeguards
1. **Content Incident Reports**: Unified APIs for user-facing report forms ("Inappropriate behavior", "Harassment", "Copyright Infringement", "Scam").
2. **AIGC Pre-Approval Service**: Runs lightweight semantic scanning on social feed articles, images, and description texts to auto-reject illegal material or spam messages before publication.
3. **Vendor Escrow Holds**: Automatic holds placed on payouts inside the **Payments** database for stores logging massive visual chargebacks or user complaints.
4. **Moderator Portal Mockups**: Queue managers allowing the Zooz safety operations staff to rapidly approve, deny, edit, or append warning overlays to flagged posts.

## Layout Overview
- `/schemas` - Abuse reporting payloads and standard appeal submission sheets.
- `/automoderator` - Basic regex filters, text classification inputs, and safe image heuristics.
- `/guides` - Standard Safe Community guidelines for creators and physical vendors.
