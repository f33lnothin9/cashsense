package ru.resodostudios.cashsense.navigation

import ru.resodostudios.cashsense.R
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.feature.categories.R as categoriesR
import ru.resodostudios.cashsense.feature.home.R as homeR
import ru.resodostudios.cashsense.feature.subscription.R as subscriptionR
import ru.resodostudios.cashsense.feature.subscriptions.R as subscriptionsR
import ru.resodostudios.cashsense.feature.wallet.R as walletR

enum class TopLevelDestination(
    val selectedIcon: Int,
    val unselectedIcon: Int,
    val iconTextId: Int,
    val titleTextId: Int,
    val fabIcon: Int,
    val fabTitle: Int
) {
    HOME(
        selectedIcon = CsIcons.HomeFilled,
        unselectedIcon = CsIcons.Home,
        iconTextId = homeR.string.home,
        titleTextId = R.string.app_name,
        fabIcon = CsIcons.Wallet,
        fabTitle = walletR.string.feature_wallet_new_wallet
    ),
    CATEGORIES(
        selectedIcon = CsIcons.CategoryFilled,
        unselectedIcon = CsIcons.Category,
        iconTextId = categoriesR.string.categories,
        titleTextId = categoriesR.string.categories,
        fabIcon = CsIcons.Add,
        fabTitle = categoriesR.string.new_category
    ),
    SUBSCRIPTIONS(
        selectedIcon = CsIcons.SubscriptionsFilled,
        unselectedIcon = CsIcons.Subscriptions,
        iconTextId = subscriptionsR.string.feature_subscriptions_title,
        titleTextId = subscriptionsR.string.feature_subscriptions_title,
        fabIcon = CsIcons.Add,
        fabTitle = subscriptionR.string.feature_subscription_new_subscription
    )
}