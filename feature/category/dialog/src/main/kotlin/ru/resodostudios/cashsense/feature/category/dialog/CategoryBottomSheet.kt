package ru.resodostudios.cashsense.feature.category.dialog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.designsystem.component.CsModalBottomSheet
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.ui.LoadingState
import ru.resodostudios.cashsense.core.ui.R
import ru.resodostudios.cashsense.core.ui.StoredIcon

@Composable
fun CategoryBottomSheet(
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    viewModel: CategoryDialogViewModel = hiltViewModel(),
) {
    val categoryDialogState by viewModel.categoryDialogUiState.collectAsStateWithLifecycle()

    CategoryBottomSheet(
        categoryDialogState = categoryDialogState,
        onCategoryEvent = viewModel::onCategoryEvent,
        onDismiss = onDismiss,
        onEdit = onEdit,
    )
}

@Composable
fun CategoryBottomSheet(
    categoryDialogState: CategoryDialogUiState,
    onCategoryEvent: (CategoryDialogEvent) -> Unit,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
) {
    CsModalBottomSheet(
        onDismiss = onDismiss
    ) {
        AnimatedVisibility(categoryDialogState.isLoading) {
            LoadingState(
                Modifier
                    .height(100.dp)
                    .fillMaxWidth()
            )
        }
        AnimatedVisibility(!categoryDialogState.isLoading) {
            Column {
                ListItem(
                    headlineContent = { Text(categoryDialogState.title) },
                    leadingContent = {
                        Icon(
                            imageVector = ImageVector.vectorResource(StoredIcon.asRes(categoryDialogState.icon)),
                            contentDescription = null,
                        )
                    },
                )
                HorizontalDivider(Modifier.padding(16.dp))
                ListItem(
                    headlineContent = { Text(stringResource(R.string.edit)) },
                    leadingContent = {
                        Icon(
                            imageVector = ImageVector.vectorResource(CsIcons.Edit),
                            contentDescription = null,
                        )
                    },
                    modifier = Modifier.clickable {
                        onDismiss()
                        onEdit()
                    },
                )
                ListItem(
                    headlineContent = { Text(stringResource(R.string.delete)) },
                    leadingContent = {
                        Icon(
                            imageVector = ImageVector.vectorResource(CsIcons.Delete),
                            contentDescription = null,
                        )
                    },
                    modifier = Modifier.clickable {
                        onDismiss()
                        onCategoryEvent(CategoryDialogEvent.Delete)
                    },
                )
            }
        }
    }
}