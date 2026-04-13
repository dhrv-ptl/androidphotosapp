# IronTrack Gym App

React + Vite gym training tracker with Supabase auth/data and an Express dev server.

## Requirements

- Node.js 20+
- npm
- Supabase project credentials

## Setup

1. Copy `.env.example` to `.env`.
2. Fill in `VITE_SUPABASE_URL` and `VITE_SUPABASE_ANON_KEY`.
3. Install dependencies with `npm install`.

## Scripts

- `npm run dev` starts the Express server with Vite middleware on port `3000`.
- `npm run build` builds the frontend with Vite.
- `npm run lint` runs `tsc --noEmit`.
- `npm run start` runs the TypeScript server entry with `tsx`.

## Notes

- The project root is `/Users/dhruvpatel/Documents/New project/GymApp-main`.
- Database schema setup lives in `supabase_migration.sql`.
