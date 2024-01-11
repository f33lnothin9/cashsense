plugins {
    alias(libs.plugins.cashsense.android.library)
    alias(libs.plugins.ksp)
}

android {
    namespace = "ru.resodostudios.cashsense.core.domain"
}

dependencies {
    api(projects.core.data)
    api(projects.core.model)

    //testImplementation(projects.core.testing)
}