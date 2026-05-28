package net.paceapp.features.main.eventdetails.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.wvelabs.core_ui.components.LiquidGlassButton
import net.paceapp.R
import net.paceapp.core.components.AppBaseScreen
import net.paceapp.core.components.CommonAppBar
import net.paceapp.features.main.eventdetails.EventDetailsContract.Event
import net.paceapp.features.main.eventdetails.EventDetailsContract.State
import net.paceapp.theme.AppColors

@Composable
internal fun EventDetailsContent(
    state: State,
    onEvent: (Event) -> Unit
) {
    val backdrop = rememberLayerBackdrop()

    val animatedTint by animateColorAsState(
        targetValue = when {
            state.isFavorite -> AppColors.FluorescentMint
            else -> AppColors.HintGray
        }
    )
    AppBaseScreen(
        modifier = Modifier
            .fillMaxSize(),
        isLoading = state.isLoading,
        hasPattern = true,
        appBar = {
            //App bar
            CommonAppBar(
                title = stringResource(R.string.new_event),
                onBackClick = {
                    onEvent(Event.OnBackClick)
                },
                actions = {
                    LiquidGlassButton(
                        modifier = Modifier.size(32.dp),
                        shape = CircleShape,
                        onClick = { onEvent(Event.OnFavoriteToggle) },
                        backdrop = backdrop,
                        maxScale = 1.2f
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_favorites),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = animatedTint
                        )
                    }
                }
            )
        },
    ) { innerPaddings ->
        Text(text = "EventDetails Screen")
    }
}
