package ru.resodostudios.cashsense.feature.glancewidget

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import ru.resodostudios.cashsense.feature.glancewidget.ui.WalletWidget

class WalletWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = WalletWidget()
}