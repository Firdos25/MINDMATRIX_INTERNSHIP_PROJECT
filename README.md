# Raitha-Varta Full Stack Agriculture Project

Raitha-Varta is now upgraded to a complete **Android + Backend + Database** project for final year demo/viva.

## What is implemented

- Full login/registration/forgot password flow.
- Secure JWT authentication and backend session tracking.
- User profile management with profile update and image upload.
- Leaf disease prediction API and Android upload flow.
- Prediction result screen with confidence, causes, prevention, and treatment.
- User prediction history and notifications pages.
- Settings page with logout + session/history API integration.
- Admin-ready API endpoint (`/api/admin/users`).
- Existing farmer tips/success stories modules retained.

## Project Structure

```text
rv/
├── app/                       # Android Studio project (Java + XML)
└── backend/                   # Node.js backend (Express + SQLite)
    ├── db/
    │   ├── schema.sql         # Full database schema
    │   ├── seed.sql           # Sample seed inserts
    │   └── queries.sql        # CRUD SQL for all required tables
    ├── uploads/               # Uploaded images
    ├── .env.example
    ├── package.json
    └── server.js
```

## Database tables included

- `users`
- `login_history`
- `sessions`
- `leaf_disease_reports`
- `uploaded_images`
- `disease_predictions`
- `notifications`

## Backend API Overview

### Auth
- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/forgot-password`
- `POST /api/auth/logout`

### Profile
- `GET /api/profile`
- `PUT /api/profile`
- `POST /api/profile/image`

### Disease + history
- `POST /api/disease/predict`
- `GET /api/history`

### Notifications
- `GET /api/notifications`
- `PUT /api/notifications/:id/read`

### Session tracking
- `GET /api/sessions`
- `GET /api/login-history`

### Admin
- `GET /api/admin/users`

## Android screens added

- `LoginActivity`
- `RegisterActivity`
- `ForgotPasswordActivity`
- `DashboardActivity`
- `DiseaseDetectionActivity`
- `ResultActivity`
- `UserHistoryActivity`
- `ProfileActivity`
- `NotificationsActivity`
- `SettingsActivity`

`SplashActivity` now routes to login/dashboard based on saved session.

## Setup guide

### 1) Backend

1. Install Node.js 18+.
2. Open terminal in `backend/`.
3. Copy env:
   - Windows: `copy .env.example .env`
4. Install dependencies: `npm install`
5. Run server: `npm run dev`
6. Backend runs at `http://localhost:5000`

> Android emulator uses `http://10.0.2.2:5000` (already configured).

### 2) Android

1. Open `rv` in Android Studio.
2. Let Gradle sync complete.
3. Ensure JDK 17 and Android SDK 34 are installed.
4. Run app on emulator/device (min SDK 24).
5. Register a user and start disease prediction flow.

## Leaf disease module notes

The backend currently includes a structured disease knowledge base for:
- Healthy
- Bacterial Blight
- Leaf Spot
- Powdery Mildew

You can extend `DISEASE_KB` in `backend/server.js` with more crop-specific diseases and richer recommendations.

## Production/demo hardening checklist

- Replace fallback JWT secret with strong value in `.env`.
- Add HTTPS reverse proxy (Nginx/Caddy) for deployment.
- Integrate real ML model service in `/api/disease/predict`.
- Add unit/integration tests for backend APIs.
- Add FCM push notifications for real-time alerts.

