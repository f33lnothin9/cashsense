package ru.resodostudios.cashsense.wallet.widget.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.components.TitleBar
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.model.data.WalletWithTransactionsAndCategories
import ru.resodostudios.cashsense.core.ui.formatAmount
import ru.resodostudios.cashsense.feature.wallet.widget.R
import ru.resodostudios.cashsense.wallet.widget.WalletWidgetEntryPoint
import ru.resodostudios.cashsense.core.ui.R as uiR

class WalletWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val walletsEntryPoint =
            EntryPointAccessors.fromApplication(
                context.applicationContext,
                WalletWidgetEntryPoint::class.java,
            )
        val walletsRepository = walletsEntryPoint.walletsRepository()

        val initialWallets = withContext(Dispatchers.IO) {
            walletsRepository.getWalletsWithTransactions().first()
        }

        provideContent {
            val wallets by walletsRepository.getWalletsWithTransactions().collectAsState(initialWallets)

            GlanceTheme {
                WalletWidgetContent(wallets)
            }
        }
    }
}

@Composable
private fun WalletWidgetContent(wallets: List<WalletWithTransactionsAndCategories>) {
    val walletPopulated = wallets.firstOrNull()

    val currentBalance =
        walletPopulated?.wallet?.initialBalance?.plus(walletPopulated.transactionsWithCategories.sumOf { it.transaction.amount })

    val context = LocalContext.current

    Scaffold(
        titleBar = {
            TitleBar(
                startIcon = ImageProvider(CsIcons.Wallet),
                title = context.getString(R.string.wallet_widget_title),
            )
        },
    ) {
        WalletItem(
            title = walletPopulated?.wallet?.title ?: context.getString(uiR.string.none),
            currentBalance = currentBalance?.formatAmount(walletPopulated.wallet.currency) ?: context.getString(uiR.string.none),
        )
    }
}

@Composable
fun WalletItem(
    title: String,
    currentBalance: String,
    modifier: GlanceModifier = GlanceModifier,
) {
    Row {
        Column(
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.Start,
            modifier = modifier,
        ) {
            Text(
                text = title,
                style = TextStyle(GlanceTheme.colors.onBackground),
                maxLines = 1,
            )
            Text(
                text = currentBalance,
                style = TextStyle(GlanceTheme.colors.onBackground),
                maxLines = 1,
            )
        }
    }
}