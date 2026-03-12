package com.viswa2k.eyecare.ui.theme

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween

object EyeCareAnimation {
    val tweenDefault = tween<Float>(durationMillis = 300)
    val tweenSlow = tween<Float>(durationMillis = 500)
    val springBouncy = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
}
