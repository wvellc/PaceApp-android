package com.example.paceapp.core.components

import androidx.annotation.DimenRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicSecureTextField
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldDecorator
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.arpitkatiyarprojects.countrypicker.models.CountryDetails
import com.example.paceapp.theme.AppColors
import com.example.paceapp.theme.AppTheme
import com.kyant.capsule.ContinuousRoundedRectangle


@Composable
fun AppTextField(
    modifier: Modifier = Modifier,
    hint: String,
    state: TextFieldState,
    oldPasswordState: TextFieldState? = null,
    focusRequester: FocusRequester = remember { FocusRequester() },
    borderShape: Shape = ContinuousRoundedRectangle(12.dp),
    validatorType: ValidatorType = ValidatorType.None,
    imeAction: ImeAction = ImeAction.Unspecified,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    capitalization: KeyboardCapitalization = KeyboardCapitalization.None,
    keyboardActions: KeyboardActions = KeyboardActions(),
    enabled: Boolean = true,
    readOnly: Boolean = false,
    maxLines: Int = 1,
    minLines: Int = 1,
    textStyle: TextStyle = AppTheme.typography.size18.copy(fontWeight = FontWeight.Medium),
    borderColor: Color = AppColors.White,
    iconColor: Color = AppColors.HintGray,
    onKeyboardAction: KeyboardActionHandler? = null,
    // Custom Border Width Controls
    unfocusedBorderWidth: Dp = 1.dp,
    focusedBorderWidth: Dp = 2.dp,

    // Simplified Error State
    showErrorMessage: Boolean = false, // Parent can force an error state
    errorTextStyle: TextStyle = AppTheme.typography.size16.copy(fontWeight = FontWeight.Medium),
    @DimenRes showPasswordIcon: Int? = null,
    @DimenRes hidePasswordIcon: Int? = null,
    onCountrySelected: (country: CountryDetails) -> Unit = {},
    height: Dp = 48.dp,
    title: String? = null,
    titleSpacing: Dp = 5.dp,
    selectedCountryCode: String = "",
    trailingIcon: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    val keyboardType = when (validatorType) {
        ValidatorType.Email -> KeyboardType.Email
        ValidatorType.Phone -> KeyboardType.Phone
        ValidatorType.Password, ValidatorType.ConfirmPassword -> KeyboardType.Password
        ValidatorType.Name, ValidatorType.Text -> KeyboardType.Text
        ValidatorType.Number -> KeyboardType.Decimal
        ValidatorType.None -> KeyboardType.Text
    }


    var isPasswordShown by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }
    val keyboard = LocalSoftwareKeyboardController.current
    var isInitialized by rememberSaveable { mutableStateOf(state.text.isNotEmpty()) }

    val isError = remember(state.text, isFocused, isInitialized, oldPasswordState?.text) {
        val currentText = state.text.toString()

        val hasValidationError = if (validatorType == ValidatorType.ConfirmPassword) {
            when {
                currentText.isBlank() -> false // Don't show "no match" if empty
                oldPasswordState?.text?.toString() != currentText -> true
                else -> Validator.validate(currentText, validatorType) != null
            }
        } else {
            Validator.validate(currentText, validatorType) != null
        }

        // ONLY show the error if the field is dirty (has been touched)
        isInitialized && hasValidationError
    }


    val currentBorderColor = when {
        !enabled -> AppColors.FashionGray
        isError -> AppColors.Error
        else -> borderColor
    }
    val currentBorderWidth =
        if (isFocused && enabled && !isError) focusedBorderWidth else unfocusedBorderWidth
    val currentIconColor = if (isError) AppColors.Error else iconColor
    val currentTextStyle = textStyle.copy(
        color = when {
            enabled -> AppColors.White
            else -> AppColors.FashionGray
        }
    )
    LaunchedEffect(state.text) {
        // Once the user types something, the field is considered "Dirty" forever
        if (state.text.isNotEmpty()) {
            isInitialized = true
        }
    }

    // Optional: Also mark as dirty if the user focuses and then leaves (Blur)
    LaunchedEffect(isFocused) {
        if (!isFocused && state.text.isNotEmpty()) {
            isInitialized = true
        }
    }

    //Text field decoration
    val decorator = TextFieldDecorator { innerTextField ->
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .clip(borderShape)
                .border(
                    width = currentBorderWidth,
                    color = currentBorderColor,
                    shape = borderShape
                )
                .padding(horizontal = 16.dp)
        ) {
            if (leadingIcon != null) {
                Box(modifier = Modifier.padding(end = 8.dp)) { leadingIcon() }
            }

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                // Hint (Using state.text instead of value.text)
                if (state.text.isEmpty()) {
                    Text(
                        text = hint,
                        style = textStyle,
                        color = AppColors.HintGray.copy(0.8f)
                    )
                }
                innerTextField()
            }

            // Trailing Icon
            if (validatorType.isPasswordTypeField() && showPasswordIcon != null && hidePasswordIcon != null) {
                IconButton(
                    onClick = { isPasswordShown = !isPasswordShown },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(
                        painter = painterResource(if (isPasswordShown) showPasswordIcon else hidePasswordIcon),
                        contentDescription = "Toggle Password",
                        tint = currentIconColor
                    )
                }
            } else if (trailingIcon != null) {
                Box(modifier = Modifier.padding(start = 8.dp)) { trailingIcon() }
            }
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(titleSpacing)
    ) {
        // Title
        title?.let {
            Text(
                text = title,
                style = AppTheme.typography.size20.copy(color = AppColors.White)

            )
        }

        // Alignment.Top ensures the Phone Prefix doesn't slide down when the error message appears!
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.Transparent),
        ) {

            // Prefix UI
            AnimatedVisibility(
                visible = validatorType == ValidatorType.Phone,
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally()
            ) {
                CountryCodeField(
                    modifier = Modifier
                        .height(height)
                        .clip(borderShape)
                        .border(
                            width = unfocusedBorderWidth,
                            color = borderColor,
                            shape = borderShape
                        ),
                    textStyle = currentTextStyle,
                    defaultCountryCode = selectedCountryCode,
                    onCountrySelected = onCountrySelected,
                )
            }

            // The Text Field & Error Column
            Column(modifier = Modifier.weight(1f)) {

                val fieldModifier = Modifier
                    .height(height)
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusEvent { focusState -> isFocused = focusState.isFocused }


                if (validatorType.isPasswordTypeField()) {
                    BasicSecureTextField(
                        state = state,
                        modifier = fieldModifier,
                        enabled = enabled,
                        textStyle = currentTextStyle,
                        cursorBrush = SolidColor(AppColors.NeonAquaBlue),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = keyboardType,
                            imeAction = imeAction,
                        ),
                        onKeyboardAction = onKeyboardAction,
                        interactionSource = interactionSource,
                        decorator = decorator,
                        // Natively handles the dots vs text without VisualTransformation!
                        textObfuscationMode = if (isPasswordShown) TextObfuscationMode.Visible else TextObfuscationMode.Hidden
                    )
                } else {
                    BasicTextField(
                        state = state,
                        modifier = fieldModifier,
                        enabled = enabled,
                        readOnly = readOnly,
                        textStyle = currentTextStyle,
                        cursorBrush = SolidColor(AppColors.NeonAquaBlue),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = keyboardType,
                            imeAction = imeAction,
                            capitalization = capitalization,
                        ),
                        onKeyboardAction = onKeyboardAction,
                        interactionSource = interactionSource,
                        decorator = decorator,
                        lineLimits = if (maxLines == 1) TextFieldLineLimits.SingleLine else TextFieldLineLimits.MultiLine(
                            minLines,
                            maxLines
                        )
                    )
                }

                // The Animated Error Message
                validatorType.errorResId?.let { errorRes ->
                    AnimatedVisibility(
                        visible = isError && showErrorMessage,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Text(
                            text = stringResource(errorRes),
                            color = AppColors.Error,
                            style = errorTextStyle,
                            modifier = Modifier.padding(top = 4.dp, start = 12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF235BFF)
@Composable
fun AppTextFieldPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        //  Standard Email Field
        AppTextField(
            title = "Email",
            hint = "Enter your email address",
            validatorType = ValidatorType.Email,
            state = rememberTextFieldState(),
        )

        //  Password Field
        val passwordState = rememberTextFieldState("Secret#1")
        AppTextField(
            title = "Password",
            hint = "Enter your password",
            validatorType = ValidatorType.Password,
            state = passwordState,
        )
        AppTextField(
            title = "Confirm Password",
            hint = "Enter your password",
            oldPasswordState = passwordState,
            validatorType = ValidatorType.ConfirmPassword,
            showErrorMessage = true,
            state = rememberTextFieldState(initialText = "Secret#2"),
        )
    }
}
