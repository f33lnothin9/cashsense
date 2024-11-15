package ru.resodostudios.cashsense.core.shortcuts

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.resodostudios.cashsense.core.util.Constants.DEEP_LINK_SCHEME_AND_HOST
import ru.resodostudios.cashsense.core.util.Constants.HOME_PATH
import ru.resodostudios.cashsense.core.util.Constants.TARGET_ACTIVITY_NAME
import javax.inject.Inject
import ru.resodostudios.cashsense.core.locales.R as localesR

private const val DYNAMIC_TRANSACTION_SHORTCUT_ID = "dynamic_new_transaction"

internal class DynamicShortcutManager @Inject constructor(
    @ApplicationContext private val context: Context,
) : ShortcutManager {

    override fun addTransactionShortcut(walletId: String) {
        val shortcutInfo = ShortcutInfoCompat.Builder(context, DYNAMIC_TRANSACTION_SHORTCUT_ID)
            .setShortLabel(context.getString(localesR.string.new_transaction))
            .setLongLabel(context.getString(localesR.string.transaction_shortcut_long_label))
            .setIcon(IconCompat.createWithResource(context, R.drawable.ic_shortcut_receipt_long))
            .setIntent(
                Intent().apply {
                    action = Intent.ACTION_VIEW
                    data = "$DEEP_LINK_SCHEME_AND_HOST/$HOME_PATH/$walletId/true".toUri()
                    component = ComponentName(
                        context.packageName,
                        TARGET_ACTIVITY_NAME,
                    )
                }
            )
            .build()
        ShortcutManagerCompat.pushDynamicShortcut(context, shortcutInfo)
    }

    override fun removeShortcuts() = ShortcutManagerCompat.removeAllDynamicShortcuts(context)
}