plugins {
    alias(libs.plugins.cashsense.android.feature)
    alias(libs.plugins.cashsense.android.library.compose)
}

android {
    namespace = "ru.resodostudios.cashsense.feature.transactions"
}

dependencies {
    implementation(projects.feature.categories)

    implementation(libs.kotlinx.datetime)
}