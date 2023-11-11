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
    implementation(projects.core.model)

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.datetime)

    //androidTestImplementation(projects.core.testing)
}
