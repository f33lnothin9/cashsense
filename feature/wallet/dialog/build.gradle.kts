plugins {
    alias(libs.plugins.cashsense.android.feature)
    alias(libs.plugins.cashsense.android.library.compose)
}

android {
    namespace = "ru.resodostudios.cashsense.feature.wallet.dialog"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.shortcuts)

    implementation(projects.feature.transaction)
    implementation(projects.feature.category.list)
}