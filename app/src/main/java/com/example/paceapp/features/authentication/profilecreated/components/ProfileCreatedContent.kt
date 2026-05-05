package com.example.paceapp.features.authentication.profilecreated.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.paceapp.R
import com.example.paceapp.core.components.AppBaseScreen
import com.example.paceapp.core.components.AppSuccessView
import com.example.paceapp.features.authentication.profilecreated.ProfileCreatedContract.Event
import com.example.paceapp.features.authentication.profilecreated.ProfileCreatedContract.State

@Composable
internal fun ProfileCreatedContent(
    state: State,
    onEvent: (Event) -> Unit
) {
    AppBaseScreen(
        modifier = Modifier.fillMaxSize(),
        isLoading = state.isLoading,
        hasPattern = true,
    ) { innerPaddings ->
        AppSuccessView(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPaddings),
            title = stringResource(R.string.profile_created_title),
            subtitle = stringResource(R.string.profile_created_subtitle),
            logoRes = R.drawable.ic_profile_created,
            buttonLabel = stringResource(R.string.get_started),
            onButtonClick = {
                onEvent(Event.OnGetStartedClicked)
            }
        )
    }
}
