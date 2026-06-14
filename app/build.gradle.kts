plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    // Applied Google services plugin to read the google-services.json file
    id("com.google.gms.google-services")
    // Added Kotlin Symbol Processing (KSP) for modern, fast Room compilation
    id("com.google.devtools.ksp") version "2.0.21-1.0.27"
}

android {
    namespace = "com.example.a221007_tharssan_drnelson_project2"
    compileSdk = 36 // Standard stable compilation environment targeting your configuration

    defaultConfig {
        applicationId = "com.example.a221007_tharssan_drnelson_project2"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    // --- Pre-existing Default Version Catalog Core Dependencies ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)

    // --- Project 2 Core Technical Pillar Additions ---

    // Jetpack Compose Navigation Core Support Hook
    implementation("androidx.navigation:navigation-compose:2.8.5")

    // Pillar 1: Local Offline Data Persistence (Room Engine Setup)
    // Upgraded Room version to fix "unexpected jvm signature V" error with Kotlin 2.0/KSP
    val roomVersion = "2.7.0-alpha11"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    // Swapped out legacy/broken annotation processor with modern KSP integration
    ksp("androidx.room:room-compiler:$roomVersion")

    // Pillar 2: Online Data Streaming (Retrofit REST Web Client)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Pillar 3: Remote Cloud Infrastructure Integration (Firebase Firestore)
    implementation(platform("com.google.firebase:firebase-bom:33.1.1"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-firestore")

    // Pillar 4: Embedded Mobile Hardware Integrations (GPS Telemetry)
    implementation("com.google.android.gms:play-services-location:21.3.0")

    // Image Content Management Utilities (Coil Content Renderer Pipeline)
    implementation("io.coil-kt:coil-compose:2.6.0")

    // --- Core Architecture Testing Layer ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}