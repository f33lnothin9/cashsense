package ru.resodostudios.cashsense.feature.wallet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.component.CsTag
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.theme.CsTheme
import ru.resodostudios.cashsense.core.model.data.Currency
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.model.data.Wallet
import ru.resodostudios.cashsense.core.ui.formatAmountWithCurrency
import java.math.BigDecimal
import ru.resodostudios.cashsense.feature.transaction.R as transactionR

@Composable
fun WalletCard(
    wallet: Wallet,
    transactions: List<Transaction>,
    onWalletClick: (String) -> Unit,
    onTransactionCreate: (String) -> Unit,
    onWalletMenuClick: (String, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentWalletBalance = wallet.initialBalance
        .plus(transactions.sumOf { it.amount })
        .formatAmountWithCurrency(wallet.currency)

    OutlinedCard(
        onClick = { onWalletClick(wallet.id) },
        shape = RoundedCornerShape(24.dp),
        modifier = modifier,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 12.dp),
        ) {
            Text(
                text = wallet.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                text = currentWalletBalance,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge,
            )
            FinanceIndicators(
                transactions = transactions,
                currency = wallet.currency,
            )
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(start = 16.dp, end = 8.dp, bottom = 12.dp)
                .fillMaxWidth(),
        ) {
            Button(
                onClick = { onTransactionCreate(wallet.id) },
            ) {
                Text(stringResource(transactionR.string.feature_transaction_add_transaction))
            }
            IconButton(
                onClick = { onWalletMenuClick(wallet.id, currentWalletBalance) },
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(CsIcons.MoreVert),
                    contentDescription = stringResource(R.string.feature_wallet_menu_icon_description),
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FinanceIndicators(
    transactions: List<Transaction>,
    currency: String,
    modifier: Modifier = Modifier,
) {
    val walletIncome = transactions
        .asSequence()
        .filter { it.amount > BigDecimal.ZERO }
        .sumOf { it.amount }
        .abs()
    val walletExpenses = transactions
        .asSequence()
        .filter { it.amount < BigDecimal.ZERO }
        .sumOf { it.amount }
        .abs()

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier,
    ) {
        if (walletIncome != BigDecimal.ZERO) {
            CsTag(
                text = walletIncome.formatAmountWithCurrency(currency),
                iconId = CsIcons.TrendingUp,
            )
        }
        if (walletExpenses != BigDecimal.ZERO) {
            CsTag(
                text = walletExpenses.formatAmountWithCurrency(currency),
                color = MaterialTheme.colorScheme.errorContainer,
                iconId = CsIcons.TrendingDown,
            )
        }
    }
}

@PreviewLightDark
@Composable
fun WalletCardPreview() {
    CsTheme {
        Surface {
            WalletCard(
                wallet = Wallet(
                    id = "",
                    title = "Main",
                    initialBalance = BigDecimal(1499.99),
                    currency = Currency.USD.name,
                ),
                transactions = emptyList(),
                onWalletClick = {},
                onTransactionCreate = {},
                onWalletMenuClick = { _, _ ->},
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}