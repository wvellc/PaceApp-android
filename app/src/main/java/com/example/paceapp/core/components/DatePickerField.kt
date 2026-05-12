package com.example.paceapp.core.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paceapp.R
import com.example.paceapp.core.extensions.defaultClickable
import com.example.paceapp.theme.AppColors
import com.example.paceapp.theme.AppTheme
import com.kyant.capsule.ContinuousRoundedRectangle

@Composable
fun DatePickerField(
    modifier: Modifier = Modifier,
    shape: Shape = ContinuousRoundedRectangle(12.dp),
    value: String? = null,
    title: String? = null,
    titleStyle: TextStyle = AppTheme.typography.size20.copy(
        color = AppColors.DarkCharcoal,
        fontWeight = FontWeight.SemiBold,
    ),
    titleSpacing: Dp = 8.dp,
    onClick: () -> Unit
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
                .border(
                    border = BorderStroke(1.2.dp, color = AppColors.FashionGray),
                    shape = shape,
                )
                .background(AppColors.Transparent, shape)
                .defaultClickable(onClick = onClick)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier.weight(1f)) {
                if (value.isNullOrBlank()) {
                    Text(
                        text = "e.g. 31 Jan 2026",
                        style = AppTheme.typography.size18.copy(
                            lineHeight = 24.sp,
                            color = AppColors.FashionGray,
                            fontWeight = FontWeight.Medium
                        )
                    )
                } else {
                    Text(
                        text = value,
                        style = AppTheme.typography.size18.copy(
                            lineHeight = 24.sp,
                            color = AppColors.DarkCharcoal,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            Image(
                painter = painterResource(R.drawable.ic_calender),
                contentDescription = "Calendar"
            )
        }
    }
}
