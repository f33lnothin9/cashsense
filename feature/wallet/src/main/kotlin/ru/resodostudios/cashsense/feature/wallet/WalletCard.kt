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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.component.CsTag
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.theme.CsTheme
import ru.resodostudios.cashsense.core.model.data.Currency
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.model.data.Wallet
import ru.resodostudios.cashsense.core.ui.EditAndDeleteDropdownMenu
import ru.resodostudios.cashsense.core.ui.formatAmountWithCurrency
import java.math.BigDecimal
import ru.resodostudios.cashsense.feature.transaction.R as transactionR

@Composable
fun WalletCard(
    wallet: Wallet,
    transactions: List<Transaction>,
    onWalletClick: (String) -> Unit,
    onTransactionCreate: (String) -> Unit,
    onEdit: (String) -> Unit,
    onDelete: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
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
                .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
        ) {
            Text(
                text = wallet.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                text = wallet.initialBalance
                    .plus(transactions.sumOf { it.amount })
                    .formatAmountWithCurrency(wallet.currency),
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
            EditAndDeleteDropdownMenu(
                onEdit = { onEdit(wallet.id) },
                onDelete = { onDelete(wallet.id) },
            )
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
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier,
    ) {
        val walletIncome = transactions
            .asSequence()
            .filter { it.amount > BigDecimal(0) }
            .sumOf { it.amount }
        if (walletIncome != BigDecimal(0)) {
            CsTag(
                text = walletIncome
                    .abs()
                    .formatAmountWithCurrency(currency),
                iconId = CsIcons.TrendingUp,
            )
        }

        val walletExpenses = transactions
            .asSequence()
            .filter { it.amount < BigDecimal(0) }
            .sumOf { it.amount }
        if (walletExpenses != BigDecimal(0)) {
            CsTag(
                text = walletExpenses
                    .abs()
                    .formatAmountWithCurrency(currency),
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
                    title = "Wallet 1",
                    initialBalance = 1500.85.toBigDecimal(),
                    currency = Currency.USD.name
                ),
                transactions = emptyList(),
                onWalletClick = {},
                onTransactionCreate = {},
                onEdit = {},
                onDelete = {},
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}