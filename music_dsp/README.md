# Zooz Music DSP (Digital Service Provider) Hub

Welcome to the **Zooz Music DSP** integration center. Inspired by the **Facebook Music (Meta Rights Manager)** distribution system, this architecture manages ingestion, music rights clearance, and content ID matching for continuous audio assets within the Zooz multimedia platform.

## Overview
Independent and major distributors (e.g., DistroKid, TuneCore, CD Baby, Believe) use these APIs and protocol schemas to deliver audio tracks to the Zooz Audio Catalog, making music globally available for creators inside the mobile app and web clients for use in:
- Short videos / Reels
- Image status stories
- Background profile audios

## Integration Modules

### 1. Catalog Ingestion (DDEX ERN)
Standardized XML/JSON schema ingestion to process incoming albums, releases, tracks, and associated cover arts.
```json
{
  "dsp_track_id": "z_dsp_9843213082",
  "isrc": "US-S1Z-26-48293",
  "title": "Neon Sunset",
  "artist": "Midnight Wave",
  "duration_seconds": 214,
  "rights_holder": "Wave Records LLC"
}
```

### 2. Audio Fingerprinting & Content ID Match (Rights Manager)
Automated scanning system to detect copyright matches in user-generated content.
- **Match Strategy**: Mutes, displays credit to the original artist/rights holder, or inserts a "Buy Product Link" dynamically referencing the track on the **Zooz Marketplace**.

### 3. Share-to-Stories SDK Integration
Provides native hooks for third-party music streaming applications (e.g. Spotify, Apple Music) to easily export a musical item card Directly into Zooz stories with interactive audio attachments and custom gradients.
