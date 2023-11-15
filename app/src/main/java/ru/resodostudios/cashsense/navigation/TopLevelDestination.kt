package ru.resodostudios.cashsense.navigation

import ru.resodostudios.cashsense.R
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

enum class TopLevelDestination(
    val selectedIcon: Int,
    val unselectedIcon: Int,
    val iconTextId: Int,
    val titleTextId: Int,
) {
    HOME(
        selectedIcon = CsIcons.HomeFilled,
        unselectedIcon = CsIcons.Home,
        iconTextId = R.string.home,
        titleTextId = R.string.app_name,
    ),
    CATEGORIES(
        selectedIcon = CsIcons.CategoryFilled,
        unselectedIcon = CsIcons.Category,
        iconTextId = R.string.categories,
        titleTextId = R.string.categories,
    ),
    SUBSCRIPTIONS(
        selectedIcon = CsIcons.SubscriptionsFilled,
        unselectedIcon = CsIcons.Subscriptions,
        iconTextId = R.string.subscriptions,
        titleTextId = R.string.subscriptions,
    )
}