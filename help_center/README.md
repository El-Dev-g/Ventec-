# Zooz Help Center & Dispute Support

Welcome to the **Zooz Help & Support Center**. This is our specialized system handling customer service requests, merchant chargeback disputes, and user tutorials.

## Key Submodules
- **Ticket Escalation Pipeline**: Connects buyers with store operators on the **Marketplace** to resolve shipping delays, item defects, or missing downloads.
- **Interactive Knowledge Base**: Standard JSON files managing step-by-step documentation on setting up an Ad Pixel, getting Verified as an Artist, or listing products.
- **AI Support Assistant**: Integrations with LLMs to address redundant questions (e.g. "How do I download my receipt?") without human agent interaction.
- **Escrow Refund Arbiter**: Workflows for platform moderators to review conversation logs and manually resolve financial disputes.

## Folder Configuration
- `/faq` - Structured Markdown guides grouped by system scopes (Sellers, Advertisers, Artists, Developers).
- `/ticket-engine` - Interface schemas to initiate, close, or transfer tickets.
- `/chatbot` - Semantic matching arrays and pre-defined response hooks for user triage.
