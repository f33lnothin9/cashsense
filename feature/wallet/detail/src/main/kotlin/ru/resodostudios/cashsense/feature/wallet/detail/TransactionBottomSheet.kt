package ru.resodostudios.cashsense.feature.wallet.detail

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
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
import ru.resodostudios.cashsense.core.designsystem.component.CsListItem
import ru.resodostudios.cashsense.core.designsystem.component.CsModalBottomSheet
import ru.resodostudios.cashsense.core.designsystem.component.CsTag
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.model.data.StatusType.COMPLETED
import ru.resodostudios.cashsense.core.model.data.TransactionWithCategory
import ru.resodostudios.cashsense.core.ui.util.FormatDateType.DATE_TIME
import ru.resodostudios.cashsense.core.ui.StoredIcon
import ru.resodostudios.cashsense.core.ui.util.formatAmount
import ru.resodostudios.cashsense.core.ui.util.formatDate
import java.math.BigDecimal
import java.util.Currency
import ru.resodostudios.cashsense.core.locales.R as localesR

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun TransactionBottomSheet(
    transactionCategory: TransactionWithCategory,
    currency: Currency,
    onDismiss: () -> Unit,
    onIgnoreClick: (Boolean) -> Unit,
    onRepeatClick: (String) -> Unit,
    onEdit: (String) -> Unit,
    onDelete: () -> Unit,
) {
    val transaction = transactionCategory.transaction
    CsModalBottomSheet(onDismiss) {
        val (leadingIconRes, supportingText) = if (transaction.transferId != null) {
            CsIcons.SendMoney to stringResource(localesR.string.transfers)
        } else {
            val iconRes = StoredIcon.asRes(
                transactionCategory.category?.iconId
                    ?: StoredIcon.TRANSACTION.storedId
            )
            val categoryTitle = transactionCategory.category?.title
                ?: stringResource(localesR.string.uncategorized)
            iconRes to categoryTitle
        }
        Column {
            CsListItem(
                headlineContent = {
                    Text(
                        text = transaction.amount
                            .run { if (this < BigDecimal.ZERO) negate() else abs() }
                            .formatAmount(currency, true),
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
                    text = transaction.timestamp.formatDate(DATE_TIME),
                    iconId = CsIcons.Calendar,
                )
                val transactionStatusTag = if (transaction.status == COMPLETED) {
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
            if (transaction.description != null && transaction.description!!.isNotBlank()) {
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
                    text = transaction.description.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                )
            }
            HorizontalDivider(Modifier.padding(16.dp))
            if (transaction.transferId == null) {
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
                            checked = transaction.ignored,
                            onCheckedChange = onIgnoreClick,
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
                        onDismiss()
                        onRepeatClick(transaction.id)
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
                        onEdit(transaction.id)
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
                    onDelete()
                },
            )
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