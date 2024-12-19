package ru.resodostudios.cashsense.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.locales.R

@Composable
fun WalletDropdownMenu(
    onTransferClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.wrapContentSize(Alignment.TopStart),
    ) {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = ImageVector.vectorResource(CsIcons.MoreVert),
                contentDescription = stringResource(R.string.wallet_menu_icon_description),
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.transfer)) },
                onClick = {
                    onTransferClick()
                    expanded = false
                },
                leadingIcon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(CsIcons.SendMoney),
                        contentDescription = null,
                    )
                },
            )
            HorizontalDivider(
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.edit)) },
                onClick = {
                    onEditClick()
                    expanded = false
                },
                leadingIcon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(CsIcons.Edit),
                        contentDescription = null,
                    )
                },
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.delete)) },
                onClick = {
                    onDeleteClick()
                    expanded = false
                },
                leadingIcon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(CsIcons.Delete),
                        contentDescription = null,
                    )
                },
            )
        }
    }
}