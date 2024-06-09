package ru.resodostudios.cashsense.core.data.util

interface ShortcutManager {

    fun addTransactionShortcut(
        walletId: String,
        shortLabel: String,
        longLabel: String,
    )

    fun removeShortcuts()
}