# Zooz Analytics & Reporting Portal

Welcome to the **Zooz Analytics Portal**. This service outlines the schema structures, event tracking mechanisms, and metrics algorithms responsible for processing real-time system and business metrics for both creators and advertisers.

## Functional Pipelines
- **Event Tracker (Scribe)**: Real-time user action pipeline capturing clickthrough rates on the **Marketplace**, view durations, swipe gestures, and ad conversions.
- **Creator Dashboard Data**: Computes active engagement indices, demographic tables (age groups, regions), and subscriber retention graphs.
- **Conversion API (CAPI)**: Allows business servers to transmit offline event logs back to Zooz for attributing advertisement impact securely.
- **Weekly Summary Generator**: Scheduled analytical scripts that aggregate individual user data and email merchants visual reports on their store's financial health.

## Folder Layout
- `/event-schemas` - Strongly typed JSON Schema definitions for core analytics occurrences.
- `/data-pipelines` - MapReduce or Spark scripts modeling hourly aggregations.
- `/dashboard-cards` - Standard JSON definitions representing chart configurations for visual layouts.
