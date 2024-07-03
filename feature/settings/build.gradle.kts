plugins {
    alias(libs.plugins.cashsense.android.feature)
    alias(libs.plugins.cashsense.android.library.compose)
    alias(libs.plugins.aboutlibraries)
}

android {
    namespace = "ru.resodostudios.cashsense.feature.settings"
}

dependencies {
    implementation(projects.core.data)

    implementation(libs.aboutlibraries.core)
    implementation(libs.aboutlibraries.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.android.material)
    implementation(libs.google.oss.licenses)
}