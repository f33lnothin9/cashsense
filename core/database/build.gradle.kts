plugins {
    alias(libs.plugins.cashsense.android.library)
    alias(libs.plugins.cashsense.android.hilt)
    alias(libs.plugins.cashsense.android.room)
}

android {
    defaultConfig {
        testInstrumentationRunner =
            "ru.resodostudios.cashsense.core.testing.CsTestRunner"
    }
    namespace = "ru.resodostudios.cashsense.core.database"
}

dependencies {
    api(projects.core.model)

    implementation(libs.kotlinx.datetime)
}
