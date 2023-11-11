package ru.resodostudios.cashsense

/**
 * This is shared between :app and :benchmarks module to provide configurations type safety.
 */
enum class CsBuildType(val applicationIdSuffix: String? = null) {
    DEBUG(".debug"),
    RELEASE,
    BENCHMARK(".benchmark")
}
