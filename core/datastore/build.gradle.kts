plugins {
    alias(libs.plugins.cashsense.android.library)
    alias(libs.plugins.cashsense.android.hilt)
}

android {
    defaultConfig {
        consumerProguardFiles("consumer-proguard-rules.pro")
    }
    namespace = "ru.resodostudios.cashsense.core.datastore"
    testOptions {
        unitTests {
            isReturnDefaultValues = true
        }
    }
}

dependencies {
    api(projects.core.datastoreProto)
    implementation(projects.core.common)
    implementation(projects.core.model)
    implementation(libs.androidx.dataStore.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.protobuf.kotlin.lite)
}