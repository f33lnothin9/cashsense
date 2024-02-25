package ru.resodostudios.cashsense.feature.category

import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.ui.getIconId

@Composable
fun CategoryBottomSheet(
    onDismiss: () -> Unit,
    viewModel: CategoryViewModel = hiltViewModel(),
) {
    val categoryState by viewModel.categoryUiState.collectAsStateWithLifecycle()

    CategoryBottomSheet(
        categoryState = categoryState,
        onCategoryEvent = viewModel::onCategoryEvent,
        onDismiss = onDismiss,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryBottomSheet(
    categoryState: CategoryUiState,
    onCategoryEvent: (CategoryEvent) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        windowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        val context = LocalContext.current

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .then(
                    if (Build.VERSION.SDK_INT <= 25) Modifier.padding(bottom = 40.dp) else Modifier.navigationBarsPadding()
                )
        ) {
            ListItem(
                headlineContent = { Text(categoryState.title.text) },
                leadingContent = {
                    Icon(
                        imageVector = ImageVector.vectorResource(categoryState.icon.getIconId(context)),
                        contentDescription = null
                    )
                },
            )
            HorizontalDivider(Modifier.padding(16.dp))
            ListItem(
                headlineContent = { Text(stringResource(ru.resodostudios.cashsense.core.ui.R.string.edit)) },
                leadingContent = {
                    Icon(
                        imageVector = ImageVector.vectorResource(CsIcons.Edit),
                        contentDescription = null
                    )
                },
            )
            ListItem(
                headlineContent = { Text(stringResource(ru.resodostudios.cashsense.core.ui.R.string.delete)) },
                leadingContent = {
                    Icon(
                        imageVector = ImageVector.vectorResource(CsIcons.Delete),
                        contentDescription = null
                    )
                },
                modifier = Modifier.clickable {
                    onDismiss()
                    onCategoryEvent(CategoryEvent.Delete)
                }
            )
        }
    }
}