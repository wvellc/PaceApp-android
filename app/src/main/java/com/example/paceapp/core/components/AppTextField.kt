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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.paceapp.R
import com.example.paceapp.core.extensions.defaultClickable
import com.example.paceapp.ui.theme.AppColors
import com.example.paceapp.ui.theme.AppTheme
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
    unfocusedBorderWidth: Dp = 1.2.dp,
    focusedBorderWidth: Dp = 2.dp,

    // Simplified Error State
    showErrorMessage: Boolean = false, // Parent can force an error state
    errorTextStyle: TextStyle = AppTheme.typography.size14.copy(fontWeight = FontWeight.Medium),

    @DimenRes showPasswordIcon: Int? = null,
    @DimenRes hidePasswordIcon: Int? = null,

    title: String? = null,
    titleSpacing: Dp = 0.dp,
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
    val currentBorderWidth = if (isFocused && enabled && !isError) focusedBorderWidth else unfocusedBorderWidth
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
            modifier = Modifier.fillMaxWidth().background(color = Color.Transparent),
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
                    textStyle = textStyle.copy(color = if (enabled) AppColors.White else AppColors.FashionGray),
                    cursorBrush = SolidColor(borderColor),
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
                                        color = AppColors.HintGray
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
/*

import androidx.annotation.DimenRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.paceapp.R
import com.example.paceapp.core.extensions.defaultClickable
import com.example.paceapp.ui.theme.AppColors
import com.example.paceapp.ui.theme.AppTheme
import com.kyant.capsule.ContinuousRoundedRectangle

@Composable
fun AppTextField(
    modifier: Modifier,
    hint: String,
    focusRequester: FocusRequester = remember { FocusRequester() },
    borderShape: Shape = ContinuousRoundedRectangle(12.dp),
    value: TextFieldValue = TextFieldValue(),
    onValueChange: (TextFieldValue) -> Unit = {},
    trailingIcon: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    oldPassword: TextFieldValue? = null,
    titlePadding: Dp = 0.dp,
    validatorType: ValidatorType = ValidatorType.None,
    imeAction: ImeAction = ImeAction.Unspecified,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    capitalization: KeyboardCapitalization = KeyboardCapitalization.None,
    keyboardActions: KeyboardActions = KeyboardActions(),
    title: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    maxLines: Int = 1,
    minLines: Int = 1,
    textStyle: TextStyle = AppTheme.typography.size18.copy(
        fontWeight = FontWeight.Medium
    ),
    borderColor: Color = AppColors.White,
    iconColor: Color = AppColors.HintGray,
    @DimenRes showPasswordIcon: Int? = null,
    @DimenRes hidePasswordIcon: Int? = null
) {
    var isError by remember { mutableStateOf(false) }
    var isPasswordShown by remember { mutableStateOf(false) }
    val keyboard = LocalSoftwareKeyboardController.current

    val colors = OutlinedTextFieldDefaults.colors(
        //Focused
        focusedContainerColor = AppColors.Transparent,
        focusedBorderColor = borderColor,
        focusedLeadingIconColor = iconColor,
        focusedTrailingIconColor = iconColor,

        //Unfocused
        unfocusedContainerColor = AppColors.Transparent,
        unfocusedBorderColor = borderColor,
        unfocusedLeadingIconColor = iconColor,
        unfocusedTrailingIconColor = iconColor,

        //Disabled
        disabledContainerColor = AppColors.Transparent,
        disabledBorderColor = AppColors.FashionGray,
        disabledLeadingIconColor = iconColor,
        disabledTrailingIconColor = iconColor,

        //Error
        errorContainerColor = AppColors.Transparent,
        errorBorderColor = AppColors.Error,
        errorLeadingIconColor = iconColor,
        errorTrailingIconColor = iconColor,
    )
    //Keyboard type
    val keyboardType = when (validatorType) {
        ValidatorType.Email -> KeyboardType.Email
        ValidatorType.Phone -> KeyboardType.Phone
        ValidatorType.Password -> KeyboardType.Password
        ValidatorType.ConfirmPassword -> KeyboardType.Password
        ValidatorType.Name -> KeyboardType.Text
        ValidatorType.Text -> KeyboardType.Text
        ValidatorType.Number -> KeyboardType.Decimal
        ValidatorType.None -> KeyboardType.Text
    }


    val eyeIcon: @Composable () -> Unit = {
        if (showPasswordIcon != null && hidePasswordIcon != null) {
            IconButton(
                onClick = { isPasswordShown = !isPasswordShown }
            ) {
                Icon(
                    painter = painterResource(
                        when (isPasswordShown) {
                            true -> showPasswordIcon
                            false -> hidePasswordIcon
                        }
                    ),
                    contentDescription = null,
                )
            }
        }
    }


    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .background(color = Color.Transparent),
    ) {
        if (validatorType == ValidatorType.Phone) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(7.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .height(48.dp)
                    .clip(borderShape)
                    .border(
                        width = 1.2.dp,
                        color = borderColor,
                        shape = borderShape
                    )
                    .defaultClickable(
                        rippleColor = AppColors.White20,
                        onClick = {}
                    )
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    "+91",
                    style = textStyle,
                    color = AppColors.White,
                )
                Image(
                    painter = painterResource(R.drawable.ic_down_arrow),
                    contentDescription = null,
                )
            }
        }
        // Text Field
        OutlinedTextField(

            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .focusRequester(focusRequester)
                .onFocusEvent {
                    //Check confirm password
                    if (validatorType == ValidatorType.ConfirmPassword) {
                        val hasError = Validator.validate(
                            value.text,
                            validatorType
                        ) != null

                        isError = when {
                            hasError || value.text.isNotBlank() -> oldPassword?.text != value.text
                            else -> false
                        }
                    }
                    if (it.isFocused && isError) {
                        keyboard?.show()
                    }
                },
            enabled = enabled,
            readOnly = readOnly,
            value = value,
            singleLine = maxLines <= 1,
            maxLines = maxLines,
            minLines = minLines,
            visualTransformation = when {
                validatorType.isPasswordTypeField() && !isPasswordShown -> PasswordVisualTransformation()
                else -> VisualTransformation.None
            },
            onValueChange = {
                //If this is confirm password field then compare it to oldPassword Field
                isError = if (validatorType == ValidatorType.ConfirmPassword) {
                    Validator.validate(
                        it.text,
                        validatorType
                    ) != null || oldPassword?.text != it.text
                } else {
                    Validator.validate(it.text, validatorType) != null
                }
                onValueChange.invoke(it)
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction,
                capitalization = capitalization,
            ),

            keyboardActions = keyboardActions,
            isError = isError,
            colors = colors,
            interactionSource = interactionSource,
            shape = borderShape,
            leadingIcon = leadingIcon,
            trailingIcon = if (validatorType.isPasswordTypeField()) eyeIcon else trailingIcon,
            placeholder = {
                Text(
                    hint,
                    style = textStyle,
                    color = AppColors.RadiantBlue,
                )
            },
            textStyle = textStyle,

        )
    }
}
*/
