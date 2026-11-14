STREET RUNNER 3D — ANDROID BUILD PACKAGE (PHASE 1)

WHAT THIS PACKAGE CONTAINS
- web/: recovered Street Runner 3D HTML game, PWA manifest, service worker, icons and privacy-policy draft.
- android/: Android Studio project wrapping the game in a fullscreen Android WebView.
- assets/: supplied original SR 3D logo artwork.
- Application ID: com.streetrunner3d.game
- Version: 1.0.0 (versionCode 1)

VERIFICATION PERFORMED
- ZIP archive integrity checked after packaging.
- Extracted inline JavaScript syntax checked with Node.js.
- Android project configuration and source files prepared.
- This is NOT a compiled or signed APK/AAB. Android SDK/Android Studio build and real-device testing are still required.

IMPORTANT LIMITATIONS
- The recovered HTML currently loads Three.js r128 from https://cdnjs.cloudflare.com/ajax/libs/three.js/r128/three.min.js. The first game-engine load therefore requires internet access. Three.js is not bundled locally in this package.
- Ads are not integrated or active.
- In-app purchases are not integrated or active.
- The privacy-policy page is a draft. Replace placeholder contact details and update it to match actual data collection and SDK behavior before publishing.
- No PhonePe submission, review or approval has happened.
- Keep your release keystore and passwords private and backed up.

BUILD ON A COMPUTER (NO CODING REQUIRED)
1. Install Android Studio from https://developer.android.com/studio
2. Extract this ZIP.
3. In Android Studio choose Open and select the `android` folder inside this project.
4. Allow Android Studio to install the required Android SDK and Gradle components.
5. Wait for Gradle Sync to finish.
6. Connect your Android phone with USB debugging enabled, then press Run to test.
7. For a test APK: Build > Build Bundle(s) / APK(s) > Build APK(s).
8. For store submission: Build > Generate Signed Bundle / APK. Select the package format accepted by the store at submission time.
9. Create your own release keystore and store it safely. Never share the keystore or passwords.
10. Install and test the release build on a real device before submitting.

NETLIFY WEB DEPLOYMENT
1. Open Netlify and choose Add new site > Deploy manually.
2. Extract this ZIP.
3. Drag the CONTENTS of the `web` folder into Netlify's deploy area (not the outer project folder).
4. Test the resulting site on desktop and mobile.

PHONEPE PRE-SUBMISSION CHECKLIST
- Confirm current developer registration, package format, target SDK, signing, privacy and content requirements in PhonePe's official developer console.
- Replace the privacy-policy draft with a complete accurate policy and a real support contact.
- Add store screenshots, app description, category, age/content rating and support details.
- Test install, launch, gameplay, pause/resume, back behavior, network failure, screen sizes and crash handling.
- Ads and purchases must be separately integrated and tested before describing them as available.

MONETIZATION STATUS
Not implemented in this Phase 1 package. The next phase requires the chosen ad network account/app IDs, consent/privacy flow, and the store/payment provider's current purchase integration requirements.

OPTIONAL CLOUD TEST-APK BUILD (IF YOU DO NOT HAVE ANDROID STUDIO)
This ZIP includes a GitHub Actions workflow at `.github/workflows/android-debug.yml`.
1. Create a GitHub account and a new private repository.
2. Upload the extracted project files/folders to that repository using GitHub's website.
3. Open the repository's Actions tab.
4. Select `Build Android test APK` and choose Run workflow (or push the files to trigger it).
5. When the workflow completes successfully, open its run and download the `street-runner-3d-debug-apk` artifact.
6. This is a DEBUG test APK only. It is not signed with your private release key and must not be submitted as a production store release.
