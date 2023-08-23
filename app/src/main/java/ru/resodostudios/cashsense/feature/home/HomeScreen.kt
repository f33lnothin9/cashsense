package ru.resodostudios.cashsense.feature.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.LunchDining
import androidx.compose.material.icons.outlined.TrendingDown
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@ExperimentalMaterial3Api
@Composable
internal fun HomeScreen() {

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
                headlineContent = { Text(text = "1 500 ₽") },
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
                headlineContent = { Text(text = "1 000 ₽") },
                supportingContent = { Text(text = "Доходы") },
                trailingContent = {
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowRight,
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
                headlineContent = { Text(text = "-2 500 ₽") },
                supportingContent = { Text(text = "Расходы") },
                trailingContent = {
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowRight,
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
                text = "Недавние транзакции",
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge
            )
        }

        item {
            ListItem(
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.LunchDining,
                        contentDescription = null
                    )
                },
                supportingContent = { Text(text = "Фастфуд") },
                headlineContent = { Text(text = "-500 ₽") },
                trailingContent = { Text(text = "11 августа") }
            )
        }

        item {
            ListItem(
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.LunchDining,
                        contentDescription = null
                    )
                },
                supportingContent = { Text(text = "Фастфуд") },
                headlineContent = { Text(text = "-500 ₽") },
                trailingContent = { Text(text = "11 августа") }
            )
        }
    }
}