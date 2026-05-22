package net.paceapp.features.main.editprofile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import net.paceapp.R
import net.paceapp.core.components.AppBaseScreen
import net.paceapp.core.components.AppButton
import net.paceapp.core.components.AppTextField
import net.paceapp.core.components.CommonAppBar
import net.paceapp.core.components.ValidatorType
import net.paceapp.core.extensions.clearFocusOnTap
import net.paceapp.core.extensions.verticalScrollOnIme
import net.paceapp.features.main.editprofile.EditProfileContract.Event
import net.paceapp.features.main.editprofile.EditProfileContract.State
import net.paceapp.theme.AppColors
import net.paceapp.theme.AppTheme

@Composable
internal fun EditProfileContent(
    state: State,
    onEvent: (Event) -> Unit
) {
    val focusManager = LocalFocusManager.current

    val firstNameFocus = remember { FocusRequester() }
    val lastNameFocus = remember { FocusRequester() }


    val leadingIcon: @Composable () -> Unit = {
        Icon(
            painter = painterResource(R.drawable.ic_name),
            contentDescription = null,
            tint = AppColors.White
        )
    }
    //Screen
    AppBaseScreen(
        modifier = Modifier
            .fillMaxSize()
            .clearFocusOnTap(focusManager),
        isLoading = state.isLoading,
        hasPattern = true,
        appBar = {
            //App bar
            CommonAppBar(
                title = stringResource(R.string.edit_profile),
                onBackClick = {
                    onEvent(Event.OnBackClick)
                },
            )
        },
    ) { innerPaddings ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScrollOnIme()
                .padding(top = innerPaddings.calculateTopPadding())
                .padding(horizontal = AppTheme.screenPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                //First name
                AppTextField(
                    state = state.firstNameState,
                    hint = stringResource(R.string.first_name),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    leadingIcon = { leadingIcon() },
                    validatorType = ValidatorType.Name,
                    imeAction = ImeAction.Next,
                    showErrorMessage = true,
                    unfocusedBorderWidth = 1.2.dp,
                    focusedBorderWidth = 1.2.dp,
                    capitalization = KeyboardCapitalization.Words,
                    focusRequester = firstNameFocus,
                )


                //Last name
                AppTextField(
                    state = state.lastNameState,
                    hint = stringResource(R.string.last_name),
                    leadingIcon = { leadingIcon() },
                    modifier = Modifier.fillMaxWidth(),
                    validatorType = ValidatorType.Name,
                    imeAction = ImeAction.Done,
                    showErrorMessage = true,
                    unfocusedBorderWidth = 1.2.dp,
                    focusedBorderWidth = 1.2.dp,
                    capitalization = KeyboardCapitalization.Words,
                    focusRequester = lastNameFocus,
                )
            }

            //Next button
            AppButton(
                enabled = state.isNextButtonEnabled,
                title = stringResource(R.string.update_profile),
                modifier = Modifier
                    .fillMaxWidth()
                    .safeContentPadding()
                    .padding(bottom = AppTheme.screenPadding),
                onClick = {
                    focusManager.clearFocus(force = true)
                    onEvent(Event.OnUpdateProfileClick)
                }
            )
        }
    }
}
