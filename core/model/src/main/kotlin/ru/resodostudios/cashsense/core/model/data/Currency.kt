package ru.resodostudios.cashsense.core.model.data

enum class Currency(val symbol: String) {
    USD("$"),
    EUR("€"),
    RUB("₽"),
    TRY("₺"),
    INR("₹"),
    GBP("£"),
    CHF("₣"),
    JPY("¥"),
    CNY("¥")
}