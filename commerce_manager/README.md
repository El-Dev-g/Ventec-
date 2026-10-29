# Zooz Commerce Manager

Welcome to the **Zooz Commerce Manager** system. This repository holds the schemas, APIs, and catalog structures used by merchants and sellers in the Zooz ecosystem to manage retail operations, sync catalogs, and customize checkout experiences.

## Core Pillars

### 1. Catalog Syncing & APIs
Connect external e-commerce engines to the Zooz network to ingest, synchronize, and update catalogs in real-time:
- **Shopify Integration**: Sync products, inventory variants, collections, and price books using Shopify Admin Webhooks and GraphQL Admin APIs.
- **BigCommerce Integration**: Pull catalog trees, manage SKU level mapping, and process webhook updates.
- **WooCommerce Integration**: Sync with self-hosted shops using REST APIs with automated rate-limiting backoffs.

### 2. Checkout Modalities
Control how customers purchase items found across the platform:
- **Direct-Checkout**: Process payments entirely within the Zooz application environment leveraging **ZoozPay** to ensure a single-tap transaction experience.
- **Redirect Shopping**: Route high-intent users directly to the external brand's website or checkout funnel to complete their shopping journey.

## Folder Configuration
- `/schemas` - JSON definitions for standardized product, pricing, and tax structures.
- `/catalog-sync-apis` - Connectors and API definitions for Shopify, WooCommerce, and BigCommerce.
- `/checkout` - Direct checkout schemas, webhook callbacks, and redirect routing managers.
- `/order-service` - Order lifecycle schemas monitoring transaction states across shopping channels.