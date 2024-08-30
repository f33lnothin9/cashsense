package ru.resodostudios.cashsense.core.designsystem.component

import androidx.annotation.StringRes
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CsTopAppBar(
    @StringRes titleRes: Int,
    modifier: Modifier = Modifier,
) {
    CenterAlignedTopAppBar(
        title = { Text(stringResource(titleRes)) },
        modifier = modifier.testTag("csTopAppBar"),
    )
}