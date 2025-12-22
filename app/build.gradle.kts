plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
}

val junitVersion = "4.13.2"
val koinVersion = "4.1.1"
val kotlinCoroutinesVersion = "1.10.2"
val kotlinVersion = "2.3.0"
val lifecycleVersion = "2.10.0"
val lifecycleTestVersion = "2.2.0"
val mockkVersion = "1.14.7"
val okhttpVersion = "5.3.2"
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
    // Import BOMs for version management
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:$kotlinVersion"))
    implementation(platform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:$kotlinCoroutinesVersion"))
    implementation(platform("com.squareup.okhttp3:okhttp-bom:$okhttpVersion"))
    implementation(platform("com.squareup.retrofit2:retrofit-bom:$retrofitVersion"))
    implementation(platform("io.insert-koin:koin-bom:$koinVersion"))

    // Dependencies managed by BOMs (no version numbers needed)
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android")
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")
    implementation("com.squareup.retrofit2:retrofit")
    implementation("com.squareup.retrofit2:converter-gson")
    implementation("io.insert-koin:koin-android")

    // Dependencies not managed by BOMs (explicit versions)
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.leanback:leanback:1.2.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("com.facebook.stetho:stetho:$stethoVersion")
    implementation("com.facebook.stetho:stetho-okhttp3:$stethoVersion")

    // Test dependencies
    testImplementation("androidx.arch.core:core-testing:$lifecycleTestVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("junit:junit:$junitVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
}