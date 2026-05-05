package com.example.paceapp.core.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paceapp.R
import com.example.paceapp.core.components.animation.AnimationWrapper
import com.example.paceapp.theme.AppColors
import com.example.paceapp.theme.AppTheme
import com.wvelabs.core_ui.extensions.defaultScaleIn
import com.wvelabs.core_ui.extensions.fadeInUpTransition

@Composable
fun AppSuccessView(
    modifier: Modifier,
    title: String = "",
    @DrawableRes logoRes: Int? = null,
    logoSize: Dp = 198.dp,
    textHorizontalPaddings:Dp =36.dp,
    subtitle: String? = null,
    buttonLabel: String? = null,
    onButtonClick: () -> Unit = {},
) {
    AnimationWrapper {
        Column(
            modifier = modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            Spacer(modifier = Modifier.fillMaxHeight(0.27f))
            if (logoRes != null) {
                //Logo
                Image(
                    painter = painterResource(logoRes),
                    contentDescription = null,
                    modifier = Modifier
                        .size(logoSize)
                        .animateEnterExit(
                            enter = defaultScaleIn()
                        ),
                )
            }
            Spacer(modifier = Modifier.height(34.dp))

            //Title
            Text(
                text = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = textHorizontalPaddings)
                    .animateEnterExit(
                        enter = fadeInUpTransition()
                    ),
                textAlign = TextAlign.Center,
                style = AppTheme.typography.size24.copy(
                    color = AppColors.White,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 24.sp,
                )
            )

            if (subtitle != null) {
                Spacer(modifier = Modifier.height(8.dp))

                //Subtitle
                Text(
                    text = subtitle,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = textHorizontalPaddings)
                        .animateEnterExit(
                            enter = fadeInUpTransition(delay = 50)
                        ),
                    textAlign = TextAlign.Center,
                    style = AppTheme.typography.size16.copy(
                        color = AppColors.HintGray,
                        fontWeight = FontWeight.Medium,
                    )
                )
            }

            if (buttonLabel != null) {
                Spacer(modifier = Modifier.weight(1f))

                //Button
                AppButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateEnterExit(
                            enter = fadeInUpTransition(delay = 100)
                        ),
                    title = buttonLabel,
                    onClick = onButtonClick,
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF200FFF)
@Composable
fun AppSuccessPreview() = AppSuccessView(
    modifier = Modifier.fillMaxSize(),
    title = "Test Title",
    buttonLabel = "Next",
    logoRes = R.drawable.ic_otp_success
)