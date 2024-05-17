import ru.resodostudios.cashsense.CsBuildType

plugins {
    alias(libs.plugins.cashsense.android.application)
    alias(libs.plugins.cashsense.android.application.compose)
    alias(libs.plugins.cashsense.android.hilt)
    alias(libs.plugins.cashsense.android.application.firebase)
    alias(libs.plugins.baselineprofile)
    alias(libs.plugins.kotlin.serialization)
    id("com.google.android.gms.oss-licenses-plugin")
}

android {
    defaultConfig {
        applicationId = "ru.resodostudios.cashsense"
        versionCode = 14
        versionName = "0.4.1"

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
            applicationIdSuffix = CsBuildType.RELEASE.applicationIdSuffix
            baselineProfile.automaticGenerationDuringBuild = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    androidResources {
        generateLocaleConfig = true
    }
    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
    namespace = "ru.resodostudios.cashsense"
}

baselineProfile {
    automaticGenerationDuringBuild = false
}

dependencies {
    implementation(projects.feature.home)
    implementation(projects.feature.category.list)
    implementation(projects.feature.category.dialog)
    implementation(projects.feature.subscription.list)
    implementation(projects.feature.subscription.dialog)
    implementation(projects.feature.transaction)
    implementation(projects.feature.wallet.detail)
    implementation(projects.feature.wallet.dialog)
    implementation(projects.feature.settings)

    implementation(projects.core.data)
    implementation(projects.core.designsystem)
    implementation(projects.core.model)
    implementation(projects.core.ui)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.compose.material3.adaptive.navigation)
    implementation(libs.androidx.compose.material3.adaptive.navigation.suite)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.profileinstaller)
    implementation(libs.androidx.tracing.ktx)
    implementation(libs.kotlinx.serialization.json)

    debugImplementation(libs.androidx.compose.ui.testManifest)

    baselineProfile(projects.baselineprofile)
}