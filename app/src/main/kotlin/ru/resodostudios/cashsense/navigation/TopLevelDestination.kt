package ru.resodostudios.cashsense.navigation

import ru.resodostudios.cashsense.R
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.feature.category.list.navigation.CategoriesRoute
import ru.resodostudios.cashsense.feature.home.navigation.HomeRoute
import ru.resodostudios.cashsense.feature.settings.navigation.SettingsGraph
import ru.resodostudios.cashsense.feature.subscription.list.navigation.SubscriptionsRoute
import kotlin.reflect.KClass
import ru.resodostudios.cashsense.core.locales.R as localesR
import ru.resodostudios.cashsense.feature.settings.R as settingsR
import ru.resodostudios.cashsense.feature.subscription.dialog.R as subscriptionDialogR
import ru.resodostudios.cashsense.feature.subscription.list.R as subscriptionListR
import ru.resodostudios.cashsense.feature.wallet.dialog.R as walletDialogR

enum class TopLevelDestination(
    val selectedIcon: Int,
    val unselectedIcon: Int,
    val iconTextId: Int,
    val titleTextId: Int,
    val fabIcon: Int?,
    val fabTitle: Int?,
    val route: KClass<*>,
) {
    HOME(
        selectedIcon = CsIcons.HomeFilled,
        unselectedIcon = CsIcons.Home,
        iconTextId = localesR.string.home_title,
        titleTextId = R.string.app_name,
        fabIcon = CsIcons.Wallet,
        fabTitle = walletDialogR.string.feature_wallet_dialog_new_wallet,
        route = HomeRoute::class,
    ),
    CATEGORIES(
        selectedIcon = CsIcons.CategoryFilled,
        unselectedIcon = CsIcons.Category,
        iconTextId = localesR.string.categories_title,
        titleTextId = localesR.string.categories_title,
        fabIcon = CsIcons.Add,
        fabTitle = localesR.string.new_category,
        route = CategoriesRoute::class,
    ),
    SUBSCRIPTIONS(
        selectedIcon = CsIcons.AutoRenew,
        unselectedIcon = CsIcons.AutoRenew,
        iconTextId = subscriptionListR.string.feature_subscription_list_title,
        titleTextId = subscriptionListR.string.feature_subscription_list_title,
        fabIcon = CsIcons.Add,
        fabTitle = subscriptionDialogR.string.feature_subscription_dialog_new,
        route = SubscriptionsRoute::class,
    ),
    SETTINGS(
        selectedIcon = CsIcons.SettingsFilled,
        unselectedIcon = CsIcons.Settings,
        iconTextId = settingsR.string.feature_settings_title,
        titleTextId = settingsR.string.feature_settings_title,
        fabIcon = null,
        fabTitle = null,
        route = SettingsGraph::class,
    )
}