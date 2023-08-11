package ru.resodostudios.cashsense.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import ru.resodostudios.cashsense.R
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

enum class TopLevelDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val iconTextId: Int,
    val titleTextId: Int,
) {
    HOME(
        selectedIcon = CsIcons.HomeFilled,
        unselectedIcon = CsIcons.Home,
        iconTextId = R.string.home,
        titleTextId = R.string.app_name,
    ),
    TRANSACTIONS(
        selectedIcon = CsIcons.TransactionFilled,
        unselectedIcon = CsIcons.Transaction,
        iconTextId = R.string.transactions,
        titleTextId = R.string.transactions,
    ),
    CATEGORIES(
        selectedIcon = CsIcons.CategoryFilled,
        unselectedIcon = CsIcons.Category,
        iconTextId = R.string.categories,
        titleTextId = R.string.categories,
    )
}