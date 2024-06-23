package ru.resodostudios.cashsense.wallet.widget.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.glance.GlanceId
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.components.TitleBar
import androidx.glance.appwidget.provideContent
import androidx.glance.text.Text
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

class WalletWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {

        provideContent {
            GlanceTheme {
                WalletWidgetContent()
            }
        }
    }

    @Composable
    private fun WalletWidgetContent() {
        Scaffold(
            titleBar = {
                TitleBar(
                    startIcon = ImageProvider(CsIcons.Wallet),
                    title = "Wallets",
                )
            },
        ) {
            Text("Hello world")
        }
    }
}