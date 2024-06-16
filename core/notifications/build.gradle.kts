plugins {
    alias(libs.plugins.cashsense.android.library)
    alias(libs.plugins.cashsense.android.hilt)
}

android {
    namespace = "ru.resodostudios.cashsense.core.notifications"
}

dependencies {
    api(projects.core.common)
    api(projects.core.model)

    implementation(projects.core.designsystem)
    implementation(projects.core.ui)
}
