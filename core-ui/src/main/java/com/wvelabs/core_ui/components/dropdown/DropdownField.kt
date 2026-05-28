package com.wvelabs.core_ui.components.dropdown

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle

@Composable
internal fun <T> DropdownField(
    expanded: Boolean,
    selectedItem: T?,
    placeholder: String,
    itemTitleExtractor: @Composable (T) -> String,
    fieldShape: Shape,
    fieldContainerColor: Color,
    fieldContentColor: Color,
    fieldTextStyle: TextStyle,
    fieldPadding: PaddingValues,
    arrowIcon: (@Composable (Modifier) -> Unit)?,
    onToggle: () -> Unit
) {
    val arrowRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "arrow_rotation"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(fieldShape)
            .background(fieldContainerColor)
            .clickable { onToggle() }
            .padding(fieldPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val displayText = selectedItem?.let { itemTitleExtractor(it) } ?: placeholder
        Text(
            text = displayText,
            style = fieldTextStyle,
            color = fieldContentColor
        )
        arrowIcon?.invoke(Modifier.rotate(arrowRotation))
    }
}
