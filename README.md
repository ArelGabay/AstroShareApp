# AstroShareApp - Android Kotlin Project

AstroShareApp is a mobile Android application developed in **Kotlin**, following the **MVVM architecture**, and designed to provide users with a space-themed social platform for sharing astronomical experiences, trip logs, and celestial events. 

This app was built using native Android components and leverages Firebase and local storage for robust functionality and performance.

---

## 🌌 Core Features

- **User Authentication**:
  - Sign up and login with **Firebase Authentication**
  - Supports email/password login

- **User Profile**:
  - View and update user display name
  - Display user-generated posts and profile image
  - Logout functionality

- **Trip Sharing (Posts)**:
  - Create trip posts with title, description, location, and image
  - View trips from all users
  - Edit or delete your own trips
  - Filter to view only "My Trips"
  - Posts displayed in full-height cards with labeled fields

- **Explore Tab**:
  - Uses location permission to fetch nearby celestial data
  - Toggle between data sources (events, stars, moon)
  - Integrates **AstronomyAPI** to provide detailed, real-time data:
    - Moon phase and illumination
    - Planetary positions
    - Star charts based on the user's coordinates and date
    - Celestial events (eclipses, conjunctions)
  - The AstronomyAPI integration was a major aspect of the app, with customized OkHttp requests, secure token usage, and dynamic chart rendering

- **Commenting & Likes (Optional)**:
  - Users can comment on and like posts (if implemented)

---

## 📦 Tech Stack

- **Language**: Kotlin
- **Architecture**: MVVM (Model-View-ViewModel)
- **UI**: XML-based layouts with ConstraintLayout & RecyclerView
- **Database**: Firebase Firestore
- **Authentication**: Firebase Auth
- **Storage**: Firebase Storage for images
- **Navigation**: Jetpack Navigation Component
- **Image Loading**: Glide
- **External API**: AstronomyAPI (REST-based)

---

## 📁 Structure Highlights

- `TripsFragment`, `UserFragment`, `ExploreFragment`
- `TripAdapter` for RecyclerView
- `ViewModel` and `Repository` for data handling
- `FirebaseService` for modular backend logic
- `AstronomyApiService` with token-secured HTTP integration

---

## 🚀 Setup Instructions

1. Clone the repo:  
   `git clone https://github.com/Eilonasraf/AstroShareApp`

2. Open in **Android Studio**

3. Configure Firebase:
   - Connect to Firebase in Android Studio (Tools > Firebase)
   - Enable Firebase Auth and Firestore
   - Replace `google-services.json` with your own Firebase config

4. Configure AstronomyAPI:
   - Sign up and retrieve API credentials from [astronomyapi.com](https://www.astronomyapi.com)
   - Set your token securely in headers via `OkHttp` implementation

5. Build & Run on emulator or device

---

## ✅ Future Improvements
- Dark mode UI
- Real-time chat (e.g., with Firestore + Firebase Functions)
- Notifications for comments or likes
- Offline support using Room DB

---

## 📜 License
This project was developed as part of a university assignment and is intended for educational use.

---

Enjoy exploring the stars with AstroShareApp ☄️
