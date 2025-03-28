import java.util.Properties

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.gms.google-services")
    kotlin("kapt")
    id("kotlin-parcelize")
}

android {
    namespace = "com.example.astroshare"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.astroshare"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "ASTRONOMY_API_APP_ID",
            "\"${localProperties["ASTRONOMY_API_APP_ID"]}\""
        )
        buildConfigField(
            "String",
            "ASTRONOMY_API_APP_SECRET",
            "\"${localProperties["ASTRONOMY_API_APP_SECRET"]}\""
        )

        buildConfigField(
            "String",
            "ASTRONOMY_API_AFTER_HASH",
            "\"${localProperties["ASTRONOMY_API_AFTER_HASH"]}\""
        )

        javaCompileOptions {
            annotationProcessorOptions {
                argument("room.incremental", "true")
            }
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
    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

}

val room_version = "2.6.1"

dependencies {
// Implementations by me //
    // Nav Graph
    implementation ("androidx.navigation:navigation-fragment-ktx:2.8.5")
    implementation ("androidx.navigation:navigation-ui-ktx:2.8.5")
    // OSM Maps
    implementation ("org.osmdroid:osmdroid-android:6.1.13")
    // Location allowance
    implementation ("com.google.android.gms:play-services-location:21.3.0")
    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.google.firebase:firebase-firestore:24.9.1")
    implementation("com.google.firebase:firebase-auth:22.0.0")
    implementation ("com.google.firebase:firebase-appcheck-debug:16.0.0")
    // SQLite and Room
    implementation ("androidx.room:room-runtime:$room_version")
    implementation(libs.firebase.storage.ktx)
    implementation(libs.androidx.media3.common.ktx)
    kapt ("androidx.room:room-compiler:$room_version")
    implementation ("androidx.room:room-ktx:$room_version")
    testImplementation("androidx.room:room-testing:$room_version")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    // Cloudinary
    implementation("com.cloudinary:cloudinary-android:3.0.2")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.0")
    // Glide
    implementation ("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")
    // OkHTTP
    implementation ("com.squareup.okhttp3:okhttp:4.11.0")
    // Material
    implementation ("com.google.android.material:material:1.9.0")
    // Picasso
    implementation ("com.squareup.picasso:picasso:2.8")



    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}