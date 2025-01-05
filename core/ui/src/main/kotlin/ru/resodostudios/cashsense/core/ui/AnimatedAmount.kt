package ru.resodostudios.cashsense.core.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
    modifier: Modifier = Modifier,
    content: @Composable AnimatedContentScope.(targetState: BigDecimal) -> Unit,
) {
    AnimatedContent(
        targetState = targetState,
        transitionSpec = {
            if (targetState > initialState) {
                slideInVertically { -it } + fadeIn() togetherWith slideOutVertically { it } + fadeOut()
            } else {
                slideInVertically { it } + fadeIn() togetherWith slideOutVertically { -it } + fadeOut()
            }
        },
        label = label,
        modifier = modifier,
        content = content,
    )
}