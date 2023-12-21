plugins {
    alias(libs.plugins.cashsense.android.library)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "ru.resodostudios.cashsense.core.model"
}

dependencies {
    implementation(libs.kotlinx.datetime)
}