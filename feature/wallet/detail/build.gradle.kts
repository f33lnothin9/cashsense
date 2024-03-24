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
    implementation(projects.feature.categories)
}