package ru.resodostudios.cashsense.core.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import ru.resodostudios.cashsense.core.locales.R as localesR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerTextField(
    value: String,
    @StringRes labelTextId: Int,
    @DrawableRes iconId: Int,
    onDateClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    initialSelectedDateMillis: Long? = null,
    isAllDatesEnabled: Boolean = true,
) {
    var openDialog by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = {},
        readOnly = true,
        label = { Text(stringResource(labelTextId)) },
        trailingIcon = {
            IconButton(onClick = { openDialog = true }) {
                Icon(
                    imageVector = ImageVector.vectorResource(iconId),
                    contentDescription = null,
                )
            }
        },
        maxLines = 1,
        modifier = modifier,
    )
    if (openDialog) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialSelectedDateMillis,
            selectableDates = if (!isAllDatesEnabled) {
                object : SelectableDates {
                    override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                        utcTimeMillis >= System.currentTimeMillis()

                    override fun isSelectableYear(year: Int): Boolean = true
                }
            } else DatePickerDefaults.AllDates,
        )
        val confirmEnabled = remember {
            derivedStateOf { datePickerState.selectedDateMillis != null }
        }
        DatePickerDialog(
            onDismissRequest = { openDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        openDialog = false
                        onDateClick(datePickerState.selectedDateMillis!!)
                    },
                    enabled = confirmEnabled.value,
                ) {
                    Text(stringResource(localesR.string.ok))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { openDialog = false }
                ) {
                    Text(stringResource(localesR.string.cancel))
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}