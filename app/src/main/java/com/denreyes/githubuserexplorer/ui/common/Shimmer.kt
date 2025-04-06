package com.denreyes.githubuserexplorer.ui.common

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Applies a shimmer animation effect to a composable as a loading placeholder.
 *
 * @param durationMillis Duration of the shimmer animation cycle.
 * @param shimmerColor Color used for the shimmer gradient.
 */
fun Modifier.shimmerEffect(
    durationMillis: Int = 1000,
    shimmerColor: Color = Color.LightGray.copy(alpha = 0.6f)
): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "shimmerTranslate"
    )

    drawBehind {
        val gradientWidth = size.width * 0.2f
        val startX = translateAnim.value - gradientWidth
        val endX = translateAnim.value
        val brush = Brush.linearGradient(
            colors = listOf(
                shimmerColor.copy(alpha = 0.6f),
                shimmerColor.copy(alpha = 0.2f),
                shimmerColor.copy(alpha = 0.6f)
            ),
            start = Offset(startX, 0f),
            end = Offset(endX, 0f)
        )
        drawRect(brush = brush)
    }
}
