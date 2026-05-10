# HobbyHive Project Documentation

## 1. Project Overview
HobbyHive is a comprehensive tracking application designed to help users structure, organize, and track their personal hobbies, passions, and study events. The project is split into two primary components:
- **Frontend / Landing Page**: A web-based promotional landing page demonstrating the features and visual identity of the app.
- **Android Application**: The core mobile application built with native Android technologies (Kotlin, Jetpack Compose) for users to track their activities.

## 2. Directory Structure
- `frontend/`: Contains static web files (`index.html`, `main.html`). These files provide a rich, interactive landing page showcasing features like "Smart Analytics", "Streak Engine", "Goal Forge", and "Community Hive".
- `hobbyhive/`: The Android project directory. Contains the full source code for the native Android application.

## 3. Architecture & Tech Stack
### Web Landing Page
- **Tech Stack**: Vanilla HTML, CSS, JavaScript.
- **Styling**: Custom CSS with complex animations, CSS variables for theming, and GSAP-like manual animations.

### Android Application
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Architecture**: MVVM (Model-View-ViewModel)
- **Database**: Room Database (Local SQLite) for offline-first data storage.
- **Image Loading**: Coil
- **Background Tasks**: WorkManager & AlarmManager
- **Local Storage**: DataStore for user preferences

## 4. Key Features & Data Models
Based on the underlying database schema and UI components, the app supports:
- **Hobbies & Categories**: Users can create and categorize various hobbies.
- **Session Tracking**: Users log time spent on hobbies, creating a data trail for analytics.
- **Goals**: Setting milestones and tracking progress over time.
- **Streak Engine**: Gamification through tracking consecutive days of activity.
- **Achievements**: Unlockable trophies/badges based on user activity.
- **Reminders**: Configurable notifications to remind users to engage with their hobbies.
- **Community**: Mock or local representations of community sharing.
- **Study Events**: Specialized tracking for educational or focused learning sessions.

## 5. Future Enhancements
As per your plans, the future trajectory of the app will involve:
- **UI Overhaul**: Revamping the user interface for better accessibility, dynamic aesthetics, and user experience.
- **Backend Integration**: Transitioning from a purely local Room database to a synchronized cloud backend (e.g., Firebase, Node.js/Express) to enable true cross-device syncing and real community interaction features.
