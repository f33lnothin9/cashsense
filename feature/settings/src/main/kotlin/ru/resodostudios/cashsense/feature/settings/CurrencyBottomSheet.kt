package ru.resodostudios.cashsense.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.resodostudios.cashsense.core.locales.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CurrencyBottomSheet(
    currencyCode: String,
    onCurrencyClick: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    ModalBottomSheet(
        onDismissRequest = {
            onDismiss()
        },
        sheetState = sheetState,
    ) {
        var searchText by remember { mutableStateOf("") }
        Column(
            modifier = Modifier
                .padding(horizontal = 14.dp) // Ensure the column has padding
                .fillMaxWidth()
        ){
            OutlinedTextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                },
                label = { Text(stringResource(R.string.search_currency)) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }

        CurrencyList(
            currencyCode,
            searchText
        ) {
            onCurrencyClick(it)
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible) {
                    onDismiss()
                }
            }
        }
    }
}
