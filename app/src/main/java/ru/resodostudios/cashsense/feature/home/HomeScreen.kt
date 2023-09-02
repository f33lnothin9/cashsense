package ru.resodostudios.cashsense.feature.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.TrendingDown
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.R
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.ui.LoadingState
import ru.resodostudios.cashsense.feature.transactions.TransactionsUiState
import ru.resodostudios.cashsense.feature.transactions.TransactionsViewModel
import ru.resodostudios.cashsense.feature.transactions.formatLocalDate

@Composable
internal fun HomeRoute(
    viewModel: TransactionsViewModel = hiltViewModel()
) {
    val transactionsState by viewModel.transactionsUiState.collectAsStateWithLifecycle(initialValue = TransactionsUiState.Loading)
    HomeScreen(
        transactionsState = transactionsState
    )
}

@Composable
internal fun HomeScreen(
    transactionsState: TransactionsUiState
) {

    var totalBalance by rememberSaveable { mutableIntStateOf(0) }
    var expenses by rememberSaveable { mutableIntStateOf(0) }
    var income by rememberSaveable { mutableIntStateOf(0) }

    when (transactionsState) {
        TransactionsUiState.Loading -> LoadingState()
        is TransactionsUiState.Success -> {
            totalBalance = transactionsState.transactions.fold(0) { acc, transaction ->
                acc + transaction.value
            }
            expenses =
                transactionsState.transactions.filter { it.value < 0 }.fold(0) { acc, transaction ->
                    acc + transaction.value
                }
            income =
                transactionsState.transactions.filter { it.value > 0 }.fold(0) { acc, transaction ->
                    acc + transaction.value
                }
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    ListItem(
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Outlined.AccountBalanceWallet,
                                contentDescription = null
                            )
                        },
                        headlineContent = { Text(text = "$totalBalance ₽") },
                        supportingContent = { Text(text = "Итоговый баланс") },
                        trailingContent = {
                            Surface(
                                modifier = Modifier.clip(RoundedCornerShape(12.dp)),
                                color = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Text(
                                    text = "+1 500 ₽",
                                    modifier = Modifier
                                        .padding(
                                            start = 8.dp,
                                            top = 4.dp,
                                            end = 8.dp,
                                            bottom = 4.dp
                                        ),
                                    style = MaterialTheme.typography.labelLarge,
                                    maxLines = 1,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        },
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                item {
                    ListItem(
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Outlined.TrendingUp,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        headlineContent = { Text(text = "$income ₽") },
                        supportingContent = { Text(text = "Доходы") },
                        trailingContent = {
                            Icon(
                                imageVector = Icons.Outlined.ChevronRight,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {

                            }
                    )
                }

                item {
                    ListItem(
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Outlined.TrendingDown,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        headlineContent = { Text(text = "$expenses ₽") },
                        supportingContent = { Text(text = "Расходы") },
                        trailingContent = {
                            Icon(
                                imageVector = Icons.Outlined.ChevronRight,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {

                            }
                    )
                }

                item {
                    Text(
                        text = stringResource(id = R.string.recent_transactions),
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                items(transactionsState.transactions.take(3)) { transaction ->
                    ListItem(
                        leadingContent = {
                            Icon(
                                imageVector = CsIcons.Transaction,
                                contentDescription = null
                            )
                        },
                        supportingContent = { Text(text = transaction.category.toString()) },
                        headlineContent = { Text(text = "${transaction.value} ₽") },
                        trailingContent = { Text(text = formatLocalDate(transaction.date)) }
                    )
                }
            }
        }
    }
}