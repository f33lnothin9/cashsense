package ru.resodostudios.cashsense.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import ru.resodostudios.cashsense.R
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.feature.category.list.navigation.CategoriesBaseRoute
import ru.resodostudios.cashsense.feature.category.list.navigation.CategoriesRoute
import ru.resodostudios.cashsense.feature.home.navigation.HomeRoute
import ru.resodostudios.cashsense.feature.settings.navigation.SettingsBaseRoute
import ru.resodostudios.cashsense.feature.settings.navigation.SettingsRoute
import ru.resodostudios.cashsense.feature.subscription.list.navigation.SubscriptionsBaseRoute
import ru.resodostudios.cashsense.feature.subscription.list.navigation.SubscriptionsRoute
import ru.resodostudios.cashsense.ui.home2pane.HomeListDetailRoute
import kotlin.reflect.KClass
import ru.resodostudios.cashsense.core.locales.R as localesR

/**
 * Represents the top-level destinations in the application.
 * Each destination has associated icons, text, and a route.
 *
 * @param selectedIcon Resource ID for the icon when the destination is selected.
 * @param unselectedIcon Resource ID for the icon when the destination is not selected.
 * @param iconTextId Resource ID for the text displayed with the icon.
 * @param titleTextId Resource ID for the title text of the destination.
 * @param fabIcon Resource ID for the Floating Action Button (FAB) icon, if applicable.
 * @param fabTitle Resource ID for the FAB title text, if applicable.
 * @param route The route to use when navigating to this destination.
 * @param baseRoute The highest ancestor of this destination. Defaults to [route], meaning that
 * there is a single destination in that section of the app (no nested destinations).
 */
enum class TopLevelDestination(
    @DrawableRes val selectedIcon: Int,
    @DrawableRes val unselectedIcon: Int,
    @StringRes val iconTextId: Int,
    @StringRes val titleTextId: Int,
    @DrawableRes val fabIcon: Int?,
    @StringRes val fabTitle: Int?,
    val route: KClass<*>,
    val baseRoute: KClass<*> = route,
) {
    HOME(
        selectedIcon = CsIcons.HomeFilled,
        unselectedIcon = CsIcons.Home,
        iconTextId = localesR.string.home_title,
        titleTextId = R.string.app_name,
        fabIcon = CsIcons.Wallet,
        fabTitle = localesR.string.new_wallet,
        route = HomeRoute::class,
        baseRoute = HomeListDetailRoute::class,
    ),
    CATEGORIES(
        selectedIcon = CsIcons.CategoryFilled,
        unselectedIcon = CsIcons.Category,
        iconTextId = localesR.string.categories_title,
        titleTextId = localesR.string.categories_title,
        fabIcon = CsIcons.Add,
        fabTitle = localesR.string.new_category,
        route = CategoriesRoute::class,
        baseRoute = CategoriesBaseRoute::class,
    ),
    SUBSCRIPTIONS(
        selectedIcon = CsIcons.AutoRenew,
        unselectedIcon = CsIcons.AutoRenew,
        iconTextId = localesR.string.subscriptions_title,
        titleTextId = localesR.string.subscriptions_title,
        fabIcon = CsIcons.Add,
        fabTitle = localesR.string.new_subscription,
        route = SubscriptionsRoute::class,
        baseRoute = SubscriptionsBaseRoute::class,
    ),
    SETTINGS(
        selectedIcon = CsIcons.SettingsFilled,
        unselectedIcon = CsIcons.Settings,
        iconTextId = localesR.string.settings_title,
        titleTextId = localesR.string.settings_title,
        fabIcon = null,
        fabTitle = null,
        route = SettingsRoute::class,
        baseRoute = SettingsBaseRoute::class,
    )
}