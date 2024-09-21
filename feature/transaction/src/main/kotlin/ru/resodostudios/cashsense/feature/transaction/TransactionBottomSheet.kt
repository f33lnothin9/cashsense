package ru.resodostudios.cashsense.feature.transaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.designsystem.component.CsListItem
import ru.resodostudios.cashsense.core.designsystem.component.CsModalBottomSheet
import ru.resodostudios.cashsense.core.designsystem.component.CsTag
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.model.data.StatusType.COMPLETED
import ru.resodostudios.cashsense.core.model.data.StatusType.PENDING
import ru.resodostudios.cashsense.core.ui.FormatDateType.DATE_TIME
import ru.resodostudios.cashsense.core.ui.LoadingState
import ru.resodostudios.cashsense.core.ui.StoredIcon
import ru.resodostudios.cashsense.core.ui.formatAmount
import ru.resodostudios.cashsense.core.ui.formatDate
import ru.resodostudios.cashsense.feature.transaction.TransactionDialogEvent.Save
import ru.resodostudios.cashsense.feature.transaction.TransactionDialogEvent.UpdateIgnoring
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
fun TransactionBottomSheet(
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: (String) -> Unit,
    viewModel: TransactionDialogViewModel = hiltViewModel(),
) {
    val transactionDialogState by viewModel.transactionDialogUiState.collectAsStateWithLifecycle()

    TransactionBottomSheet(
        transactionDialogState = transactionDialogState,
        onTransactionEvent = viewModel::onTransactionEvent,
        onDismiss = onDismiss,
        onEdit = onEdit,
        onDelete = onDelete,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TransactionBottomSheet(
    transactionDialogState: TransactionDialogUiState,
    onTransactionEvent: (TransactionDialogEvent) -> Unit,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: (String) -> Unit,
) {
    CsModalBottomSheet(onDismiss) {
        if (transactionDialogState.isLoading) {
            LoadingState(
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth()
            )
        } else {
            Column {
                CsListItem(
                    headlineContent = {
                        Text(
                            text = transactionDialogState.amount
                                .toBigDecimal()
                                .formatAmount(transactionDialogState.currency, true)
                        )
                    },
                    supportingContent = {
                        if (transactionDialogState.description.isNotEmpty()) {
                            Text(transactionDialogState.description)
                        }
                    },
                )
                FlowRow(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (transactionDialogState.category != null) {
                        CsTag(
                            text = transactionDialogState.category.title.toString(),
                            iconId = StoredIcon.asRes(transactionDialogState.category.iconId ?: 0),
                        )
                    }

                    CsTag(
                        text = transactionDialogState.date.formatDate(DATE_TIME),
                        iconId = CsIcons.Calendar,
                    )

                    val statusTag: Pair<String, Int> = when (transactionDialogState.status) {
                        COMPLETED -> stringResource(localesR.string.completed) to CsIcons.CheckCircle
                        PENDING -> stringResource(localesR.string.pending) to CsIcons.Pending
                    }
                    CsTag(
                        text = statusTag.first,
                        iconId = statusTag.second,
                        color = if (transactionDialogState.status == PENDING) {
                            MaterialTheme.colorScheme.tertiaryContainer
                        } else {
                            MaterialTheme.colorScheme.secondaryContainer
                        }
                    )
                }
                HorizontalDivider(Modifier.padding(16.dp))
                CsListItem(
                    headlineContent = { Text(stringResource(localesR.string.transaction_ignore)) },
                    leadingContent = {
                        Icon(
                            imageVector = ImageVector.vectorResource(CsIcons.Block),
                            contentDescription = null,
                        )
                    },
                    trailingContent = {
                        Switch(
                            checked = transactionDialogState.ignored,
                            onCheckedChange = {
                                onTransactionEvent(UpdateIgnoring(it))
                                onTransactionEvent(Save)
                            },
                        )
                    },
                )
                CsListItem(
                    headlineContent = { Text(stringResource(localesR.string.edit)) },
                    leadingContent = {
                        Icon(
                            imageVector = ImageVector.vectorResource(CsIcons.Edit),
                            contentDescription = null,
                        )
                    },
                    onClick = {
                        onDismiss()
                        onEdit()
                    },
                )
                CsListItem(
                    headlineContent = { Text(stringResource(localesR.string.delete)) },
                    leadingContent = {
                        Icon(
                            imageVector = ImageVector.vectorResource(CsIcons.Delete),
                            contentDescription = null,
                        )
                    },
                    onClick = {
                        onDismiss()
                        onDelete(transactionDialogState.transactionId)
                    },
                )
            }
        }
    }
}