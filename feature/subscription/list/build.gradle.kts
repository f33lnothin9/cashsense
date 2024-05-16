plugins {
    alias(libs.plugins.cashsense.android.feature)
    alias(libs.plugins.cashsense.android.library.compose)
}

android {
    namespace = "ru.resodostudios.cashsense.feature.subscription.list"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.notifications)

    implementation(projects.feature.subscription.dialog)
}