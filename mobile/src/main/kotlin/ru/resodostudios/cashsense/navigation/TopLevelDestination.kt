package ru.resodostudios.cashsense.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import ru.resodostudios.cashsense.R
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.filled.Category
import ru.resodostudios.cashsense.core.designsystem.icon.filled.Home
import ru.resodostudios.cashsense.core.designsystem.icon.filled.Settings
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Add
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Autorenew
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Category
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Home
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Settings
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Wallet
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
 * Each destination has associated icons, text resources, and navigation routes.
 * This enum is used for configuring the bottom navigation bar and managing navigation within the app.
 *
 * @property selectedIcon The icon displayed when the destination is selected in the bottom navigation.
 * @property unselectedIcon The icon displayed when the destination is not selected.
 * @property iconTextId The resource ID for the text displayed below the icon in the bottom navigation.
 * @property titleTextId The resource ID for the title displayed in the top app bar when this destination is active.
 * @property fabIcon The icon displayed on the floating action button (FAB) for this destination (if applicable).
 * @property fabTitle The resource ID for the content description of the FAB (if applicable).
 * @property route The Kotlin class representing the composable route for this destination.
 *               Used for navigating to the main screen of the destination.
 * @property baseRoute The Kotlin class representing the base composable route for this destination.
 *               Used for navigating to any screen within the destination's navigation graph.
 *               Defaults to the `route` if not specified separately.
 */
enum class TopLevelDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    @StringRes val iconTextId: Int,
    @StringRes val titleTextId: Int,
    val fabIcon: ImageVector?,
    @StringRes val fabTitle: Int?,
    val route: KClass<*>,
    val baseRoute: KClass<*> = route,
) {
    HOME(
        selectedIcon = CsIcons.Filled.Home,
        unselectedIcon = CsIcons.Outlined.Home,
        iconTextId = localesR.string.home_title,
        titleTextId = R.string.app_name,
        fabIcon = CsIcons.Outlined.Wallet,
        fabTitle = localesR.string.new_wallet,
        route = HomeRoute::class,
        baseRoute = HomeListDetailRoute::class,
    ),
    CATEGORIES(
        selectedIcon = CsIcons.Filled.Category,
        unselectedIcon = CsIcons.Outlined.Category,
        iconTextId = localesR.string.categories_title,
        titleTextId = localesR.string.categories_title,
        fabIcon = CsIcons.Outlined.Add,
        fabTitle = localesR.string.new_category,
        route = CategoriesRoute::class,
        baseRoute = CategoriesBaseRoute::class,
    ),
    SUBSCRIPTIONS(
        selectedIcon = CsIcons.Outlined.Autorenew,
        unselectedIcon = CsIcons.Outlined.Autorenew,
        iconTextId = localesR.string.subscriptions_title,
        titleTextId = localesR.string.subscriptions_title,
        fabIcon = CsIcons.Outlined.Add,
        fabTitle = localesR.string.new_subscription,
        route = SubscriptionsRoute::class,
        baseRoute = SubscriptionsBaseRoute::class,
    ),
    SETTINGS(
        selectedIcon = CsIcons.Filled.Settings,
        unselectedIcon = CsIcons.Outlined.Settings,
        iconTextId = localesR.string.settings_title,
        titleTextId = localesR.string.settings_title,
        fabIcon = null,
        fabTitle = null,
        route = SettingsRoute::class,
        baseRoute = SettingsBaseRoute::class,
    )
}