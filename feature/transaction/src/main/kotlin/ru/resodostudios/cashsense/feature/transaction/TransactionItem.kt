package ru.resodostudios.cashsense.feature.transaction

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.component.CsListItem
import ru.resodostudios.cashsense.core.designsystem.theme.CsTheme
import ru.resodostudios.cashsense.core.model.data.StatusType
import ru.resodostudios.cashsense.core.model.data.StatusType.PENDING
import ru.resodostudios.cashsense.core.ui.StoredIcon
import ru.resodostudios.cashsense.core.ui.formatAmount
import java.math.BigDecimal

@Composable
fun TransactionItem(
    amount: String,
    icon: Int,
    categoryTitle: String,
    transactionStatus: StatusType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CsListItem(
        headlineContent = {
            Text(
                text = amount,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        supportingContent = {
            Text(
                text = categoryTitle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        trailingContent = {
            AnimatedVisibility(
                visible = transactionStatus == PENDING,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut(),
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Text(
                        text = stringResource(R.string.feature_transaction_status_pending),
                        modifier = Modifier.padding(
                            start = 6.dp,
                            top = 3.dp,
                            end = 6.dp,
                            bottom = 3.dp,
                        ),
                    )
                }
            }
        },
        leadingContent = {
            Icon(
                imageVector = ImageVector.vectorResource(StoredIcon.asRes(icon)),
                contentDescription = null,
            )
        },
        modifier = modifier,
        onClick = onClick,
    )
}

@PreviewLightDark
@Composable
fun TransactionItemPreview() {
    CsTheme {
        Surface {
            TransactionItem(
                amount = BigDecimal(-199.99).formatAmount(
                    currencyCode = "USD",
                    withPlus = true,
                ),
                icon = StoredIcon.FASTFOOD.storedId,
                categoryTitle = "Fastfood",
                transactionStatus = PENDING,
                onClick = {},
            )
        }
    }
}