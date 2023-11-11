plugins {
    alias(libs.plugins.cashsense.android.library)
    alias(libs.plugins.cashsense.android.hilt)
}

android {
    namespace = "ru.resodostudios.cashsense.core.data"
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
}

dependencies {
    //implementation(projects.core.common)
    implementation(projects.core.database)
    //implementation(projects.core.datastore)
    implementation(projects.core.model)
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.datetime)

    //testImplementation(projects.core.datastoreTest)
    //testImplementation(projects.core.testing)
}
