plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.myapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.myapp"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "RAPID_API_KEY", "\"${project.properties["RAPID_API_KEY"] ?: ""}\"")
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

    kotlinOptions {
        jvmTarget = "11"
    }

    // Add this block to enable buildConfig
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    // AndroidX Core Libraries
    implementation ("androidx.core:core-ktx:1.13.0")
    implementation ("androidx.core:core-ktx:1.13.0")

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")

    // Material Design
    implementation("com.google.android.material:material:1.12.0")

    // Google Play Services (Location and Maps)
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.google.android.gms:play-services-maps:19.0.0")

    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")

    // Maps (OpenStreetMap)
    implementation("org.osmdroid:osmdroid-android:6.1.11")

//    // ML Kit Dependencies
//    implementation("com.google.mlkit:entity-extraction:18.0.0")
//    implementation("com.google.mlkit:text-recognition:16.0.1")
//    implementation("com.google.mlkit:speech-recognition:17.0.1")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}