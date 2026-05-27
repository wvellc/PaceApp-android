package net.paceapp.features.splash.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.unit.sp
import net.paceapp.R
import net.paceapp.core.components.AppBaseScreen
import net.paceapp.core.components.AppButton
import net.paceapp.core.components.AppButtonStyle
import net.paceapp.core.components.AppLogo
import net.paceapp.core.components.LogoStyle
import net.paceapp.core.components.animation.AnimationWrapper
import net.paceapp.features.splash.SplashContract.Event
import net.paceapp.features.splash.SplashContract.State
import net.paceapp.theme.AppColors
import net.paceapp.theme.AppTheme
import com.wvelabs.core_ui.extensions.defaultScaleIn
import com.wvelabs.core_ui.extensions.fadeInUpTransition

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
                    .padding(AppTheme.screenPadding),
            ) {
                AppLogo(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = innerPaddings.calculateTopPadding() + 120.dp)
                        .animateEnterExit(
                            enter = defaultScaleIn()
                        ),
                    logoStyle = LogoStyle.Vertical
                )
                Spacer(modifier = Modifier.weight(1f))

                AnimatedVisibility(
                    visible = state.showGetStarted,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
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
                            style = AppTheme.typography.light.copy(
                                fontSize = 32.sp,
                                textAlign = TextAlign.Center
                            ),
                            color = AppColors.White,
                            modifier = Modifier.animateEnterExit(
                                enter = fadeInUpTransition()
                            ),
                        )
                        Spacer(modifier = Modifier.height(25.dp))
                        AppButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateEnterExit(
                                    enter = fadeInUpTransition()
                                ),
                            title = stringResource(R.string.get_started),
                            trailingIconRes = R.drawable.ic_arrow,
                            style = AppButtonStyle.OUTLINED_GRADIENT,
                            onClick = { onEvent(Event.OnGetStarted) }
                        )
                        //Bottom inset paddings
                        Spacer(modifier = Modifier.height(innerPaddings.calculateBottomPadding()))

                    }
                }


            }
        }

    }
}


@Preview
@Composable
fun SplashPreview() = SplashContent(state = State(), onEvent = {})