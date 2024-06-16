package ru.resodostudios.cashsense.core.shortcuts

interface ShortcutManager {

    fun addTransactionShortcut(
        walletId: String,
        shortLabel: String,
        longLabel: String,
    )

    fun removeShortcuts()
}