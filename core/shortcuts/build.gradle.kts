plugins {
    alias(libs.plugins.cashsense.android.library)
    alias(libs.plugins.cashsense.android.hilt)
}

android {
    namespace = "ru.resodostudios.cashsense.core.shortcuts"
}

dependencies {
    api(projects.core.data)
    api(projects.core.model)
}
