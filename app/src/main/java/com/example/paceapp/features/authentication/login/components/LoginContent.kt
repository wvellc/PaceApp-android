package com.example.paceapp.features.authentication.login.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.paceapp.R
import com.example.paceapp.core.components.AppBaseScreen
import com.example.paceapp.core.components.AppButton
import com.example.paceapp.core.components.AppButtonStyle
import com.example.paceapp.core.components.AppLogo
import com.example.paceapp.core.components.AppSegmentedButtons
import com.example.paceapp.core.components.AppTextField
import com.example.paceapp.core.components.LogoStyle
import com.example.paceapp.core.components.ValidatorType
import com.example.paceapp.core.components.animation.AnimationWrapper
import com.example.paceapp.core.extensions.g2Continuity
import com.example.paceapp.features.authentication.data.enums.LoginTypes
import com.example.paceapp.features.authentication.data.enums.title
import com.example.paceapp.features.authentication.login.LoginContract.Event
import com.example.paceapp.features.authentication.login.LoginContract.State
import com.example.paceapp.ui.theme.AppColors
import com.example.paceapp.ui.theme.AppTheme
import com.kyant.capsule.ContinuousRoundedRectangle

@Composable
internal fun LoginContent(
    state: State,
    onEvent: (Event) -> Unit
) {

    //Phone field state
    val (phoneField, onPhoneChange) = remember { mutableStateOf(TextFieldValue()) }

    val focusRequester = remember { FocusRequester() }

    AppBaseScreen(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        isLoading = state.isLoading,
        hasPattern = true,
    ) { innerPaddings ->
        AnimationWrapper {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPaddings)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
            ) {
                AppLogo(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 120.dp),
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


                    AppTextField(
                        hint = "1234567890",
                        borderColor = AppColors.White20,
                        modifier = Modifier.fillMaxWidth(),
                        validatorType = ValidatorType.Phone,
                        value = phoneField,
                        onValueChange = onPhoneChange,
                        imeAction = ImeAction.Done,
                        showErrorMessage = true,
                        capitalization = KeyboardCapitalization.Words,
                        focusRequester = focusRequester,
                    )

                    AppButton(
                        modifier = Modifier.fillMaxWidth(),
                        style = AppButtonStyle.FILLED_GRADIENT,
                        title = stringResource(R.string.send_otp)
                    ) {

                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun LoginPreview() = LoginContent(
    state = State(),
) { }