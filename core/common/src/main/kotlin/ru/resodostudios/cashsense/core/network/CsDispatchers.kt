package ru.resodostudios.cashsense.core.network

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME)
annotation class Dispatcher(val csDispatcher: CsDispatchers)

enum class CsDispatchers {
    Default,
    IO,
}