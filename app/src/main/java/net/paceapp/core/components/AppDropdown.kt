package net.paceapp.core.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wvelabs.core_ui.components.dropdown.GlassBaseDropdown
import com.wvelabs.core_ui.utils.LocalAppBackdrop
import net.paceapp.R
import net.paceapp.theme.AppColors
import net.paceapp.theme.AppTheme

@Composable
fun <T> AppDropdown(
    items: List<T>,
    selectedItem: T?,
    onItemSelected: (T) -> Unit,
    itemTitleExtractor: @Composable (T) -> String,
    modifier: Modifier = Modifier,
    placeholder: String = "Select an option",
    fieldShape: RoundedCornerShape = RoundedCornerShape(12.dp),
    customItem: (@Composable (item: T, isSelected: Boolean, onClick: () -> Unit) -> Unit)? = null,
) {
    val backdrop = LocalAppBackdrop.current

    GlassBaseDropdown(
        items = items,
        selectedItem = selectedItem,
        onItemSelected = onItemSelected,
        itemTitleExtractor = itemTitleExtractor,
        modifier = modifier,
        placeholder = placeholder,
        menuOffset = DpOffset(0.dp, 2.dp),
        fieldShape = fieldShape,
        fieldContainerColor = AppColors.Transparent, // Or your input background color
        fieldContentColor = AppColors.DarkCharcoal,
        fieldTextStyle = AppTheme.typography.medium.copy(
            fontSize = 18.sp,
            lineHeight = 24.sp
        ),

        // Provide the arrow, accepting the rotation modifier from the Base component
        arrowIcon = { rotationModifier ->
            Image(
                painter = painterResource(id = R.drawable.ic_down_arrow), // Replace with your icon
                contentDescription = "Dropdown Arrow",
                modifier = rotationModifier.size(24.dp),
            )
        },
        menuCornerRadius = 24.dp,
        itemTextStyle = AppTheme.typography.medium.copy(
            fontSize = 18.sp,
            lineHeight = 24.sp
        ),
        selectedItemColor = AppColors.NeonAquaBlue,      // Highlight color for the selected item!
        unselectedItemColor = AppColors.DarkCharcoal,
        menuElevation = 4.dp,
        menuBackgroundColor = AppColors.White.copy(alpha = 0.8f),
        customItem = customItem,
        backdrop = backdrop

    )
}