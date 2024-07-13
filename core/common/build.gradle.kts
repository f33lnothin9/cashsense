plugins {
    alias(libs.plugins.cashsense.jvm.library)
    alias(libs.plugins.cashsense.hilt)
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
}