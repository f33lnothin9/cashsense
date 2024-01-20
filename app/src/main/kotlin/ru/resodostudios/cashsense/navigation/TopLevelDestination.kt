package ru.resodostudios.cashsense.navigation

import ru.resodostudios.cashsense.R
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.feature.categories.R as categoriesR
import ru.resodostudios.cashsense.feature.home.R as homeR
import ru.resodostudios.cashsense.feature.subscriptions.R as subscriptionsR

enum class TopLevelDestination(
    val selectedIcon: Int,
    val unselectedIcon: Int,
    val iconTextId: Int,
    val titleTextId: Int,
) {
    HOME(
        selectedIcon = CsIcons.HomeFilled,
        unselectedIcon = CsIcons.Home,
        iconTextId = homeR.string.home,
        titleTextId = R.string.app_name,
    ),
    CATEGORIES(
        selectedIcon = CsIcons.CategoryFilled,
        unselectedIcon = CsIcons.Category,
        iconTextId = categoriesR.string.categories,
        titleTextId = categoriesR.string.categories,
    ),
    SUBSCRIPTIONS(
        selectedIcon = CsIcons.SubscriptionsFilled,
        unselectedIcon = CsIcons.Subscriptions,
        iconTextId = subscriptionsR.string.feature_subscriptions_title,
        titleTextId = subscriptionsR.string.feature_subscriptions_title,
    )
}