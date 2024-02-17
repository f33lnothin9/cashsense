package ru.resodostudios.cashsense

enum class CsBuildType(val applicationIdSuffix: String? = null) {
    DEBUG(".debug"),
    RELEASE,
}