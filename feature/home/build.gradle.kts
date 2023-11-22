plugins {
    alias(libs.plugins.cashsense.android.feature)
    alias(libs.plugins.cashsense.android.library.compose)
}

android {
    namespace = "ru.resodostudios.cashsense.feature.home"
}

dependencies {
    implementation(projects.feature.transactions)
    implementation(projects.feature.wallets)

    implementation(libs.kotlinx.datetime)
    implementation(libs.androidx.activity.compose)
}
