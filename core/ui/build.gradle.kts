plugins {
    alias(libs.plugins.cashsense.android.library)
    alias(libs.plugins.cashsense.android.library.compose)
}

android {
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    namespace = "ru.resodostudios.cashsense.core.ui"
}

dependencies {
    api(projects.core.designsystem)
    api(projects.core.locales)
    api(projects.core.model)

    implementation(projects.core.common)

    implementation(libs.lottie.compose)
    implementation(libs.vico.compose)
    implementation(libs.vico.compose.m3)
    implementation(libs.vico.core)
}
