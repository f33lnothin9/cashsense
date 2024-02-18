package ru.resodostudios.cashsense.feature.wallet

import android.content.res.Configuration
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        onClick = { onWalletClick(wallet.id) },
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
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
                text = getCurrentBalance(wallet.initialBalance, transactions).formatAmountWithCurrency(wallet.currency),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge
            )
            FinanceIndicators(
                transactions = transactions,
                currency = wallet.currency
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
                onClick = { onTransactionCreate(wallet.id) }
            ) {
                Text(text = stringResource(transactionR.string.feature_transaction_add_transaction))
            }
            EditAndDeleteDropdownMenu(
                onEdit = { onEdit(wallet.id) },
                onDelete = { onDelete(wallet.id) }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FinanceIndicators(
    transactions: List<Transaction>,
    currency: String,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        val positiveTransactions = transactions.filter { it.amount > 0.toBigDecimal() }
        val walletIncome = positiveTransactions.sumOf { it.amount }
        if (walletIncome != 0.toBigDecimal()) {
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
                        imageVector = ImageVector.vectorResource(CsIcons.TrendingUp),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = walletIncome
                            .abs()
                            .formatAmountWithCurrency(currency),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }

        val negativeTransactions = transactions.filter { it.amount < 0.toBigDecimal() }
        val walletExpenses = negativeTransactions.sumOf { it.amount }
        if (walletExpenses != 0.toBigDecimal()) {
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
                        imageVector = ImageVector.vectorResource(CsIcons.TrendingDown),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = walletExpenses
                            .abs()
                            .formatAmountWithCurrency(currency),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

private fun getCurrentBalance(startBalance: BigDecimal, transactions: List<Transaction>): BigDecimal {
    var currentBalance = startBalance
    transactions.forEach {
        currentBalance += it.amount
    }
    return currentBalance
}

@Preview(
    name = "Wallet Card",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
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
                onWalletClick = { },
                onTransactionCreate = { },
                onEdit = { },
                onDelete = { },
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}