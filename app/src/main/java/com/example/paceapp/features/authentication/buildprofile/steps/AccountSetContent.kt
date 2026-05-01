package com.example.paceapp.features.authentication.buildprofile.steps

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paceapp.R
import com.example.paceapp.core.components.AppTextField
import com.example.paceapp.core.components.ValidatorType
import com.example.paceapp.core.components.imagepicker.AppImagePicker
import com.example.paceapp.core.extensions.defaultClickable
import com.example.paceapp.core.extensions.verticalScrollOnIme
import com.example.paceapp.features.authentication.buildprofile.BuildProfileContract.Event
import com.example.paceapp.theme.AppColors
import com.example.paceapp.theme.AppTheme
import com.example.paceapp.theme.PaceAppTheme
import com.wvelabs.core_ui.components.AppNetworkImage

@Composable
fun AccountSetContent(
    profileImage: String?,
    firstNameState: TextFieldState,
    lastNameState: TextFieldState,
    onEvent: (Event) -> Unit,
) {

    var showPicker by remember { mutableStateOf(false) }
    val firstNameFocus = remember { FocusRequester() }
    val lastNameFocus = remember { FocusRequester() }
    val hasProfileImage = profileImage != null


    val leadingIcon: @Composable () -> Unit = {
        Icon(
            painter = painterResource(R.drawable.ic_name),
            contentDescription = null,
            tint = AppColors.White
        )
    }

    //Scroll inner content on Ime
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScrollOnIme(
                scrollState, delayMs = 200 //delay to sync with outer scroll
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.create_account_description),
            style = AppTheme.typography.size20.copy(
                color = AppColors.White,
                fontWeight = FontWeight.Medium,
                lineHeight = 32.sp
            )
        )

        Spacer(Modifier.height(32.dp))

        //Profile Image
        Box(
            modifier = Modifier
                .size(width = 100.dp, height = 108.dp)
                .clip(
                    shape = RoundedCornerShape(
                        topStart = 72.dp,
                        topEnd = 72.dp,
                        bottomEnd = 10.dp,
                        bottomStart = 10.dp
                    )
                )
                .background(color = AppColors.White)
                .defaultClickable { showPicker = true },
            contentAlignment = Alignment.Center,
        ) {


            //Profile image
            AppNetworkImage(
                imageUrl = profileImage,
                modifier = Modifier.fillMaxSize(),
            )

            //Camera icon
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = when {
                            hasProfileImage -> AppColors.Black.copy(alpha = 0.7f)
                            else -> AppColors.Transparent
                        }
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_camera),
                    modifier = Modifier,
                    contentDescription = null,
                )
            }
        }

        //Photo label
        Crossfade(
            targetState = hasProfileImage,
            animationSpec = tween(300),
            modifier = Modifier.padding(top = 16.dp),
        ) { hasImage ->
            Text(
                when {
                    hasImage -> stringResource(R.string.update_photo)
                    else -> stringResource(R.string.add_photo)
                },
                style = AppTheme.typography.size16.copy(
                    fontWeight = FontWeight.Medium,
                    color = AppColors.White
                )
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        //First name
        AppTextField(
            state = firstNameState,
            hint = stringResource(R.string.first_name),
            borderColor = AppColors.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 42.dp),
            leadingIcon = { leadingIcon() },
            validatorType = ValidatorType.Name,
            imeAction = ImeAction.Next,
            showErrorMessage = true,
            unfocusedBorderWidth = 1.2.dp,
            focusedBorderWidth = 1.2.dp,
            capitalization = KeyboardCapitalization.Words,
            focusRequester = firstNameFocus,
        )

        Spacer(modifier = Modifier.height(16.dp))
        //Last name
        AppTextField(
            state = lastNameState,
            hint = stringResource(R.string.last_name),
            leadingIcon = { leadingIcon() },
            borderColor = AppColors.White,
            modifier = Modifier.fillMaxWidth(),
            validatorType = ValidatorType.Name,
            imeAction = ImeAction.Done,
            showErrorMessage = true,
            unfocusedBorderWidth = 1.2.dp,
            focusedBorderWidth = 1.2.dp,
            capitalization = KeyboardCapitalization.Words,
            focusRequester = lastNameFocus,
        )
        Spacer(modifier = Modifier.height(24.dp))

        //Image picker
        AppImagePicker(
            isVisible = showPicker,
            showReplaceSheet = hasProfileImage,
            onDismiss = { showPicker = false },
            onAction = { action -> onEvent(Event.OnImagePickerAction(action)) },
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF235BFF)
@Composable
private fun AccountSetContentPreview() {
    PaceAppTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            AccountSetContent(
                profileImage = null,
                firstNameState = remember { TextFieldState() },
                lastNameState = remember { TextFieldState() },
                onEvent = {}
            )
        }
    }
}
