package ru.resodostudios.cashsense.feature.wallets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.theme.CsTheme
import ru.resodostudios.cashsense.core.model.data.Currency
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.model.data.Wallet
import ru.resodostudios.cashsense.core.ui.DefaultDropdownMenu
import ru.resodostudios.cashsense.core.ui.AmountWithCurrencyText

@Composable
fun WalletCard(
    wallet: Wallet,
    transactions: List<Transaction>,
    onWalletClick: (Long) -> Unit,
    onTransactionCreate: (Long) -> Unit,
    onEdit: (Wallet) -> Unit,
    onDelete: (Wallet, List<Transaction>) -> Unit
) {
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
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                AmountWithCurrencyText(
                    sum = getCurrentBalance(
                        wallet.startBalance,
                        transactions
                    ),
                    currency = wallet.currency
                )
                VerticalDivider(modifier = Modifier.height(24.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = CsIcons.TrendingUp, contentDescription = null)
                    Text(
                        text = wallet.income.toString(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = CsIcons.TrendingDown, contentDescription = null)
                    Text(
                        text = wallet.expenses.toString(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(start = 14.dp, end = 4.dp, bottom = 12.dp)
                .fillMaxWidth()
        ) {
            Button(
                onClick = { onTransactionCreate(wallet.walletId) }
            ) {
                Text(text = stringResource(R.string.add_transaction))
            }
            DefaultDropdownMenu(
                onEdit = { onEdit(wallet) },
                onDelete = { onDelete(wallet, transactions) }
            )
        }
    }
}

private fun getCurrentBalance(startBalance: Float, transactions: List<Transaction>): Float {
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
                startBalance = 100.00f,
                currency = Currency.USD,
                income = 132.0f,
                expenses = 223.43f
            ),
            transactions = emptyList(),
            onWalletClick = { },
            onTransactionCreate = { },
            onEdit = { },
            onDelete = { _, _ ->

            }
        )
    }
}