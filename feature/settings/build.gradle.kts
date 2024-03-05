plugins {
    alias(libs.plugins.cashsense.android.feature)
    alias(libs.plugins.cashsense.android.library.compose)
}

android {
    namespace = "ru.resodostudios.cashsense.feature.settings"
}

dependencies {
    implementation(projects.core.data)

    implementation(libs.androidx.appcompat)
    implementation(libs.google.oss.licenses)
}