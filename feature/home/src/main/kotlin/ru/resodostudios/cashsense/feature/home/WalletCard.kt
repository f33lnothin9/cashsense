package ru.resodostudios.cashsense.feature.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.component.CsTag
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.filled.Star
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.TrendingDown
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.TrendingUp
import ru.resodostudios.cashsense.core.designsystem.theme.CsTheme
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.model.data.UserWallet
import ru.resodostudios.cashsense.core.ui.component.AnimatedAmount
import ru.resodostudios.cashsense.core.ui.component.WalletDropdownMenu
import ru.resodostudios.cashsense.core.ui.util.formatAmount
import ru.resodostudios.cashsense.core.ui.util.getZonedDateTime
import ru.resodostudios.cashsense.core.ui.util.isInCurrentMonthAndYear
import ru.resodostudios.cashsense.core.util.getUsdCurrency
import java.math.BigDecimal
import java.util.Currency
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
fun WalletCard(
    userWallet: UserWallet,
    transactions: List<Transaction>,
    onWalletClick: (String) -> Unit,
    onNewTransactionClick: (String) -> Unit,
    onTransferClick: (String) -> Unit,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
) {
    val border = if (selected) {
        CardDefaults.outlinedCardBorder().copy(
            width = 2.dp,
            brush = SolidColor(MaterialTheme.colorScheme.outlineVariant),
        )
    } else {
        CardDefaults.outlinedCardBorder()
    }

    OutlinedCard(
        onClick = { onWalletClick(userWallet.id) },
        shape = RoundedCornerShape(24.dp),
        border = border,
        modifier = modifier,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth(),
        ) {
            Text(
                text = userWallet.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
            )
            AnimatedAmount(
                targetState = userWallet.currentBalance,
                label = "wallet_balance",
            ) {
                Text(
                    text = it.formatAmount(userWallet.currency),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            TagsSection(
                transactions = transactions,
                currency = userWallet.currency,
                isPrimary = userWallet.isPrimary,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(start = 16.dp, end = 8.dp, bottom = 12.dp, top = 12.dp)
                .fillMaxWidth(),
        ) {
            Button(
                onClick = { onNewTransactionClick(userWallet.id) },
            ) {
                Text(stringResource(localesR.string.add_transaction))
            }
            WalletDropdownMenu(
                onTransferClick = { onTransferClick(userWallet.id) },
                onEditClick = { onEditClick(userWallet.id) },
                onDeleteClick = { onDeleteClick(userWallet.id) },
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TagsSection(
    transactions: List<Transaction>,
    currency: Currency,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = false,
) {
    val currentMonthTransactions by remember(transactions) {
        derivedStateOf {
            transactions.filter {
                it.timestamp.getZonedDateTime().isInCurrentMonthAndYear() && !it.ignored
            }
        }
    }
    val expenses by remember(currentMonthTransactions) {
        derivedStateOf {
            currentMonthTransactions
                .filter { it.amount.signum() == -1 }
                .sumOf { it.amount }
                .abs()
        }
    }
    val income by remember(currentMonthTransactions) {
        derivedStateOf {
            currentMonthTransactions
                .filter { it.amount.signum() == 1 }
                .sumOf { it.amount }
        }
    }

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier,
    ) {
        AnimatedVisibility(
            visible = isPrimary,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
        ) {
            CsTag(
                text = stringResource(localesR.string.primary),
                icon = CsIcons.Filled.Star,
            )
        }
        AnimatedVisibility(
            visible = expenses.signum() == 1,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
        ) {
            CsAnimatedTag(
                amount = expenses,
                currency = currency,
                color = MaterialTheme.colorScheme.errorContainer,
                icon = CsIcons.Outlined.TrendingDown,
            )
        }
        AnimatedVisibility(
            visible = income.signum() == 1,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
        ) {
            CsAnimatedTag(
                amount = income,
                currency = currency,
                color = MaterialTheme.colorScheme.tertiaryContainer,
                icon = CsIcons.Outlined.TrendingUp,
            )
        }
    }
}

@Composable
private fun CsAnimatedTag(
    amount: BigDecimal,
    currency: Currency,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.secondaryContainer,
    shape: Shape = RoundedCornerShape(16.dp),
    icon: ImageVector? = null,
) {
    Surface(
        color = color,
        shape = shape,
        modifier = modifier,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(
                start = 8.dp,
                top = 4.dp,
                end = 8.dp,
                bottom = 4.dp,
            )
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
            }
            AnimatedAmount(
                targetState = amount,
                label = "animated_tag",
            ) {
                Text(
                    text = it.formatAmount(currency),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
fun WalletCardPreview() {
    CsTheme {
        Surface {
            WalletCard(
                userWallet = UserWallet(
                    id = "",
                    title = "Debit",
                    initialBalance = BigDecimal(1499.99),
                    currency = getUsdCurrency(),
                    currentBalance = BigDecimal(2499.99),
                    isPrimary = true,
                ),
                transactions = emptyList(),
                onWalletClick = {},
                onNewTransactionClick = {},
                onTransferClick = { _ -> },
                onEditClick = { _ -> },
                onDeleteClick = { _ -> },
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}