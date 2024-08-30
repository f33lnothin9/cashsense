package ru.resodostudios.cashsense.wallet.widget.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.components.CircleIconButton
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.components.TitleBar
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.model.data.WalletWithTransactionsAndCategories
import ru.resodostudios.cashsense.core.ui.formatAmount
import ru.resodostudios.cashsense.core.util.Constants.DEEP_LINK_SCHEME_AND_HOST
import ru.resodostudios.cashsense.core.util.Constants.HOME_PATH
import ru.resodostudios.cashsense.core.util.Constants.TARGET_ACTIVITY_NAME
import ru.resodostudios.cashsense.feature.wallet.widget.R
import ru.resodostudios.cashsense.wallet.widget.WalletWidgetEntryPoint
import ru.resodostudios.cashsense.core.ui.R as uiR

class WalletWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val walletsEntryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            WalletWidgetEntryPoint::class.java,
        )
        val walletsRepository = walletsEntryPoint.walletsRepository()

        val initialWallets = withContext(Dispatchers.IO) {
            walletsRepository.getWalletsWithTransactions().first()
        }

        provideContent {
            val wallets by walletsRepository.getWalletsWithTransactions().collectAsState(initialWallets)

            CsGlanceTheme {
                WalletWidgetContent(wallets)
            }
        }
    }
}

@Composable
private fun WalletWidgetContent(wallets: List<WalletWithTransactionsAndCategories>) {
    Scaffold(
        titleBar = {
            TitleBar(
                startIcon = ImageProvider(CsIcons.Wallet),
                title = LocalContext.current.getString(R.string.wallet_widget_title),
            )
        },
        modifier = GlanceModifier.cornerRadius(16.dp),
    ) {
        if (wallets.isNotEmpty()) {
            LazyColumn {
                items(
                    items = wallets,
                    itemId = { walletPopulated ->
                        walletPopulated.wallet.id
                            .filter { it.isDigit() }
                            .take(9)
                            .toLong()
                    }
                ) { walletPopulated ->
                    val currentBalance = walletPopulated.wallet.initialBalance
                        .plus(walletPopulated.transactionsWithCategories
                            .sumOf { it.transaction.amount })

                    WalletItem(
                        walletId = walletPopulated.wallet.id,
                        title = walletPopulated.wallet.title,
                        currentBalance = currentBalance.formatAmount(walletPopulated.wallet.currency),
                        modifier = GlanceModifier.padding(start = 4.dp, end = 4.dp, bottom = 8.dp),
                    )
                }
            }
        } else {
            Box(
                contentAlignment = Alignment.Center,
                modifier = GlanceModifier.fillMaxSize(),
            ) {
                Text(
                    text = LocalContext.current.getString(R.string.wallet_widget_empty),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = GlanceTheme.colors.onBackground,
                    ),
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
fun WalletItem(
    walletId: String,
    title: String,
    currentBalance: String,
    modifier: GlanceModifier = GlanceModifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.Start,
            modifier = GlanceModifier.defaultWeight(),
        ) {
            Text(
                text = title,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = GlanceTheme.colors.onBackground,
                ),
                maxLines = 1,
            )
            Spacer(GlanceModifier.height(4.dp))
            Text(
                text = currentBalance,
                style = TextStyle(GlanceTheme.colors.onBackground),
                maxLines = 1,
            )
        }
        CircleIconButton(
            imageProvider = ImageProvider(CsIcons.Add),
            onClick = actionStartActivity(
                Intent().apply {
                    action = Intent.ACTION_VIEW
                    data = "$DEEP_LINK_SCHEME_AND_HOST/$HOME_PATH/$walletId/true".toUri()
                    component = ComponentName(
                        LocalContext.current.packageName,
                        TARGET_ACTIVITY_NAME,
                    )
                }
            ),
            contentDescription = LocalContext.current.getString(uiR.string.core_ui_add),
        )
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = 250, heightDp = 200)
@Composable
fun WalletWidgetPreview() {
    CsGlanceTheme {
        WalletWidgetContent(wallets = emptyList())
    }
}