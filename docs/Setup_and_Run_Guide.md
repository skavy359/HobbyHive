# Setup and Run Guide for HobbyHive

## 1. Project Components
The HobbyHive project consists of a static web landing page and a native Android application. This guide covers how to run both, with a focus on running the Android app via Android Studio.

## 2. Running the Frontend (Web Landing Page)
Since the frontend consists of static HTML files, no complex server setup is required.
1. Navigate to the `frontend/` directory.
2. Open `index.html` or `main.html` directly in any modern web browser.
3. Alternatively, use a local server like VS Code Live Server extension or Python's `http.server` for a better experience:
   ```bash
   cd frontend
   python -m http.server 8000
   ```
   Then visit `http://localhost:8000` in your browser.

## 3. Running the Android Application in Android Studio

### Prerequisites
- Install **Android Studio** (latest versions like Iguana or Jellyfish recommended).
- Ensure you have the **Android SDK** installed (The project's Target SDK is 36, Min SDK is 24).

### Backend and External Connections
**Currently, HobbyHive is a local-first application.** 
- **Database**: It uses a local SQLite database via the Android Room library.
- **Backend/API**: There is **no external backend connection required** to run the current state of the application. All features (Goals, Sessions, Achievements, etc.) are processed and stored locally on the device.
- **Future Integration**: When you decide to implement real-time community features or cross-device syncing in the future, you will need to set up a backend (like Firebase, Supabase, or a custom REST API) and integrate a networking library like Retrofit or Ktor into the app.

### Setup Instructions
1. Open Android Studio.
2. Select **File > Open** and navigate to your project directory. 
   *(Note: Do not open the root `HobbyHive` folder; open the `hobbyhive` folder which contains the `app` directory and `build.gradle.kts` files).*
3. Wait for Android Studio to sync the Gradle project. This may take a few minutes as it downloads dependencies like Jetpack Compose, Room, and Coil.
4. If prompted, update the Android Gradle Plugin to match your installed Android Studio version.

### Running on the Android Simulator
1. Open the **Device Manager** in Android Studio (usually on the right sidebar or under `Tools > Device Manager`).
2. Click the **+** or **Create Device** button.
3. Select a phone model (e.g., Pixel 7) and click **Next**.
4. Choose a system image (API Level 34 or higher is recommended) and click **Download** if it's not already downloaded. Once downloaded, select it and click **Next**.
5. Click **Finish** to create the Virtual Device.
6. Click the **Play** button next to your new device in the Device Manager to launch the emulator.
7. Once the emulator is running and unlocked, click the **Run 'app'** button (green play icon) in the top toolbar of Android Studio.
8. The app will build, install, and launch on the simulator automatically.
