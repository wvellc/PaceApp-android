package com.wvelabs.core_ui.components.dropdown

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

@Composable
internal fun <T> DropdownMenuItemsList(
    items: List<T>,
    selectedItem: T?,
    customItem: (@Composable (item: T, isSelected: Boolean, onClick: () -> Unit) -> Unit)?,
    itemTitleExtractor: @Composable (T) -> String,
    itemTextStyle: TextStyle,
    selectedItemColor: Color,
    unselectedItemColor: Color,
    onItemSelected: (T) -> Unit
) {
    Column {
        items.forEach { item ->
            val isSelected = item == selectedItem
            if (customItem != null) {
                customItem(item, isSelected) { onItemSelected(item) }
            } else {
                val contentColor by animateColorAsState(
                    targetValue = if (isSelected) selectedItemColor else unselectedItemColor,
                    label = "dropdown_item_color"
                )

                DropdownMenuItem(
                    text = {
                        Text(
                            text = itemTitleExtractor(item),
                            style = itemTextStyle,
                            color = contentColor
                        )
                    },
                    onClick = { onItemSelected(item) }
                )
            }
        }
    }
}
