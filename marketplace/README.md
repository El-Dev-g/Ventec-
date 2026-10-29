# Zooz Marketplace (Next.js)

Welcome to the **Zooz Marketplace**, a modern, type-safe local **C2C (Consumer-to-Consumer / Peer-to-Peer) Marketplace** built with Next.js, Tailwind CSS, and TypeScript. This repository supports physical and digital trade between active peers directly inside the Zooz neighborhood ecosystem.

## Peer-to-Peer (C2C) Model
- **Local Listings**: Discover trade and services from nearby verified peers.
- **Direct Chat & Meetup**: Users coordinate logistics and meetup points securely.
- **Micro-escrow Trades**: Peer funds are put on smart hold with ZoozPay until physical or digital goods are validated upon handoff.
- **Reputation Scoring**: Built-in seller feedback and verified transaction history badges.

## Tech Stack
- **Framework**: Next.js (App Router)
- **Styling**: Tailwind CSS
- **Language**: TypeScript
- **Icons**: Lucide React
- **Database (Optional Link)**: Prisma / PostgreSQL

## Getting Started

First, install the dependencies of this workspace:

```bash
cd marketplace
npm install
```

Then, run the development server:

```bash
npm run dev
```

Open [http://localhost:3000](http://localhost:3000) with your browser to see the result.

## Folder Structure
- `/app` - Next.js App Router root enclosing layout and page definitions for listing creation, details, and peer dashboard.
- `/components` - Shared UI elements (buttons, cards, inputs).
- `/lib` - Peer routing services, location distance matrices, and local storage clients.
- `/public` - Static image and vector assets.
