plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
}

val junitVersion = "4.13.2"
val koinVersion = "4.1.0"
val kotlinCoroutinesVersion = "1.10.2"
val kotlinVersion = "2.2.0"
val lifecycleVersion = "2.9.1"
val lifecycleTestVersion = "2.2.0"
val mockkVersion = "1.14.4"
val okhttpVersion = "5.0.0"
val retrofitVersion = "3.0.0"
val stethoVersion = "1.6.0"

android {
    compileSdk = 36

    defaultConfig {
        applicationId = "uk.co.mainwave.saturdayquizapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        viewBinding = true
    }
    namespace = "uk.co.mainwave.saturdayquizapp"
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.leanback:leanback:1.2.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("com.facebook.stetho:stetho-okhttp3:$stethoVersion")
    implementation("com.facebook.stetho:stetho:$stethoVersion")
    implementation("com.squareup.okhttp3:logging-interceptor:$okhttpVersion")
    implementation("com.squareup.okhttp3:okhttp:$okhttpVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("io.insert-koin:koin-android:$koinVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$kotlinCoroutinesVersion")
    testImplementation("androidx.arch.core:core-testing:$lifecycleTestVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("junit:junit:$junitVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$kotlinCoroutinesVersion")
}