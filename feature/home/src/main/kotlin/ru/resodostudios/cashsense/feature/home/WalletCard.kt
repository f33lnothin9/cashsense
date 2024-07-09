package ru.resodostudios.cashsense.feature.home

import androidx.annotation.DrawableRes
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
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.component.CsTag
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.theme.CsTheme
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.model.data.Wallet
import ru.resodostudios.cashsense.core.ui.AnimatedAmount
import ru.resodostudios.cashsense.core.ui.formatAmount
import java.math.BigDecimal
import ru.resodostudios.cashsense.feature.transaction.R as transactionR
import ru.resodostudios.cashsense.feature.wallet.dialog.R as walletDialogR

@Composable
fun WalletCard(
    wallet: Wallet,
    transactions: List<Transaction>,
    onWalletClick: (String) -> Unit,
    onTransactionCreate: (String) -> Unit,
    onWalletMenuClick: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    isPrimary: Boolean = false,
) {
    val currentBalance = wallet.initialBalance.plus(transactions.sumOf { it.amount })

    OutlinedCard(
        onClick = { onWalletClick(wallet.id) },
        shape = RoundedCornerShape(24.dp),
        elevation = if (selected) CardDefaults.outlinedCardElevation(defaultElevation = 3.dp) else CardDefaults.outlinedCardElevation(),
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
                text = wallet.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
            )
            AnimatedAmount(
                targetState = currentBalance,
                label = "wallet_balance",
                content = {
                    Text(
                        text = it.formatAmount(wallet.currency),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                },
            )
            TagsSection(
                transactions = transactions,
                currency = wallet.currency,
                isPrimary = isPrimary,
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
                onClick = { onTransactionCreate(wallet.id) },
            ) {
                Text(stringResource(transactionR.string.feature_transaction_add_transaction))
            }
            IconButton(
                onClick = { onWalletMenuClick(wallet.id, currentBalance.formatAmount(wallet.currency)) },
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(CsIcons.MoreVert),
                    contentDescription = stringResource(R.string.feature_home_wallet_menu_icon_description),
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TagsSection(
    transactions: List<Transaction>,
    currency: String,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = false,
) {
    val expenses by remember(transactions) {
        derivedStateOf {
            transactions
                .filter { it.amount < BigDecimal.ZERO }
                .sumOf { it.amount }
                .abs()
        }
    }
    val income by remember(transactions) {
        derivedStateOf {
            transactions
                .filter { it.amount > BigDecimal.ZERO }
                .sumOf { it.amount }
                .abs()
        }
    }

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier,
    ) {
        if (isPrimary) {
            CsTag(
                text = stringResource(walletDialogR.string.feature_wallet_dialog_primary),
                iconId = CsIcons.Star,
            )
        }
        AnimatedVisibility(
            visible = expenses != BigDecimal.ZERO,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
        ) {
            CsAnimatedTag(
                amount = expenses,
                currency = currency,
                color = MaterialTheme.colorScheme.errorContainer,
                iconId = CsIcons.TrendingDown,
            )
        }
        AnimatedVisibility(
            visible = income != BigDecimal.ZERO,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
        ) {
            CsAnimatedTag(
                amount = income,
                currency = currency,
                color = MaterialTheme.colorScheme.tertiaryContainer,
                iconId = CsIcons.TrendingUp,
            )
        }
    }
}

@Composable
private fun CsAnimatedTag(
    amount: BigDecimal,
    currency: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.secondaryContainer,
    shape: Shape = RoundedCornerShape(16.dp),
    @DrawableRes
    iconId: Int? = null,
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
            if (iconId != null) {
                Icon(
                    imageVector = ImageVector.vectorResource(iconId),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
            }
            AnimatedAmount(
                targetState = amount,
                label = "animated_tag",
                content = {
                    Text(
                        text = it.formatAmount(currency),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            )
        }
    }
}

@PreviewLightDark
@Composable
fun WalletCardPreview() {
    CsTheme {
        Surface {
            WalletCard(
                wallet = Wallet(
                    id = "",
                    title = "Debit",
                    initialBalance = BigDecimal(1499.99),
                    currency = "USD",
                ),
                transactions = emptyList(),
                onWalletClick = {},
                onTransactionCreate = {},
                onWalletMenuClick = { _, _ ->},
                modifier = Modifier.padding(16.dp),
                isPrimary = true,
            )
        }
    }
}