package net.paceapp.core.components.animation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith

/**
 * A generic horizontal sliding transition for multi-step flows.
 * 
 * @param isMovingForward Determines the direction of the slide based on the old and new states.
 */
fun <T> horizontalStepTransition(
    isMovingForward: (initial: T, target: T) -> Boolean
): AnimatedContentTransitionScope<T>.() -> ContentTransform = {
    
    val forward = isMovingForward(initialState, targetState)

    if (forward) {
        (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
            slideOutHorizontally { width -> -width } + fadeOut()
        )
    } else {
        (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
            slideOutHorizontally { width -> width } + fadeOut()
        )
    }.using(SizeTransform(clip = false))
}