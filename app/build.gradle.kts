import org.jetbrains.kotlin.storage.CacheResetOnProcessCanceled.enabled

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22"
    kotlin("kapt")
}

android {
    namespace = "com.example.mediaplayer"
    compileSdk = 36
    buildFeatures {
        viewBinding = true
    }
    defaultConfig {
        applicationId = "com.example.mediaplayer"
        minSdk = 24
        targetSdk = 36
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
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("com.airbnb.android:lottie:3.4.0")
    implementation("androidx.navigation:navigation-fragment:2.5.3")
    implementation("androidx.navigation:navigation-ui:2.5.3")
    implementation("com.github.sparrow007:carouselrecyclerview:1.2.6")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")



    implementation ("androidx.room:room-runtime:2.6.1")
    annotationProcessor ("androidx.room:room-compiler:2.6.1")


    //serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    //Scoped API
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose-android:2.8.7")

    //koin
    val koin_android_version = "4.0.2"
    implementation("io.insert-koin:koin-android:$koin_android_version")
}