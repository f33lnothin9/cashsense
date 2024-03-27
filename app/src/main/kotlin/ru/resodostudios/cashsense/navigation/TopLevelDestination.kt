package ru.resodostudios.cashsense.navigation

import ru.resodostudios.cashsense.R
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.feature.category.dialog.R as categoryDialogR
import ru.resodostudios.cashsense.feature.category.list.R as categoryListR
import ru.resodostudios.cashsense.feature.home.R as homeR
import ru.resodostudios.cashsense.feature.subscription.R as subscriptionR
import ru.resodostudios.cashsense.feature.subscriptions.R as subscriptionsR
import ru.resodostudios.cashsense.feature.wallet.dialog.R as walletDialogR

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
        iconTextId = homeR.string.feature_home_title,
        titleTextId = R.string.app_name,
        fabIcon = CsIcons.Wallet,
        fabTitle = walletDialogR.string.feature_wallet_dialog_new_wallet
    ),
    CATEGORIES(
        selectedIcon = CsIcons.CategoryFilled,
        unselectedIcon = CsIcons.Category,
        iconTextId = categoryListR.string.feature_category_list_title,
        titleTextId = categoryListR.string.feature_category_list_title,
        fabIcon = CsIcons.Add,
        fabTitle = categoryDialogR.string.feature_category_dialog_new_category
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