package com.example.paceapp.features.authentication.buildprofile.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paceapp.R
import com.example.paceapp.core.components.AppLogo
import com.example.paceapp.core.components.AppSegmentedButtons
import com.example.paceapp.core.components.AppTextField
import com.example.paceapp.core.components.LogoStyle
import com.example.paceapp.core.components.ValidatorType
import com.example.paceapp.core.domain.enums.GenderTypes
import com.example.paceapp.core.extensions.g2Continuity
import com.example.paceapp.core.extensions.titleRes
import com.example.paceapp.core.extensions.verticalScrollOnIme
import com.example.paceapp.features.authentication.buildprofile.BuildProfileContract.Event
import com.example.paceapp.theme.AppColors
import com.example.paceapp.theme.AppTheme
import com.example.paceapp.theme.PaceAppTheme
import com.kyant.capsule.ContinuousRoundedRectangle

@Composable
fun AccountSetContent(
    firstNameState: TextFieldState,
    lastNameState: TextFieldState,
    onEvent: (Event) -> Unit,
    gender: GenderTypes,
) {

    val firstNameFocus = remember { FocusRequester() }
    val lastNameFocus = remember { FocusRequester() }


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
    ) {
        Spacer(Modifier.height(72.dp))

        AppLogo(
            modifier = Modifier
                .fillMaxWidth(),
            imageSize = DpSize(136.dp, 91.dp),
            showLabel = false,
            logoStyle = LogoStyle.Vertical
        )
        Spacer(Modifier.height(64.dp))

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
        Text(
            text = stringResource(R.string.gender),
            style = AppTheme.typography.semiBold.copy(
                color = AppColors.White,
                fontSize = 16.sp,
                lineHeight = 20.sp
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        AppSegmentedButtons(
            modifier = Modifier
                .fillMaxWidth()
                .clip(ContinuousRoundedRectangle(12.dp, continuity = g2Continuity))
                .background(AppColors.White.copy(alpha = 0.1f))
                .padding(4.dp),
            segments = GenderTypes.entries,
            selectedSegment = gender,
            itemTitle = { stringResource(it.titleRes) },
            onSegmentSelected = { onEvent(Event.OnGenderSelected(it)) },
        )

    }
}

@Preview(showBackground = true, backgroundColor = 0xFF235BFF)
@Composable
private fun AccountSetContentPreview() {
    PaceAppTheme {
        Box(modifier = Modifier.padding(AppTheme.screenPadding)) {
            AccountSetContent(
                firstNameState = remember { TextFieldState() },
                lastNameState = remember { TextFieldState() },
                gender = GenderTypes.MALE,
                onEvent = {},
            )
        }
    }
}
