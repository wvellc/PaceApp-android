package com.example.paceapp.core.components

import androidx.annotation.DimenRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.paceapp.R
import com.example.paceapp.core.extensions.defaultClickable
import com.example.paceapp.theme.AppColors
import com.example.paceapp.theme.AppTheme
import com.kyant.capsule.ContinuousRoundedRectangle

@Composable
fun AppTextField(
    modifier: Modifier = Modifier,
    hint: String,
    focusRequester: FocusRequester = remember { FocusRequester() },
    borderShape: Shape = ContinuousRoundedRectangle(12.dp),
    value: TextFieldValue = TextFieldValue(),
    onValueChange: (TextFieldValue) -> Unit = {},
    trailingIcon: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    oldPassword: TextFieldValue? = null,
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

    // Custom Border Width Controls
    unfocusedBorderWidth: Dp = 1.dp,
    focusedBorderWidth: Dp = 2.dp,

    // Simplified Error State
    showErrorMessage: Boolean = false, // Parent can force an error state
    errorTextStyle: TextStyle = AppTheme.typography.size16.copy(fontWeight = FontWeight.Medium),

    @DimenRes showPasswordIcon: Int? = null,
    @DimenRes hidePasswordIcon: Int? = null,

    title: String? = null,
    titleSpacing: Dp = 5.dp,
) {
    var isError by remember { mutableStateOf(false) }
    var isPasswordShown by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }
    val keyboard = LocalSoftwareKeyboardController.current

    val keyboardType = when (validatorType) {
        ValidatorType.Email -> KeyboardType.Email
        ValidatorType.Phone -> KeyboardType.Phone
        ValidatorType.Password, ValidatorType.ConfirmPassword -> KeyboardType.Password
        ValidatorType.Name, ValidatorType.Text -> KeyboardType.Text
        ValidatorType.Number -> KeyboardType.Decimal
        ValidatorType.None -> KeyboardType.Text
    }

    val currentBorderColor = when {
        !enabled -> AppColors.FashionGray
        isError -> AppColors.Error
        else -> borderColor
    }
    val currentBorderWidth =
        if (isFocused && enabled && !isError) focusedBorderWidth else unfocusedBorderWidth
    val currentIconColor = if (isError) AppColors.Error else iconColor

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

            // Phone Prefix UI
            if (validatorType == ValidatorType.Phone) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .height(48.dp)
                        .clip(borderShape)
                        .border(
                            width = unfocusedBorderWidth,
                            color = borderColor,
                            shape = borderShape
                        )
                        .defaultClickable(rippleColor = AppColors.White20, onClick = {})
                        .padding(horizontal = 12.dp)
                ) {
                    Text("+91", style = textStyle, color = AppColors.White)
                    Image(
                        painter = painterResource(R.drawable.ic_down_arrow),
                        contentDescription = null
                    )
                }
            }

            // The Text Field & Error Column
            Column(modifier = Modifier.weight(1f)) {
                BasicTextField(
                    modifier = Modifier
                        .height(48.dp) // Fixed height to match prefix
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .onFocusEvent { focusState ->
                            isFocused = focusState.isFocused
                            if (validatorType == ValidatorType.ConfirmPassword) {
                                isError = when {
                                    value.text.isBlank() -> false
                                    oldPassword?.text != value.text -> true
                                    else -> Validator.validate(value.text, validatorType) != null
                                }
                            }
                            if (focusState.isFocused && isError) {
                                keyboard?.show()
                            }
                        },
                    value = value,
                    onValueChange = { newValue ->
                        isError = if (validatorType == ValidatorType.ConfirmPassword) {
                            when {
                                newValue.text.isBlank() -> false
                                oldPassword?.text != newValue.text -> true
                                else -> Validator.validate(newValue.text, validatorType) != null
                            }
                        } else {
                            Validator.validate(newValue.text, validatorType) != null
                        }
                        onValueChange.invoke(newValue)
                    },
                    enabled = enabled,
                    readOnly = readOnly,
                    singleLine = maxLines <= 1,
                    maxLines = maxLines,
                    minLines = minLines,
                    textStyle = textStyle.copy(
                        color = when {
                            enabled -> AppColors.White
                            else -> AppColors.FashionGray
                        }
                    ),
                    cursorBrush = SolidColor(AppColors.NeonAquaBlue),

                    keyboardOptions = KeyboardOptions(
                        keyboardType = keyboardType,
                        imeAction = imeAction,
                        capitalization = capitalization,
                    ),
                    keyboardActions = keyboardActions,
                    interactionSource = interactionSource,
                    visualTransformation = when {
                        validatorType.isPasswordTypeField() && !isPasswordShown -> PasswordVisualTransformation()
                        else -> VisualTransformation.None
                    },
                    decorationBox = { innerTextField ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
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
                                // Hint
                                if (value.text.isEmpty()) {
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
                                        contentDescription = "Toggle Password Visibility",
                                        tint = currentIconColor
                                    )
                                }
                            } else if (trailingIcon != null) {
                                Box(modifier = Modifier.padding(start = 8.dp)) { trailingIcon() }
                            }
                        }
                    }
                )

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
    // We use a simplified theme wrapper or just rely on the hardcoded colors in your component
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        // 1. Standard Email Field (Empty)
        AppTextField(
            title = "Email",
            hint = "Enter your email address",
            validatorType = ValidatorType.Email,
            value = TextFieldValue(""),
            onValueChange = {}
        )

        // 2. Phone Field (Shows the static +91 dropdown)
        var phoneValue by remember { mutableStateOf(TextFieldValue("")) }
        AppTextField(
            title = "Phone Number",
            hint = "1234567890",
            validatorType = ValidatorType.Phone,
            value = phoneValue,
            onValueChange = { phoneValue = it }
        )

        // 3. Password Field (With dummy text to show dots)
        var passwordValue by remember { mutableStateOf(TextFieldValue("secret123")) }
        AppTextField(
            title = "Password",
            hint = "Enter your password",
            validatorType = ValidatorType.Password,
            value = passwordValue,
            onValueChange = { passwordValue = it },
//            // Make sure these match your actual drawable names!
//            showPasswordIcon = R.drawable.ic_eye_open,
//            hidePasswordIcon = R.drawable.ic_eye_closed
        )

        // 4. Forced Error State
        AppTextField(
            title = "Forced Error State",
            hint = "Email Address",
            validatorType = ValidatorType.Email,
            value = TextFieldValue("invalid-email-format"),
            onValueChange = {},
            showErrorMessage = true // 🚀 Forces the error message to expand
        )
    }
}
