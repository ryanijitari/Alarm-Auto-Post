plugins { id("com.android.application"); id("org.jetbrains.kotlin.android") }
android {
    namespace = "id.jitari.autopostalarm"
    compileSdk = 35
    defaultConfig { applicationId = "id.jitari.autopostalarm"; minSdk = 26; targetSdk = 35; versionCode = 2; versionName = "1.0.1" }
}
