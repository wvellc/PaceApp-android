package com.example.paceapp.features.main.profile.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.paceapp.R
import com.example.paceapp.core.components.AppBaseScreen
import com.example.paceapp.core.components.CommonAppBar
import com.example.paceapp.core.components.CustomProfileImage
import com.example.paceapp.core.extensions.defaultClickable
import com.example.paceapp.core.extensions.verticalScrollOnIme
import com.example.paceapp.features.main.profile.ProfileContract.Event
import com.example.paceapp.features.main.profile.ProfileContract.State

@Composable
internal fun ProfileContent(
    state: State,
    onEvent: (Event) -> Unit
) {
    AppBaseScreen(
        modifier = Modifier
            .fillMaxSize(),
        isLoading = state.isLoading,
        hasPattern = true,
        appBar = {
            CommonAppBar(
                showBackButton = false,
                showAppLogo = true,
                onBackClick = {
                    onEvent(Event.OnBackClick)
                },

                actions = {
                    Image(
                        painter = painterResource(R.drawable.ic_settings),
                        contentDescription = stringResource(R.string.back),
                        modifier = Modifier
                            .clip(CircleShape)
                            .defaultClickable(onClick = {
                                onEvent(Event.OnSettingClick)
                            })
                    )
                }
            )
        }
    ) { innerPaddings ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPaddings)
                .verticalScrollOnIme(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))
            //Profile Image
            CustomProfileImage(
                imageUrl = "",
                isClickable = false,
                showCameraIcon = false,
                showLabel = false,
            )

            Text(
                ""
            )
        }
    }
}
