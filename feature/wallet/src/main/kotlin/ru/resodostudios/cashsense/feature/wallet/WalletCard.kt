package ru.resodostudios.cashsense.feature.wallet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.theme.CsTheme
import ru.resodostudios.cashsense.core.model.data.Currency
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.model.data.TransactionWithCategory
import ru.resodostudios.cashsense.core.model.data.Wallet
import ru.resodostudios.cashsense.core.ui.EditAndDeleteDropdownMenu
import ru.resodostudios.cashsense.core.ui.getFormattedAmountAndCurrency
import kotlin.math.abs
import ru.resodostudios.cashsense.feature.transaction.R as transactionR

@Composable
fun WalletCard(
    wallet: Wallet,
    transactionsWithCategories: List<TransactionWithCategory>,
    onWalletClick: (Long) -> Unit,
    onTransactionCreate: (Long) -> Unit,
    onEdit: (Wallet) -> Unit,
    onDelete: (Wallet, List<Transaction>) -> Unit
) {
    val transactions = transactionsWithCategories.map { it.transaction }

    OutlinedCard(
        onClick = { onWalletClick(wallet.walletId) },
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 12.dp)
        ) {
            Text(
                text = wallet.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = getFormattedAmountAndCurrency(
                    amount = getCurrentBalance(
                        wallet.startBalance,
                        transactionsWithCategories.map { it.transaction }
                    ),
                    currencyName = wallet.currency.name
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge
            )
            WalletFinancesSection(
                transactions = transactions,
                currencyName = wallet.currency.name
            )
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(start = 14.dp, end = 6.dp, bottom = 12.dp, top = 8.dp)
                .fillMaxWidth()
        ) {
            Button(
                onClick = { onTransactionCreate(wallet.walletId) }
            ) {
                Text(text = stringResource(transactionR.string.add_transaction))
            }
            EditAndDeleteDropdownMenu(
                onEdit = { onEdit(wallet) },
                onDelete = { onDelete(wallet, transactionsWithCategories.map { it.transaction }) }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WalletFinancesSection(
    transactions: List<Transaction>,
    currencyName: String
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        val positiveTransactions = transactions.filter { it.amount > 0 }
        val walletIncome = positiveTransactions.sumOf { it.amount }
        if (walletIncome != 0.0) {
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(
                        start = 8.dp,
                        top = 3.dp,
                        end = 8.dp,
                        bottom = 3.dp
                    )
                ) {
                    Icon(
                        painter = painterResource(CsIcons.TrendingUp),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = getFormattedAmountAndCurrency(
                            amount = abs(walletIncome),
                            currencyName = currencyName
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }

        val negativeTransactions = transactions.filter { it.amount < 0 }
        val walletExpenses = negativeTransactions.sumOf { it.amount }
        if (walletExpenses != 0.0) {
            Surface(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(
                        start = 8.dp,
                        top = 3.dp,
                        end = 8.dp,
                        bottom = 3.dp
                    )
                ) {
                    Icon(
                        painter = painterResource(CsIcons.TrendingDown),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = getFormattedAmountAndCurrency(
                            amount = abs(walletExpenses),
                            currencyName = currencyName
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

private fun getCurrentBalance(startBalance: Double, transactions: List<Transaction>): Double {
    var currentBalance = startBalance
    transactions.forEach {
        currentBalance += it.amount
    }
    return currentBalance
}

@Preview("Wallet Card")
@Composable
fun WalletCardPreview() {
    CsTheme {
        WalletCard(
            wallet = Wallet(
                title = "Wallet 1",
                startBalance = 100.00,
                currency = Currency.USD
            ),
            transactionsWithCategories = emptyList(),
            onWalletClick = { },
            onTransactionCreate = { },
            onEdit = { },
            onDelete = { _, _ ->

            }
        )
    }
}