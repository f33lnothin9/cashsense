plugins {
    alias(libs.plugins.cashsense.android.library)
    alias(libs.plugins.cashsense.hilt)
}

android {
    namespace = "ru.resodostudios.cashsense.core.shortcuts"
}

dependencies {
    api(projects.core.common)
    api(projects.core.model)

    implementation(projects.core.locales)
}
