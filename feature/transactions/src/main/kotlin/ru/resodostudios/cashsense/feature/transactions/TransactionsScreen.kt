package ru.resodostudios.cashsense.feature.transactions

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.ui.EmptyState
import ru.resodostudios.cashsense.core.ui.LoadingState
import ru.resodostudios.cashsense.core.ui.TimeZoneBroadcastReceiver
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@Composable
internal fun TransactionsRoute(
    viewModel: TransactionsViewModel = hiltViewModel()
) {
    val transactionsState by viewModel.transactionsUiState.collectAsStateWithLifecycle(initialValue = TransactionsUiState.Loading)
    TransactionsScreen(
        transactionsState = transactionsState,
        onDelete = viewModel::deleteTransaction
    )
}

@Composable
internal fun TransactionsScreen(
    transactionsState: TransactionsUiState,
    onDelete: (Transaction) -> Unit
) {
    when (transactionsState) {
        TransactionsUiState.Loading -> LoadingState()
        is TransactionsUiState.Success -> if (transactionsState.transactions.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(300.dp),
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    transactions(
                        transactions = transactionsState.transactions,
                        onDelete = onDelete
                    )
                }
            }
        } else {
            EmptyState(messageId = R.string.transactions_empty, animationId = R.raw.anim_empty)
        }
    }
}

private fun LazyGridScope.transactions(
    transactions: List<Transaction>,
    onDelete: (Transaction) -> Unit
) {
    items(transactions) { transaction ->
        ListItem(
            headlineContent = { Text(text = "${transaction.value} ₽") },
            trailingContent = {
                IconButton(onClick = { onDelete(transaction) }) {
                    Icon(
                        imageVector = CsIcons.Delete,
                        contentDescription = null
                    )
                }
            },
            supportingContent = { Text(text = dateFormatted(transaction.date)) }
        )
    }
}

@Composable
fun dateFormatted(date: Instant): String {
    var zoneId by remember { mutableStateOf(ZoneId.systemDefault()) }

    val context = LocalContext.current

    DisposableEffect(context) {
        val receiver = TimeZoneBroadcastReceiver(
            onTimeZoneChanged = { zoneId = ZoneId.systemDefault() },
        )
        receiver.register(context)
        onDispose {
            receiver.unregister(context)
        }
    }

    return DateTimeFormatter
        .ofLocalizedDateTime(FormatStyle.MEDIUM)
        .withLocale(Locale.getDefault())
        .withZone(zoneId)
        .format(date.toJavaInstant())

}