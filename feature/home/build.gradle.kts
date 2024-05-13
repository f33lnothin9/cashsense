plugins {
    alias(libs.plugins.cashsense.android.feature)
    alias(libs.plugins.cashsense.android.library.compose)
}

android {
    namespace = "ru.resodostudios.cashsense.feature.home"
}

dependencies {
    implementation(projects.core.data)

    implementation(projects.feature.transaction)
    implementation(projects.feature.wallet.detail)
    implementation(projects.feature.wallet.dialog)
    implementation(projects.feature.category.list)

    implementation(libs.accompanist.permissions)
}