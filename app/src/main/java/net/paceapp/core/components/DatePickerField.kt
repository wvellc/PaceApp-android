package net.paceapp.core.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.paceapp.R
import net.paceapp.core.extensions.defaultClickable
import net.paceapp.theme.AppColors
import net.paceapp.theme.AppTheme
import com.kyant.capsule.ContinuousRoundedRectangle

enum class IconAlignment {
    Start,
    End
}

@Composable
fun DatePickerField(
    modifier: Modifier = Modifier,
    shape: Shape = ContinuousRoundedRectangle(12.dp),
    value: String? = null,
    title: String? = null,
    iconColor: Color? = null,
    iconAlignment: IconAlignment = IconAlignment.End,
    titleStyle: TextStyle = AppTheme.typography.semiBold.copy(
        color = AppColors.DarkCharcoal,
        fontSize = 20.sp,
    ),
    borderColor: Color = AppColors.FashionGray,
    titleSpacing: Dp = 8.dp,
    iconSpacing: Dp = 12.dp,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(titleSpacing)
    ) {
        // Title
        title?.let {
            Text(
                text = title,
                style = titleStyle
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(shape)
                .border(
                    border = BorderStroke(1.2.dp, color = borderColor),
                    shape = shape,
                )
                .background(AppColors.Transparent, shape)
                .defaultClickable(onClick = onClick)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            // Extracted Icon to avoid duplication
            val calendarIcon = @Composable {
                Image(
                    painter = painterResource(R.drawable.ic_calender),
                    contentDescription = "Calendar",
                    colorFilter = iconColor?.let {
                        ColorFilter.tint(it)
                    }
                )
            }

            // Extracted Text to avoid duplication
            val textContent = @Composable {
                Box(modifier = Modifier.weight(1f)) {
                    val textStyle = AppTheme.typography.medium.copy(
                        lineHeight = 24.sp,
                        fontSize = 18.sp,
                    )
                    if (value.isNullOrBlank()) {
                        Text(
                            text = "e.g. 31 Jan 2026",
                            color = AppColors.FashionGray,
                            style = textStyle
                        )
                    } else {
                        Text(
                            text = value,
                            color = AppColors.DarkCharcoal,
                            style = textStyle
                        )
                    }
                }
            }

            // Layout based on position
            if (iconAlignment == IconAlignment.Start) {
                calendarIcon()
                Spacer(modifier = Modifier.width(iconSpacing))
                textContent()
            } else {
                textContent()
                Spacer(modifier = Modifier.width(iconSpacing))
                calendarIcon()
            }
        }
    }
}