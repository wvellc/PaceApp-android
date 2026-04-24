package com.example.paceapp.core.components.otpfield

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.paceapp.core.extensions.defaultClickable
import com.example.paceapp.theme.AppColors
import com.example.paceapp.theme.AppTheme
import kotlinx.coroutines.delay

@Composable
fun OtpField(
    modifier: Modifier = Modifier,
    otpValue: String,
    onOtpValueChange: (String) -> Unit,
    otpLength: Int = 6,
    boxSize: Dp = 50.dp,
    boxSpacing: Dp = 8.dp,
    enabled: Boolean = true,
    isError: Boolean = false,
    shape: Shape = RoundedCornerShape(12.dp),
    textStyle: TextStyle = AppTheme.typography.size24.copy(
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Medium,
        color = AppColors.White
    ),
    focusedBorderColor: Color = AppColors.White, // Default Indigo
    unfocusedBorderColor: Color = AppColors.White,
    errorColor: Color = AppColors.Error,
    autoFocus: Boolean = true,
    cursorColor: Color = AppColors.NeonAquaBlue,
) {
    val focusRequester = remember { FocusRequester() }

    if (autoFocus) {
        LaunchedEffect(Unit) {
            // A small delay is sometimes needed to ensure the UI is
            // fully ready to receive focus and show the keyboard.
            delay(100)
            focusRequester.requestFocus()
        }
    }
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        // Hidden TextField to handle actual input and keyboard logic
        BasicTextField(
            value = otpValue,
            onValueChange = {
                if (it.length <= otpLength && it.all { char -> char.isDigit() }) {
                    onOtpValueChange(it)
                }
            },
            modifier = Modifier
                .focusRequester(focusRequester)
                .size(1.dp) // Keep it tiny but present
                .alpha(0f),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            enabled = enabled,
        )

        // Visual Representation
        Row(
            horizontalArrangement = Arrangement.spacedBy(boxSpacing),
            modifier = Modifier.defaultClickable(rippleColor = null) { focusRequester.requestFocus() }
        ) {
            repeat(otpLength) { index ->
                val char = otpValue.getOrNull(index)?.toString() ?: ""
                val isFocused = otpValue.length == index

                OtpBox(
                    char = char,
                    isFocused = isFocused,
                    isError = isError,
                    size = boxSize,
                    shape = shape,
                    textStyle = textStyle,
                    focusedBorderColor = focusedBorderColor,
                    unfocusedBorderColor = unfocusedBorderColor,
                    errorColor = errorColor,
                    cursorColor = cursorColor
                )
            }
        }
    }
}

@Composable
private fun OtpBox(
    char: String,
    isFocused: Boolean,
    isError: Boolean,
    size: Dp,
    shape: Shape,
    textStyle: TextStyle,
    focusedBorderColor: Color,
    unfocusedBorderColor: Color,
    errorColor: Color,
    cursorColor: Color
) {

    val infiniteTransition = rememberInfiniteTransition(label = "cursor")
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1000 // 1 second blink cycle
                0.7f at 500 // Visible for 70% of the first half second
                0.0f at 600 // Quick fade out
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "cursorAlpha"
    )
    val borderColor = when {
        isError -> errorColor
        isFocused -> focusedBorderColor
        else -> unfocusedBorderColor
    }

    Box(
        modifier = Modifier
            .size(size)
            .border(width = if (isFocused) 2.dp else 1.dp, color = borderColor, shape = shape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = char,
            style = textStyle.copy(color = if (isError) errorColor else textStyle.color)
        )

        // Optional: Simple cursor for better UX
        if (isFocused && char.isEmpty()) {
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(size * 0.4f)
                    .graphicsLayer(alpha = cursorAlpha)
                    .background(cursorColor)
            )
        }
    }
}