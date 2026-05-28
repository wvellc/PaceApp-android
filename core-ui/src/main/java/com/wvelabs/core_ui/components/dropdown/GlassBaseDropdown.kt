package com.wvelabs.core_ui.components.dropdown

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.kyant.backdrop.Backdrop

@Composable
fun <T> GlassBaseDropdown(
    items: List<T>,
    selectedItem: T?,
    onItemSelected: (T) -> Unit,
    itemTitleExtractor: @Composable (T) -> String,
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
    placeholder: String = "",

    // Field Configuration
    fieldShape: Shape,
    fieldContainerColor: Color,
    fieldContentColor: Color,
    fieldTextStyle: TextStyle,
    fieldPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
    arrowIcon: (@Composable (Modifier) -> Unit)? = null,

    // Menu Configuration
    menuCornerRadius: Dp = 24.dp,
    menuOffset: DpOffset = DpOffset(0.dp, 8.dp),
    menuBackgroundColor: Color = Color.White.copy(alpha = 0.3f),
    menuElevation: Dp = 16.dp, // 🌟 NEW: Dynamic Outer Shadow

    // Item Configuration
    itemTextStyle: TextStyle,
    selectedItemColor: Color,
    unselectedItemColor: Color,
    customItem: (@Composable (item: T, isSelected: Boolean, onClick: () -> Unit) -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(IntSize.Zero) }
    var fieldPosition by remember { mutableStateOf(Offset.Zero) }

    val transitionState = remember { MutableTransitionState(false) }
    transitionState.targetState = expanded

    val density = LocalDensity.current
    val windowSize = LocalWindowInfo.current.containerSize

    val screenWidthDp = with(density) { windowSize.width.toDp() }
    val screenHeightDp = with(density) { windowSize.height.toDp() }
    val popupWidth = with(density) { textFieldSize.width.toDp() }

    // Dropdown math
    val estimatedItemHeightPx = with(density) { 52.dp.toPx() }
    val estimatedMenuHeightPx = items.size * estimatedItemHeightPx
    val shouldShowAbove =
        (fieldPosition.y + textFieldSize.height + estimatedMenuHeightPx) > windowSize.height

    val popupAbsoluteX = fieldPosition.x.toInt() + with(density) { menuOffset.x.roundToPx() }
    val popupAbsoluteY = if (shouldShowAbove) {
        fieldPosition.y.toInt() - estimatedMenuHeightPx.toInt() - with(density) { menuOffset.y.roundToPx() }
    } else {
        fieldPosition.y.toInt() + textFieldSize.height + with(density) { menuOffset.y.roundToPx() }
    }

    Box(
        modifier = modifier.onGloballyPositioned { coordinates ->
            textFieldSize = coordinates.size
            fieldPosition = coordinates.positionInWindow()
        }
    ) {
        DropdownField(
            expanded = expanded,
            selectedItem = selectedItem,
            placeholder = placeholder,
            itemTitleExtractor = itemTitleExtractor,
            fieldShape = fieldShape,
            fieldContainerColor = fieldContainerColor,
            fieldContentColor = fieldContentColor,
            fieldTextStyle = fieldTextStyle,
            fieldPadding = fieldPadding,
            arrowIcon = arrowIcon,
            onToggle = { expanded = !expanded }
        )

        if (transitionState.currentState || transitionState.targetState) {
            Popup(
                alignment = Alignment.TopStart,
                offset = IntOffset(
                    x = -fieldPosition.x.toInt(),
                    y = -fieldPosition.y.toInt()
                ),
                onDismissRequest = { expanded = false },
                properties = PopupProperties(focusable = true, clippingEnabled = false)
            ) {
                Box(
                    modifier = Modifier
                        .size(width = screenWidthDp, height = screenHeightDp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { expanded = false }
                        )
                ) {

                    AnimatedVisibility(
                        visibleState = transitionState,
                        modifier = Modifier.offset {
                            IntOffset(x = popupAbsoluteX, y = popupAbsoluteY)
                        },
                        enter = fadeIn(tween(220)) +
                                scaleIn(initialScale = 0.92f, animationSpec = tween(260)) +
                                slideInVertically(tween(260)) { if (shouldShowAbove) it / 5 else -it / 5 },
                        exit = fadeOut(tween(180)) +
                                scaleOut(targetScale = 0.95f, animationSpec = tween(180)) +
                                slideOutVertically(tween(180)) { if (shouldShowAbove) it / 8 else -it / 8 }
                    ) {
                        Box(
                            modifier = Modifier
                                .width(popupWidth)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    enabled = false,
                                    onClick = {}
                                )
                                .liquidGlassPopup(
                                    backdrop = backdrop,
                                    menuCornerRadius = menuCornerRadius,
                                    menuBackgroundColor = menuBackgroundColor, // Pass down
                                    menuElevation = menuElevation,             // Pass down
                                    shouldShowAbove = shouldShowAbove
                                )
                                .padding(vertical = 8.dp)
                        ) {
                            DropdownMenuItemsList(
                                items = items,
                                selectedItem = selectedItem,
                                customItem = customItem,
                                itemTitleExtractor = itemTitleExtractor,
                                itemTextStyle = itemTextStyle,
                                selectedItemColor = selectedItemColor,
                                unselectedItemColor = unselectedItemColor,
                                onItemSelected = {
                                    expanded = false
                                    onItemSelected(it)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

