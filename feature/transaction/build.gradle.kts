plugins {
    alias(libs.plugins.cashsense.android.feature)
    alias(libs.plugins.cashsense.android.library.compose)
}

android {
    namespace = "ru.resodostudios.cashsense.feature.transaction"
}

dependencies {
    implementation(projects.core.data)

    implementation(projects.feature.category.list)
    implementation(projects.feature.category.dialog)
}