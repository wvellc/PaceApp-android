package com.example.paceapp.features.splash.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.paceapp.R
import com.example.paceapp.core.components.AppBaseScreen
import com.example.paceapp.core.components.AppButton
import com.example.paceapp.core.components.AppLogo
import com.example.paceapp.core.components.LogoStyle
import com.example.paceapp.core.components.animation.AnimationWrapper
import com.example.paceapp.features.splash.SplashContract.Event
import com.example.paceapp.features.splash.SplashContract.State
import com.example.paceapp.ui.theme.AppColors
import com.example.paceapp.ui.theme.AppTheme
import com.wvelabs.core_ui.extensions.defaultAnimSpec

@Composable
internal fun SplashContent(
    state: State,
    onEvent: (Event) -> Unit
) {
    AppBaseScreen(
        modifier = Modifier.fillMaxSize(),
        isLoading = state.isLoading,
        hasPattern = true,
    ) { innerPaddings ->

        AnimationWrapper {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPaddings)
                    .padding(16.dp),
            ) {
                Spacer(modifier = Modifier.fillMaxHeight(fraction = 0.25f))
                AppLogo(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateEnterExit(
                            enter = scaleIn(
                                animationSpec = defaultAnimSpec(),
                                initialScale = 0.4f,
                            )
                        ),
                    logoStyle = LogoStyle.Vertical
                )
                Spacer(modifier = Modifier.weight(1f))

                AnimatedVisibility(
                    visible = state.showGetStarted,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateEnterExit(
                                enter = fadeIn(animationSpec = defaultAnimSpec(delay = 500)) +
                                        slideInVertically(
                                            initialOffsetY = { height -> height / 2 },
                                            animationSpec = defaultAnimSpec(
                                                delay = 500,
                                                easing = EaseInOut
                                            )
                                        )
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val spanStyle = SpanStyle(
                            fontWeight = FontWeight.SemiBold,
                            color = AppColors.FluorescentMint,
                        )

                        val annotatedString = buildAnnotatedString {
                            append("${stringResource(R.string.turn)} ")
                            withStyle(style = spanStyle) {
                                append(stringResource(R.string.pace))
                            }
                            append(" ${stringResource(R.string.into)} ")
                            withStyle(style = spanStyle) {
                                append(stringResource(R.string.performance))
                            }
                        }

                        Text(
                            annotatedString,
                            style = AppTheme.typography.size32.copy(
                                fontWeight = FontWeight.Light,
                                textAlign = TextAlign.Center
                            ),
                            color = AppColors.White
                        )
                        Spacer(modifier = Modifier.height(25.dp))
                        AppButton(
                            modifier = Modifier
                                .fillMaxWidth(),
                            title = stringResource(R.string.get_started),
                            iconRes = R.drawable.ic_arrow,
                            onClick = { onEvent(Event.OnGetStarted) }
                        )
                    }
                }


            }
        }

    }
}


@Preview
@Composable
fun SplashPreview() = SplashContent(state = State(), onEvent = {})