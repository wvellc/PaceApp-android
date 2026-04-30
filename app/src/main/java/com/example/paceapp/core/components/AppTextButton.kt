package com.example.paceapp.core.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.example.paceapp.theme.AppColors
import com.example.paceapp.theme.AppTheme

@Composable
fun AppTextButton(
    text: Any, // Accept String or AnnotatedString
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(50),
    contentColor: Color = AppColors.NeonAquaBlue,
    disabledContentColor: Color = AppColors.NeonAquaBlue20,
    style: TextStyle = AppTheme.typography.size14.copy(fontWeight = FontWeight.SemiBold),
    contentPadding: PaddingValues = ButtonDefaults.TextButtonContentPadding,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,

    ) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.textButtonColors(
            contentColor = contentColor,
            disabledContentColor = disabledContentColor
        ),
        contentPadding = contentPadding,
        shape = shape,
    ) {
        if (leadingIcon != null) leadingIcon()

        // Handle both String and AnnotatedString types
        when (text) {
            is AnnotatedString -> Text(text = text, style = style)
            is String -> Text(text = text, style = style)
        }

        if (trailingIcon != null) trailingIcon()
    }
}