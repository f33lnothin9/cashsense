plugins {
    alias(libs.plugins.cashsense.android.feature)
    alias(libs.plugins.cashsense.android.library.compose)
}

android {
    namespace = "ru.resodostudios.cashsense.feature.wallet.widget"
}

dependencies {
    implementation(projects.core.data)

    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.material3)

    debugImplementation(libs.androidx.glance.appwidget.preview)
    debugImplementation(libs.androidx.glance.preview)
}
