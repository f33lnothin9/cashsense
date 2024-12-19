package ru.resodostudios.cashsense.feature.wallet.detail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import ru.resodostudios.cashsense.core.designsystem.component.CsTag
import ru.resodostudios.cashsense.core.ui.EmptyState
import ru.resodostudios.cashsense.core.ui.util.formatDate
import java.time.temporal.ChronoUnit
import ru.resodostudios.cashsense.core.locales.R as localesR

@OptIn(ExperimentalFoundationApi::class)
internal fun LazyListScope.transactions(
    walletState: WalletUiState,
    onTransactionClick: (String) -> Unit,
) {
    when (walletState) {
        WalletUiState.Loading -> Unit
        is WalletUiState.Success -> {
            if (walletState.transactionsCategories.isNotEmpty()) {
                val transactionsByDay = walletState.transactionsCategories
                    .groupBy {
                        it.transaction.timestamp
                            .toJavaInstant()
                            .truncatedTo(ChronoUnit.DAYS)
                            .toKotlinInstant()
                    }
                    .toSortedMap(compareByDescending { it })

                transactionsByDay.forEach { transactionGroup ->
                    stickyHeader {
                        CsTag(
                            text = transactionGroup.key.formatDate(),
                            modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                        )
                    }
                    item { Spacer(Modifier.height(16.dp)) }
                    items(
                        items = transactionGroup.value,
                        key = { it.transaction.id },
                        contentType = { "transactionCategory" },
                    ) { transactionCategory ->
                        TransactionItem(
                            transactionCategory = transactionCategory,
                            currency = walletState.userWallet.currency,
                            onClick = onTransactionClick,
                            modifier = Modifier.animateItem(),
                        )
                    }
                }
            } else {
                item {
                    EmptyState(
                        messageRes = localesR.string.transactions_empty,
                        animationRes = R.raw.anim_transactions_empty,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                    )
                }
            }
        }
    }
}