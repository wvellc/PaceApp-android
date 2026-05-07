package com.wvelabs.core_ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun InitialAvatarView(
    name: String,
    modifier: Modifier = Modifier,
    size: DpSize = DpSize(50.dp,50.dp),
    shape: Shape = CircleShape,
    backgroundColor:Color = getColorFromName(name),
    textStyle: TextStyle = TextStyle(
        fontWeight = FontWeight.SemiBold,
        color = Color.White
    )
) {

    // Get the first characters
    val initials = name.trim().split("\\s+".toRegex())
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercase() }
        .joinToString("")

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(backgroundColor)
    ) {
        Text(
            text = initials,
            style = textStyle,
        )
    }
}


@Composable
fun rememberInitialsPainter(
    fullName: String,
    backgroundColor: Color = remember(fullName) { getColorFromName(fullName) },
    textStyle: TextStyle = TextStyle(
        fontWeight = FontWeight.Bold,
        color = Color.White,
        fontSize = 20.sp // Default base size
    )
): Painter {
    val textMeasurer = rememberTextMeasurer()
    val initials = remember(fullName) {
        fullName.trim().split("\\s+".toRegex())
            .take(2)
            .mapNotNull { it.firstOrNull()?.uppercase() }
            .joinToString("")
    }

    return remember(initials, backgroundColor, textStyle) {
        object : Painter() {
            // Unspecified size allows it to fill the container (Box/Image)
            override val intrinsicSize: Size = Size.Unspecified

            override fun DrawScope.onDraw() {
                // 0. Safeguard against unmeasured bounds
                if (size.width == 0f || size.height == 0f) return

                // 1. Draw Background
                drawRect(color = backgroundColor)

                // 2. Measure and scale text to fit ~40% of the container height
                val adaptiveSize = size.height * 0.4f
                val finalStyle = textStyle.copy(fontSize = adaptiveSize.toSp())

                val textLayoutResult = textMeasurer.measure(initials, finalStyle)

                // 3. Center perfectly
                drawText(
                    textLayoutResult = textLayoutResult,
                    topLeft = Offset(
                        x = (size.width - textLayoutResult.size.width) / 2,
                        y = (size.height - textLayoutResult.size.height) / 2
                    )
                )
            }
        }
    }
}

// Helper function to generate a color from a name string
fun getColorFromName(name: String?): Color {
    if (name.isNullOrBlank()) return Color.Gray
    val hash = kotlin.math.abs(name.hashCode()) // Ensure positive hash
    return Color(
        red = (hash and 0xFF0000 shr 16) / 255f,
        green = (hash and 0x00FF00 shr 8) / 255f,
        blue = (hash and 0x0000FF) / 255f,
        alpha = 1f
    )
}