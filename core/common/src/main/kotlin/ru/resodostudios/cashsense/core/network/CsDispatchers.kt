package ru.resodostudios.cashsense.core.network

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME)
annotation class Dispatcher

enum class CsDispatchers {
    Default,
    IO,
}