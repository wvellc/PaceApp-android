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
import androidx.compose.foundation.text.input.clearText
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component1
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component2
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
import com.example.paceapp.core.components.AppButtonStyle
import com.example.paceapp.core.components.AppLogo
import com.example.paceapp.core.components.AppSegmentedButtons
import com.example.paceapp.core.components.AppTextField
import com.example.paceapp.core.components.LogoStyle
import com.example.paceapp.core.components.Validator
import com.example.paceapp.core.components.ValidatorType
import com.example.paceapp.core.components.animation.AnimationWrapper
import com.example.paceapp.core.extensions.clearFocusOnTap
import com.example.paceapp.core.extensions.g2Continuity
import com.example.paceapp.core.extensions.verticalScrollOnIme
import com.example.paceapp.core.utils.CountryMapper
import com.example.paceapp.features.authentication.data.enums.LoginTypes
import com.example.paceapp.features.authentication.data.enums.title
import com.example.paceapp.features.authentication.login.LoginContract.Event
import com.example.paceapp.features.authentication.login.LoginContract.State
import com.example.paceapp.theme.AppColors
import com.example.paceapp.theme.AppTheme
import com.kyant.capsule.ContinuousRoundedRectangle
import com.wvelabs.core_ui.extensions.defaultAnimSpec
import kotlinx.coroutines.delay

@Composable
internal fun LoginContent(
    state: State,
    onEvent: (Event) -> Unit
) {

    //Focus requesters
    val (emailFocus, phoneFocus) = remember { FocusRequester.createRefs() }

    val isButtonEnabled by remember(state.selectedLoginType, state.phoneState.text) {
        derivedStateOf {
            when (state.selectedLoginType) {
                LoginTypes.EMAIL -> {
                    val rawEmail = state.emailState.text.trim()
                    rawEmail.isNotBlank() && Validator.validate(
                        rawEmail.toString(),
                        ValidatorType.Email
                    ) == null
                }

                LoginTypes.PHONE -> {
                    val rawPhone = state.phoneState.text.trim()
                    rawPhone.isNotBlank() && Validator.validate(
                        rawPhone.toString(),
                        ValidatorType.Phone
                    ) == null
                }
            }
        }
    }

    //Login field type change
    LaunchedEffect(state.selectedLoginType) {
        // Request focus on the specific field!
        when (state.selectedLoginType) {
            LoginTypes.EMAIL -> {
                emailFocus.requestFocus()
                delay(300)
                state.phoneState.clearText()

            }

            LoginTypes.PHONE -> {
                phoneFocus.requestFocus()
                delay(300)
                state.emailState.clearText()
            }
        }
    }

    AppBaseScreen(
        modifier = Modifier
            .fillMaxSize()
            .clearFocusOnTap(LocalFocusManager.current),
        isLoading = state.isLoading,
        hasPattern = true,
        animationWrapper = { content -> AnimationWrapper { content() } }
    ) { innerPaddings ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScrollOnIme()
                .padding(innerPaddings)
                .padding(16.dp)
        ) {
            AppLogo(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 120.dp),
                imageSize = DpSize(136.dp, 91.dp),
                logoStyle = LogoStyle.Vertical
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .padding(top = 110.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    stringResource(R.string.login_to_your_account),
                    style = AppTheme.typography.size20.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = AppColors.White,
                )

                AppSegmentedButtons(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(ContinuousRoundedRectangle(12.dp, continuity = g2Continuity))
                        .background(AppColors.White.copy(alpha = 0.1f))
                        .padding(4.dp),
                    segmentShape = ContinuousRoundedRectangle(8.dp),
                    segments = LoginTypes.entries,
                    selectedSegment = state.selectedLoginType,
                    itemTitle = { it.title },
                    onSegmentSelected = { onEvent(Event.OnLoginTypeSelected(it)) },
                )

                Crossfade(
                    targetState = state.selectedLoginType,
                    modifier = Modifier,
                    animationSpec = defaultAnimSpec(duration = 300, easing = EaseInOut),
                ) { loginTypes ->
                    when (loginTypes) {
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


                AppButton(
                    modifier = Modifier
                        .fillMaxWidth(),
                    style = AppButtonStyle.FILLED_GRADIENT,
                    title = stringResource(R.string.send_otp),
                    enabled = isButtonEnabled
                ) { onEvent(Event.OnLoginClick) }

                val spanStyle = SpanStyle(
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.White,
                )
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
