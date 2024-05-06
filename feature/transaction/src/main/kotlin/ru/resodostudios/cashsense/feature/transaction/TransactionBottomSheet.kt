package ru.resodostudios.cashsense.feature.transaction

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
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
import ru.resodostudios.cashsense.core.designsystem.component.CsModalBottomSheet
import ru.resodostudios.cashsense.core.designsystem.component.CsTag
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.ui.FormatDateType.DATE_TIME
import ru.resodostudios.cashsense.core.ui.LoadingState
import ru.resodostudios.cashsense.core.ui.R
import ru.resodostudios.cashsense.core.ui.StoredIcon
import ru.resodostudios.cashsense.core.ui.formatAmount
import ru.resodostudios.cashsense.core.ui.formatDate

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
        onDismiss = onDismiss,
        onEdit = onEdit,
        onDelete = onDelete,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TransactionBottomSheet(
    transactionDialogState: TransactionDialogUiState,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: (String) -> Unit,
) {
    CsModalBottomSheet(onDismiss = onDismiss) {
        AnimatedVisibility(transactionDialogState.isLoading) {
            LoadingState(
                Modifier
                    .height(100.dp)
                    .fillMaxWidth()
            )
        }
        AnimatedVisibility(!transactionDialogState.isLoading) {
            Column {
                ListItem(
                    headlineContent = {
                        Text(
                            transactionDialogState.amount
                                .toBigDecimal()
                                .formatAmount(transactionDialogState.currency, true)
                        )
                    },
                    supportingContent = {
                        if (transactionDialogState.description.isNotEmpty()) {
                            Text(text = transactionDialogState.description)
                        }
                    }
                )
                FlowRow(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                }
                HorizontalDivider(Modifier.padding(16.dp))
                ListItem(
                    headlineContent = { Text(stringResource(R.string.edit)) },
                    leadingContent = {
                        Icon(
                            imageVector = ImageVector.vectorResource(CsIcons.Edit),
                            contentDescription = null,
                        )
                    },
                    modifier = Modifier.clickable {
                        onDismiss()
                        onEdit()
                    },
                )
                ListItem(
                    headlineContent = { Text(stringResource(R.string.delete)) },
                    leadingContent = {
                        Icon(
                            imageVector = ImageVector.vectorResource(CsIcons.Delete),
                            contentDescription = null,
                        )
                    },
                    modifier = Modifier.clickable {
                        onDismiss()
                        onDelete(transactionDialogState.transactionId)
                    },
                )
            }
        }
    }
}