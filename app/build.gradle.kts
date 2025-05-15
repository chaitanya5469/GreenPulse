plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.gms.google-services")
}

android {
    namespace = "chaitu.android.greenpulse"
    compileSdk = 34

    defaultConfig {
        applicationId = "chaitu.android.greenpulse"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

            buildConfigField("String", "GEMINI_API_KEY", "\"AIzaSyCvVCHYFeGbdHSH0m6Lo1XhvJPLFsvYhmU\"")


        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig=true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation("com.google.firebase:firebase-bom:32.7.2") // BOM version

    // Realtime Database
    implementation("com.google.firebase:firebase-database-ktx:21.0.0")

    // Remote Config
    implementation("com.google.firebase:firebase-config-ktx:22.1.0")

    // Cloud Messaging (Push Notifications)
    implementation("com.google.firebase:firebase-messaging-ktx:24.1.1")
    // 📊 Chart Library (for graphs)
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // 🎨 Lottie Animations
    implementation("com.airbnb.android:lottie-compose:6.2.0")

    // 📦 ViewModel & State Management
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")

    // 🧭 Jetpack Navigation (Compose)
    implementation("androidx.navigation:navigation-compose:2.8.9")

    // 🖼️ Coil (Image Loading)
    implementation("io.coil-kt:coil-compose:2.5.0")

    // 🧪 Kotlin coroutines (just in case you use flows etc.)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    implementation ("androidx.camera:camera-camera2:1.4.2")
    implementation ("androidx.camera:camera-lifecycle:1.4.2")
    implementation ("androidx.camera:camera-view:1.4.2")

// MLKit Barcode
    implementation ("com.google.mlkit:barcode-scanning:17.3.0")

// Lottie
    implementation ("com.airbnb.android:lottie-compose:6.2.0")

        implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.34.0")
    implementation ("io.github.ehsannarmani:compose-charts:0.1.2")

    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.google.ai.client.generativeai:generativeai:0.7.0")

}