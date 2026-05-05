package com.example.paceapp.features.authentication.login.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.EaseInOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paceapp.R
import com.example.paceapp.config.AppWebUrls
import com.example.paceapp.core.components.AppBaseScreen
import com.example.paceapp.core.components.AppButton
import com.example.paceapp.core.components.AppLogo
import com.example.paceapp.core.components.AppSegmentedButtons
import com.example.paceapp.core.components.AppTextField
import com.example.paceapp.core.components.LogoStyle
import com.example.paceapp.core.components.ValidatorType
import com.example.paceapp.core.components.animation.AnimationWrapper
import com.example.paceapp.core.domain.enums.LoginTypes
import com.example.paceapp.core.extensions.clearFocusOnTap
import com.example.paceapp.core.extensions.g2Continuity
import com.example.paceapp.core.extensions.titleRes
import com.example.paceapp.core.extensions.verticalScrollOnIme
import com.example.paceapp.core.utils.CountryMapper
import com.example.paceapp.features.authentication.login.LoginContract.Event
import com.example.paceapp.features.authentication.login.LoginContract.State
import com.example.paceapp.theme.AppColors
import com.example.paceapp.theme.AppTheme
import com.kyant.capsule.ContinuousRoundedRectangle
import com.wvelabs.core_ui.extensions.defaultAnimSpec

@Composable
internal fun LoginContent(
    state: State,
    onEvent: (Event) -> Unit,
    emailFocus: FocusRequester,
    phoneFocus: FocusRequester
) {
    AppBaseScreen(
        modifier = Modifier
            .fillMaxSize()
            .clearFocusOnTap(LocalFocusManager.current),
        isLoading = state.isLoading,
        hasPattern = true,
        animationWrapper = { content -> AnimationWrapper { content() } }
    ) { innerPaddings ->
        //Scroll container
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScrollOnIme()
                .padding(innerPaddings)
                .padding(16.dp)
        ) {
            //Logo
            AppLogo(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 120.dp),
                imageSize = DpSize(136.dp, 91.dp),
                logoStyle = LogoStyle.Vertical
            )
            //Login form
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .padding(top = 110.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                //Title
                Text(
                    stringResource(R.string.login_to_your_account),
                    style = AppTheme.typography.size20.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = AppColors.White,
                )

                //Login types
                AppSegmentedButtons(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(ContinuousRoundedRectangle(12.dp, continuity = g2Continuity))
                        .background(AppColors.White.copy(alpha = 0.1f))
                        .padding(4.dp),
                    segments = LoginTypes.entries,
                    selectedSegment = state.selectedLoginType,
                    itemTitle = { stringResource(it.titleRes) },
                    onSegmentSelected = { onEvent(Event.OnLoginTypeSelected(it)) },
                )

                //Text field based on login type
                Crossfade(
                    targetState = state.selectedLoginType,
                    modifier = Modifier,
                    animationSpec = defaultAnimSpec(duration = 300, easing = EaseInOut),
                ) { loginTypes ->
                    when (loginTypes) {
                        //Email field
                        LoginTypes.EMAIL -> AppTextField(
                            state = state.emailState,
                            hint = stringResource(R.string.email_address),
                            borderColor = AppColors.White20,
                            modifier = Modifier.fillMaxWidth(),
                            validatorType = ValidatorType.Email,
                            imeAction = ImeAction.Done,
                            showErrorMessage = true,
                            unfocusedBorderWidth = 1.2.dp,
                            focusedBorderWidth = 1.2.dp,
                            capitalization = KeyboardCapitalization.None,
                            focusRequester = emailFocus,
                        )

                        //Phone number field
                        LoginTypes.PHONE -> AppTextField(
                            hint = "1234567890",
                            state = state.phoneState,
                            borderColor = AppColors.White20,
                            modifier = Modifier.fillMaxWidth(),
                            validatorType = ValidatorType.Phone,
                            imeAction = ImeAction.Done,
                            unfocusedBorderWidth = 1.2.dp,
                            focusedBorderWidth = 1.2.dp,
                            showErrorMessage = true,
                            selectedCountryCode = CountryMapper.getIsoFromDialCode(state.countryCode),
                            capitalization = KeyboardCapitalization.None,
                            focusRequester = phoneFocus,
                            onCountrySelected = { country ->
                                onEvent(
                                    Event.OnCountrySelected(
                                        dialCode = country.countryPhoneNumberCode
                                    )
                                )
                            }
                        )
                    }
                }

                //Send otp button
                AppButton(
                    modifier = Modifier
                        .fillMaxWidth(),
                    title = stringResource(R.string.send_otp),
                    enabled = state.isSendOTPEnabled
                ) { onEvent(Event.OnLoginClick) }

                //Terms condition style
                val spanStyle = SpanStyle(
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.White,
                )

                //Spanned string
                val annotatedString = buildAnnotatedString {
                    append("${stringResource(R.string.by_continuing_you_agree_to_our)} ")

                    //Terms & service
                    pushLink(
                        LinkAnnotation.Clickable(
                            tag = "terms",
                            styles = TextLinkStyles(style = spanStyle),
                            linkInteractionListener = {
                                onEvent(Event.ToWebview(AppWebUrls.TERM_CONDITIONS))
                            }
                        )
                    )
                    append(stringResource(R.string.terms_of_service))
                    pop()

                    //And label
                    append(" ${stringResource(R.string.and)} ")

                    // Privacy policy
                    pushLink(
                        LinkAnnotation.Clickable(
                            tag = "policy",
                            styles = TextLinkStyles(style = spanStyle),
                            linkInteractionListener = {
                                onEvent(Event.ToWebview(AppWebUrls.PRIVACY_POLICY))
                            }
                        )
                    )
                    append("${stringResource(R.string.privacy_policy)}.")
                    pop()
                }
                //Term condition text
                Text(
                    annotatedString,
                    style = AppTheme.typography.size16.copy(
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    ),
                    color = AppColors.HintGray,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }
    }
}
