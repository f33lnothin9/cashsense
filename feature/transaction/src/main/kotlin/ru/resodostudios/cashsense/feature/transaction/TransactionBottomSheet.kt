package ru.resodostudios.cashsense.feature.transaction

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.designsystem.component.CsListItem
import ru.resodostudios.cashsense.core.designsystem.component.CsModalBottomSheet
import ru.resodostudios.cashsense.core.designsystem.component.CsTag
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.model.data.StatusType.COMPLETED
import ru.resodostudios.cashsense.core.ui.FormatDateType.DATE_TIME
import ru.resodostudios.cashsense.core.ui.LoadingState
import ru.resodostudios.cashsense.core.ui.StoredIcon
import ru.resodostudios.cashsense.core.ui.formatAmount
import ru.resodostudios.cashsense.core.ui.formatDate
import ru.resodostudios.cashsense.feature.transaction.TransactionDialogEvent.Repeat
import ru.resodostudios.cashsense.feature.transaction.TransactionDialogEvent.Save
import ru.resodostudios.cashsense.feature.transaction.TransactionDialogEvent.UpdateTransactionIgnoring
import ru.resodostudios.cashsense.feature.transaction.TransactionType.EXPENSE
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
fun TransactionBottomSheet(
    onDismiss: () -> Unit,
    onRepeatClick: (String) -> Unit,
    onEdit: (String) -> Unit,
    onDelete: (String) -> Unit,
    viewModel: TransactionDialogViewModel = hiltViewModel(),
) {
    val transactionDialogState by viewModel.transactionDialogUiState.collectAsStateWithLifecycle()

    TransactionBottomSheet(
        transactionDialogState = transactionDialogState,
        onTransactionEvent = viewModel::onTransactionEvent,
        onDismiss = onDismiss,
        onRepeatClick = onRepeatClick,
        onEdit = onEdit,
        onDelete = onDelete,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TransactionBottomSheet(
    transactionDialogState: TransactionDialogUiState,
    onTransactionEvent: (TransactionDialogEvent) -> Unit,
    onDismiss: () -> Unit,
    onRepeatClick: (String) -> Unit,
    onEdit: (String) -> Unit,
    onDelete: (String) -> Unit,
) {
    CsModalBottomSheet(onDismiss) {
        if (transactionDialogState.isLoading) {
            LoadingState(
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth(),
            )
        } else {
            val (leadingIconRes, supportingText) = if (transactionDialogState.isTransfer) {
                CsIcons.SendMoney to stringResource(localesR.string.transfers)
            } else {
                val iconRes = StoredIcon.asRes(
                    transactionDialogState.category?.iconId
                        ?: StoredIcon.TRANSACTION.storedId
                )
                val categoryTitle = transactionDialogState.category?.title
                    ?: stringResource(localesR.string.uncategorized)
                iconRes to categoryTitle
            }
            Column {
                CsListItem(
                    headlineContent = {
                        Text(
                            text = transactionDialogState.amount
                                .toBigDecimal()
                                .run { if (transactionDialogState.transactionType == EXPENSE) negate() else abs() }
                                .formatAmount(transactionDialogState.currency, true),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    supportingContent = {
                        Text(
                            text = supportingText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    leadingContent = {
                        Icon(
                            imageVector = ImageVector.vectorResource(leadingIconRes),
                            contentDescription = null,
                        )
                    }
                )
                FlowRow(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    CsTag(
                        text = transactionDialogState.date.formatDate(DATE_TIME),
                        iconId = CsIcons.Calendar,
                    )
                    val transactionStatusTag = if (transactionDialogState.status == COMPLETED) {
                        Triple(
                            stringResource(localesR.string.completed),
                            CsIcons.CheckCircle,
                            MaterialTheme.colorScheme.secondaryContainer,
                        )
                    } else {
                        Triple(
                            stringResource(localesR.string.pending),
                            CsIcons.Pending,
                            MaterialTheme.colorScheme.tertiaryContainer,
                        )
                    }
                    CsTag(
                        text = transactionStatusTag.first,
                        iconId = transactionStatusTag.second,
                        color = transactionStatusTag.third,
                    )
                }
                if (transactionDialogState.description.isNotBlank()) {
                    Text(
                        text = stringResource(localesR.string.description),
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(
                            top = 16.dp,
                            bottom = 8.dp,
                            start = 16.dp,
                            end = 16.dp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    ExpandableText(
                        text = transactionDialogState.description,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                    )
                }
                HorizontalDivider(Modifier.padding(16.dp))
                if (!transactionDialogState.isTransfer) {
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
                                    onTransactionEvent(UpdateTransactionIgnoring(it))
                                    onTransactionEvent(Save)
                                },
                            )
                        },
                    )
                    CsListItem(
                        headlineContent = { Text(stringResource(localesR.string.repeat)) },
                        leadingContent = {
                            Icon(
                                imageVector = ImageVector.vectorResource(CsIcons.Redo),
                                contentDescription = null,
                            )
                        },
                        onClick = {
                            onTransactionEvent(Repeat)
                            onDismiss()
                            onRepeatClick(transactionDialogState.transactionId)
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
                            onEdit(transactionDialogState.transactionId)
                        },
                    )
                }
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

@Composable
private fun ExpandableText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    collapsedMaxLine: Int = 3,
    showMoreText: String = stringResource(localesR.string.show_more),
    showMoreStyle: SpanStyle = SpanStyle(
        fontWeight = FontWeight.W500,
        fontStyle = FontStyle.Italic,
        fontSize = 14.sp,
        textDecoration = TextDecoration.Underline,
    ),
    showLessText: String = stringResource(localesR.string.show_less),
    showLessStyle: SpanStyle = showMoreStyle,
) {
    var expanded by remember { mutableStateOf(false) }
    var clickable by remember { mutableStateOf(false) }
    var lastCharIndex by remember { mutableIntStateOf(0) }

    Box(
        modifier = modifier
            .clickable(
                enabled = clickable,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { expanded = !expanded },
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(),
            text = buildAnnotatedString {
                if (clickable) {
                    if (expanded) {
                        append("$text ")
                        withStyle(style = showLessStyle) { append(showLessText) }
                    } else {
                        val adjustText = text.substring(startIndex = 0, endIndex = lastCharIndex)
                            .dropLast(showMoreText.length + 4)
                            .dropLastWhile { Character.isWhitespace(it) || it == '.' }
                        append("$adjustText... ")
                        withStyle(style = showMoreStyle) { append(showMoreText) }
                    }
                } else {
                    append(text)
                }
            },
            maxLines = if (expanded) Int.MAX_VALUE else collapsedMaxLine,
            onTextLayout = { textLayoutResult ->
                if (!expanded && textLayoutResult.hasVisualOverflow) {
                    clickable = true
                    lastCharIndex = textLayoutResult.getLineEnd(collapsedMaxLine - 1)
                }
            },
            style = style,
        )
    }
}