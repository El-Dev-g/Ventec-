# Zooz Payments Gateway (ZoozPay)

Welcome to **ZoozPay**, the custom payment provider and escrow system designed specifically for the Zooz social ecosystem and Marketplace. 

## Features
- **Escrow-as-a-Service**: Protects both buyers and sellers by holding funds dynamically until digital downloads are verified or physical goods are marked as shipped.
- **Micro-transaction Optimizer**: Keeps network and processing fees low for small creator-tipping sums ($0.10 - $2.00).
- **Custom Wallet**: Supports Zooz credits (in-app virtual currency) linked with secure, external debit/credit cards or local digital banking services.
- **Split-Billing Webhooks**: Allows multi-vendor carts to easily split a single customer invoice into appropriate payouts to different merchants while routing platform fees directly to the Zooz treasury.

## Directory Layout
- `/sdk-android` - Kotlin fragments/view-models for prompt checkout bottom sheets inside the native Android client.
- `/sdk-next` - React components and Stripe/Adyen/PayPal hooks for the frontend Next.js web portal.
- `/escrow-engine` - Core transaction logic, ledger, and reconciliation tools.
- `/webhooks` - Secure server signature validators for processing notifications of payments completed, refunded, or disputed.
