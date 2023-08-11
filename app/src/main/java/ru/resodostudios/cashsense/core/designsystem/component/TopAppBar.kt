package ru.resodostudios.cashsense.core.designsystem.component

import androidx.annotation.StringRes
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource

@ExperimentalMaterial3Api
@Composable
fun CsTopAppBar(
    @StringRes titleRes: Int,
    actionIcon: ImageVector,
    actionIconContentDescription: String?,
    onActionClick: () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior,
) {
    MediumTopAppBar(
        title = { Text(text = stringResource(id = titleRes)) },
        actions = {
            IconButton(onClick = onActionClick) {
                Icon(
                    imageVector = actionIcon,
                    contentDescription = actionIconContentDescription
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}