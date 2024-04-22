plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)

    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.testapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.testapp"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation ("androidx.core:core-ktx:1.6.0")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.3.1")
    implementation ("androidx.activity:activity-compose:1.3.1")
    implementation ("androidx.compose.ui:ui:1.0.1")
    implementation ("androidx.compose.material:material:1.0.1")
    implementation ("androidx.compose.ui:ui-tooling-preview:1.0.1")
    implementation ("androidx.navigation:navigation-compose:2.4.0")
    implementation ("androidx.navigation:navigation-compose:2.3.5")


    // To use Kotlin annotation processing tool (kapt)
    // To use Kotlin Symbol Processing (KSP)
    implementation ("com.google.devtools.ksp:symbol-processing-api:<version>")
    //ksp ("com.google.devtools.ksp:symbol-processing:<version>")
    implementation("androidx.room:room-runtime:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    // optional - Kotlin Extensions and Coroutines support for Room

    // optional - RxJava2 support for Room

    // optional - RxJava3 support for Room

    // optional - Guava support for Room, including Optional and ListenableFuture

    // optional - Test helpers

    // optional - Paging 3 Integration

    implementation("com.google.devtools.ksp:symbol-processing-api:1.9.21-1.0.15")
    implementation ("androidx.compose.material:material-icons-extended:1.1.0")
    implementation ("androidx.compose.ui:ui:1.0.1")
    implementation ("androidx.compose.ui:ui-tooling-preview:1.0.1")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.3.1")
    implementation ("androidx.activity:activity-compose:1.3.1")
    implementation ("com.google.android.material:material:1.4.0")
    implementation ("androidx.compose.material3:material3:1.0.0-alpha03")
    implementation ("androidx.activity:activity-compose:1.4.0")
    implementation ("com.google.android.material:material:1.4.0")
    implementation ("com.google.android.gms:play-services-fitness:20.0.0")
    implementation ("com.google.android.gms:play-services-auth:19.0.0")





}