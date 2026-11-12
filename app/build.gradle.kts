plugins {
    alias(libs.plugins.android.application)
}

android {

    namespace = "com.lostfound"

    compileSdk = 36

    defaultConfig {
        applicationId = "com.lostfound"

        minSdk = 26

        targetSdk = 36

        versionCode = 1

        versionName = "1.0"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false

            proguardFiles(
                getDefaultProguardFile(
                    "proguard-android-optimize.txt"
                ),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

dependencies {

    // ============================================================
    // AndroidX
    // ============================================================

    implementation(libs.androidx.core.ktx)

    implementation(libs.androidx.appcompat)

    implementation(libs.material)

    implementation(libs.androidx.activity.ktx)

    implementation(libs.androidx.constraintlayout)


    // ============================================================
    // RecyclerView
    // ============================================================

    implementation(libs.recyclerview)

    implementation(libs.swiperefresh)


    // ============================================================
    // Retrofit
    // ============================================================

    implementation(libs.retrofit)

    implementation(libs.retrofit.gson)

    // Required for String responses from Spring Boot
    // Example:
    // "OTP sent successfully."
    // "OTP verified successfully."
    // "Password reset successfully."
    implementation("com.squareup.retrofit2:converter-scalars:2.11.0")


    // ============================================================
    // OkHttp
    // ============================================================

    implementation(libs.okhttp)

    implementation(libs.logging)


    // ============================================================
    // Gson
    // ============================================================

    implementation(libs.gson)


    // ============================================================
    // Kotlin Coroutines
    // ============================================================

    implementation(libs.coroutines)


    // ============================================================
    // Lifecycle
    // ============================================================

    implementation(libs.androidx.lifecycle.viewmodel)

    implementation(libs.androidx.lifecycle.livedata)


    // ============================================================
    // Navigation
    // ============================================================

    implementation(libs.androidx.navigation.fragment)

    implementation(libs.androidx.navigation.ui)


    // ============================================================
    // Glide
    // ============================================================

    implementation(libs.glide)


    // ============================================================
    // Unit Testing
    // ============================================================

    testImplementation(libs.junit)


    // ============================================================
    // Android Testing
    // ============================================================

    androidTestImplementation(libs.androidx.junit)

    androidTestImplementation(
        libs.androidx.espresso.core
    )
}