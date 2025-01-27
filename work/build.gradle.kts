plugins {
    alias(libs.plugins.cashsense.android.library)
    alias(libs.plugins.cashsense.hilt)
}

android {
    namespace = "ru.resodostudios.cashsense.work"
}

dependencies {
    ksp(libs.hilt.ext.compiler)

    implementation(projects.core.data)

    implementation(libs.androidx.work.ktx)
    implementation(libs.hilt.ext.work)
}
