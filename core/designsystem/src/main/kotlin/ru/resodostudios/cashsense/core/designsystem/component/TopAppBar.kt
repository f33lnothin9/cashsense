package ru.resodostudios.cashsense.core.designsystem.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CsTopAppBar(
    @StringRes titleRes: Int,
    @DrawableRes actionIconRes: Int,
    actionIconContentDescription: String?,
    modifier: Modifier = Modifier,
    onActionClick: () -> Unit = {},
) {
    TopAppBar(
        title = { Text(stringResource(titleRes)) },
        actions = {
            IconButton(onClick = onActionClick) {
                Icon(
                    imageVector = ImageVector.vectorResource(actionIconRes),
                    contentDescription = actionIconContentDescription,
                )
            }
        },
        modifier = modifier.testTag("csTopAppBar"),
    )
}