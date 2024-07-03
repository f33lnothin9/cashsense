plugins {
    alias(libs.plugins.cashsense.android.feature)
    alias(libs.plugins.cashsense.android.library.compose)
}

android {
    namespace = "ru.resodostudios.cashsense.feature.wallet.detail"
}

dependencies {
    implementation(projects.core.data)

    implementation(projects.feature.wallet.dialog)
    implementation(projects.feature.transaction)

    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.compose.material3.adaptive.navigation)
    implementation(libs.ycharts)
}