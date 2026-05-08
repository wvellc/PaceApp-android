package com.example.paceapp.features.main.settings.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOutBack
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.paceapp.R
import com.example.paceapp.core.components.AppButton
import com.example.paceapp.core.components.AppButtonStyle
import com.example.paceapp.theme.AppColors
import com.wvelabs.core_ui.extensions.defaultAnimSpec

@Composable
fun DevelopedByDetails(
    isDevOptionExpanded: Boolean = false,
    onDevButtonTap: () -> Unit = {}
) {
    AnimatedVisibility(
        visible = isDevOptionExpanded,
        enter = fadeIn(defaultAnimSpec(100)),
        exit = fadeOut(defaultAnimSpec(200))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, bottom = 8.dp, top = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // WveLabs Logo
            Icon(
                painter = painterResource(id = R.drawable.ic_wvelabs_logo), // Replace with actual logo
                contentDescription = "WveLabs",
                tint = AppColors.DarkCharcoal,
                modifier = Modifier
                    .wrapContentSize()
                    .animateEnterExit(
                        scaleIn(
                            defaultAnimSpec(500, easing = EaseInOutBack)
                        ) + fadeIn(defaultAnimSpec(500))
                    )
            )

            Spacer(modifier = Modifier.width(30.dp)) // Constant.h30


            // Visit Website Button
            AppButton(
                onClick = onDevButtonTap,
                height = 40.dp,
                padding = PaddingValues(vertical = 8.dp),
                style = AppButtonStyle.FILLED_GRADIENT,
                title = stringResource(R.string.visit_website),
                modifier = Modifier
                    .width(150.dp)
                    .animateEnterExit(
                        scaleIn(
                            initialScale = 0.2f,
                            animationSpec = defaultAnimSpec(
                                500,
                                200,
                                easing = EaseInOutBack
                            )
                        ) + fadeIn(defaultAnimSpec(500, 200))
                    ),
            )
        }
    }
}