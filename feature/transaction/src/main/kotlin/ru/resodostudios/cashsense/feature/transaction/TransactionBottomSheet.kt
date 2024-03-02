package ru.resodostudios.cashsense.feature.transaction

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import ru.resodostudios.cashsense.core.ui.R
import ru.resodostudios.cashsense.core.ui.StoredIcon
import ru.resodostudios.cashsense.core.ui.formatAmountWithCurrency

@Composable
fun TransactionBottomSheet(
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    viewModel: TransactionViewModel = hiltViewModel(),
) {
    val transactionState by viewModel.transactionUiState.collectAsStateWithLifecycle()

    TransactionBottomSheet(
        transactionState = transactionState,
        onTransactionEvent = viewModel::onTransactionEvent,
        onDismiss = onDismiss,
        onEdit = onEdit,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TransactionBottomSheet(
    transactionState: TransactionUiState,
    onTransactionEvent: (TransactionEvent) -> Unit,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
) {
    val formattedAmount = if (transactionState.amount.isNotEmpty()) {
        transactionState.amount
            .toBigDecimal()
            .formatAmountWithCurrency(transactionState.currency, true)
    } else { "" }

    CsModalBottomSheet(
        onDismiss = onDismiss
    ) {
        ListItem(
            headlineContent = { Text(formattedAmount) },
            leadingContent = {
                Icon(
                    imageVector = ImageVector.vectorResource(CsIcons.Transaction),
                    contentDescription = null,
                )
            },
        )
        FlowRow(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp)
        ) {
            if (transactionState.category != null) {
                CsTag(
                    text = transactionState.category.title.toString(),
                    iconId = StoredIcon.asRes(transactionState.category.iconId ?: 0),
                )
            }
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
                onTransactionEvent(TransactionEvent.Delete)
            },
        )
    }
}