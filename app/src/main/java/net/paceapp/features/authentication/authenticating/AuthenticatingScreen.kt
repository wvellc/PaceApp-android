package net.paceapp.features.authentication.authenticating

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.paceapp.R
import net.paceapp.core.components.AppBaseScreen
import net.paceapp.core.components.AppLoadingIndicator
import net.paceapp.theme.AppColors
import net.paceapp.theme.AppTheme

// Full-screen loading shown while an email-link sign-in completes (mirrors iOS
// AuthenticatingScreen). Navigation to/from it is driven by AppNavHost observing
// AuthManager.emailLinkPhase — this screen itself holds no state.
@Composable
fun AuthenticatingScreen() {
    AppBaseScreen(
        modifier = Modifier.fillMaxSize(),
        hasPattern = true,
    ) { innerPaddings ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPaddings),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            AppLoadingIndicator(
                isFullScreen = false,
                backgroundColor = AppColors.Transparent,
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.authenticating),
                style = AppTheme.typography.medium.copy(fontSize = 20.sp),
                color = AppColors.White,
            )
        }
    }
}
