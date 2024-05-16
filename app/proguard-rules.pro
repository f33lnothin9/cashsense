# With R8 full mode generic signatures are stripped for classes that are not
# kept. Suspend functions are wrapped in continuations where the type argument
# is used.
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

-keep class ru.resodostudios.cashsense.feature.home.navigation.HomeDestination
-keep class ru.resodostudios.cashsense.feature.wallet.detail.navigation.WalletDestination
-keep class ru.resodostudios.cashsense.ui.home2pane.WalletPlaceholderDestination
-keep class ru.resodostudios.cashsense.ui.home2pane.DetailPaneNavHostDestination