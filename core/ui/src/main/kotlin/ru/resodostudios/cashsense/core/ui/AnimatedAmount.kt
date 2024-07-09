package ru.resodostudios.cashsense.core.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import java.math.BigDecimal

@Composable
fun AnimatedAmount(
    targetState: BigDecimal,
    label: String,
    content: @Composable AnimatedContentScope.(targetState: BigDecimal) -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedContent(
        targetState = targetState,
        transitionSpec = {
            if (targetState > initialState) {
                slideInVertically { -it } togetherWith slideOutVertically { it }
            } else {
                slideInVertically { it } togetherWith slideOutVertically { -it }
            }
        },
        label = label,
        modifier = modifier,
        content = content,
    )
}