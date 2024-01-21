plugins {
    alias(libs.plugins.cashsense.android.application)
    alias(libs.plugins.cashsense.android.application.compose)
    alias(libs.plugins.cashsense.android.hilt)
    alias(libs.plugins.cashsense.android.application.firebase)
    id("com.google.android.gms.oss-licenses-plugin")
}

android {
    namespace = "ru.resodostudios.cashsense"

    defaultConfig {
        applicationId = "ru.resodostudios.cashsense"
        versionCode = 4
        versionName = "0.2.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.designsystem)
    implementation(projects.core.model)
    implementation(projects.core.ui)

    implementation(projects.feature.home)
    implementation(projects.feature.categories)
    implementation(projects.feature.subscriptions)
    implementation(projects.feature.transaction)
    implementation(projects.feature.wallet)
    implementation(projects.feature.settings)

    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.compose.material3.adaptive.navigation.suite)
    implementation(libs.androidx.compose.material3.windowSizeClass)

    implementation(libs.androidx.core.splashscreen)

    implementation(libs.androidx.window.manager)

    debugImplementation(libs.androidx.compose.ui.testManifest)

    implementation(libs.androidx.activity.compose)

    implementation(libs.androidx.lifecycle.viewModelCompose)
    implementation(libs.androidx.lifecycle.runtimeCompose)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.kotlinx.datetime)
}