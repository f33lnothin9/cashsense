import ru.resodostudios.cashsense.CsBuildType

plugins {
    alias(libs.plugins.cashsense.android.application)
    alias(libs.plugins.cashsense.android.application.compose)
    alias(libs.plugins.cashsense.android.hilt)
    alias(libs.plugins.cashsense.android.application.firebase)
    alias(libs.plugins.baselineprofile)
    id("com.google.android.gms.oss-licenses-plugin")
}

android {
    defaultConfig {
        applicationId = "ru.resodostudios.cashsense"
        versionCode = 5
        versionName = "0.2.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = CsBuildType.DEBUG.applicationIdSuffix
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            baselineProfile.automaticGenerationDuringBuild = true
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
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
    namespace = "ru.resodostudios.cashsense"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.designsystem)
    implementation(projects.core.model)
    implementation(projects.core.ui)

    implementation(projects.feature.home)
    implementation(projects.feature.categories)
    implementation(projects.feature.category)
    implementation(projects.feature.subscriptions)
    implementation(projects.feature.subscription)
    implementation(projects.feature.transaction)
    implementation(projects.feature.wallet)
    implementation(projects.feature.settings)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.profileinstaller)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.compose.material3.adaptive.navigation.suite)
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.window.manager)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.datetime)

    debugImplementation(libs.androidx.compose.ui.testManifest)

    baselineProfile(projects.baselineprofile)
}

baselineProfile {
    automaticGenerationDuringBuild = false
}