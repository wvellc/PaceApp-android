package com.example.paceapp.features.authentication.buildprofile.steps

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paceapp.R
import com.example.paceapp.core.components.AppTextField
import com.example.paceapp.core.components.CustomProfileImage
import com.example.paceapp.core.components.ValidatorType
import com.example.paceapp.core.extensions.verticalScrollOnIme
import com.example.paceapp.theme.AppColors
import com.example.paceapp.theme.AppTheme

@Composable
fun ConnectStravaContent(
    stravaFieldState: TextFieldState,
) {
    val stravaFocus = remember { FocusRequester() }
    //Scroll inner content on Ime
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScrollOnIme(scrollState, delayMs = 200),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))
        //Description
        Text(
            text = stringResource(R.string.connect_strava_step_message),
            style = AppTheme.typography.medium.copy(
                color = AppColors.White,
                fontSize = 20.sp,
                lineHeight = 32.sp
            )
        )
        Spacer(Modifier.height(64.dp))
        //Strava logo
        CustomProfileImage(
            imageUrl = null,
            backgroundColor = AppColors.Transparent,
            shape = RoundedCornerShape(
                topStart = 72.dp,
                topEnd = 72.dp,
                bottomEnd = 16.dp,
                bottomStart = 16.dp
            ),
            placeholder = painterResource(id = R.drawable.ic_strava_logo),
            isClickable = false, // <-- This ensures the camera icon and click action are both disabled
            showLabel = false
        )
        //Strava link
        AppTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 42.dp),
            state = stravaFieldState,
            hint = "strava.com/athletes/12345678",
            borderColor = AppColors.White,
            leadingIcon = {
                Image(
                    painterResource(R.drawable.ic_sync),
                    contentDescription = null,
                )
            },
            validatorType = ValidatorType.Text,
            imeAction = ImeAction.Done,
            showErrorMessage = false,
            unfocusedBorderWidth = 1.2.dp,
            focusedBorderWidth = 1.2.dp,
            capitalization = KeyboardCapitalization.None,
            focusRequester = stravaFocus,
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}