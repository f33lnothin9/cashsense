package ru.resodostudios.cashsense.core.ui

import android.annotation.SuppressLint
import android.content.Context

@SuppressLint("DiscouragedApi")
fun String.getIconId(
    context: Context,
): Int {

    return context.resources.getIdentifier(
        this,
        "drawable",
        context.packageName
    )
}

fun Int.getIconName(context: Context): String = context.resources.getResourceName(this)