plugins {
    id("com.android.application")
    id ("com.google.gms.google-services")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt") // Add this line

}

android {
    namespace = "com.example.cafeteria"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.cafeteria"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // ✅ Add this line to enable vector drawable support
        vectorDrawables.useSupportLibrary = true
    }

    buildFeatures.viewBinding = true
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("com.google.firebase:firebase-firestore:25.1.3")
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.activity:activity:1.12.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    implementation ("com.google.firebase:firebase-auth:22.1.2") // ✅ Firebase Auth
    implementation ("com.google.firebase:firebase-firestore:24.11.0") // or latest version
    implementation(platform("com.google.firebase:firebase-bom:33.12.0"))
    implementation("com.google.firebase:firebase-analytics")

    implementation("com.google.firebase:firebase-database")

    implementation("io.appwrite:sdk-for-android:12.0.0") // Check for the latest version
    implementation("com.razorpay:checkout:1.6.33")

    implementation("com.github.bumptech.glide:glide:4.16.0")
    kapt("com.github.bumptech.glide:compiler:4.16.0")
}