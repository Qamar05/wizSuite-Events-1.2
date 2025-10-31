plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    // Add the Performance Monitoring Gradle plugin
    id("com.google.firebase.firebase-perf")

    id("kotlin-kapt")
}

android {
    namespace = "com.wizsuite.event"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.wizsuite.event"
        minSdk = 24
        targetSdk = 35
        versionCode = 7
        versionName = "1.2.17"

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

    signingConfigs {
        getByName("debug") {
            keyAlias = "wizSuite Events"
            keyPassword = "wizsuite_events@2023"
            storeFile = file("wizsuite_events.jks")
            storePassword = "wizsuite_events@2023"
        }
        create("release") {
            keyAlias = "wizSuite Events"
            keyPassword = "wizsuite_events@2023"
            storeFile = file("wizsuite_events.jks")
            storePassword = "wizsuite_events@2023"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.activity)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.work.runtime.ktx)

    //Circular Imageview
    implementation(libs.circleimageview)
    implementation(libs.picasso)

    //Retrofit for API Calling
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // coroutine
    implementation (libs.kotlinx.coroutines.android)
    implementation (libs.kotlinx.coroutines.core)

    // Add the Firebase SDK for Crashlytics.
    implementation(libs.firebase.crashlytics)
    //Add the Firebase SDK for Google Analytics.
    implementation(libs.firebase.analytics)
    //Firebase Messaging
    implementation(libs.firebase.messaging)
    //Performance Monitoring
    implementation(libs.firebase.perf)

    //Sizing DP and SP
    implementation(libs.sdp.android)

    //For Retrofit Logging
    implementation(libs.logging.interceptor)
}