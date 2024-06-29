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
import androidx.glance.layout.fillMaxSize
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import ru.resodostudios.cashsense.core.data.repository.WalletsRepository
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.model.data.WalletWithTransactionsAndCategories
import ru.resodostudios.cashsense.core.ui.formatAmount
import ru.resodostudios.cashsense.feature.wallet.widget.R

class WalletWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val walletsEntryPoint =
            EntryPointAccessors.fromApplication(
                context.applicationContext,
                WalletsProviderEntryPoint::class.java,
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
        Column(
            modifier = GlanceModifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = walletPopulated?.wallet?.title ?: "Hello, World!",
                style = TextStyle(GlanceTheme.colors.onBackground),
            )
            currentBalance?.let {
                Text(
                    text = it.formatAmount(walletPopulated.wallet.currency),
                    style = TextStyle(GlanceTheme.colors.onBackground),
                )
            }
        }
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WalletsProviderEntryPoint {
    fun walletsRepository(): WalletsRepository
}