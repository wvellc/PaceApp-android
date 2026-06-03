package net.paceapp.features.main.eventmap.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.opacity
import kotlinx.coroutines.delay
import net.paceapp.R
import net.paceapp.core.components.AppBaseScreen
import net.paceapp.core.components.CommonAppBar
import net.paceapp.features.main.eventmap.EventMapContract.Event
import net.paceapp.features.main.eventmap.EventMapContract.State
import net.paceapp.theme.AppColors


@Composable
internal fun EventMapContent(
    state: State,
    onEvent: (Event) -> Unit
) {
    val backdrop = rememberLayerBackdrop {
        drawContent()
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Black, Color.Transparent),
                startY = 0f,
                endY = size.height
            ),
            blendMode = BlendMode.DstIn
        )
    }

    // Manage Camera State
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            state.startLocation ?: LatLng(0.0, 0.0),
            2f
        )
    }

    // Auto-animate camera to start location once route points load
    LaunchedEffect(state.startLocation) {
        state.startLocation?.let { startPoint ->
            delay(200)
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(startPoint, 15f),
                durationMs = 1000
            )
        }
    }

    AppBaseScreen(
        modifier = Modifier
            .fillMaxSize(),
        isLoading = state.isLoading,
        appBar = {
            //App bar
            CommonAppBar(
                title = stringResource(R.string.event_details),
                hasLightBackground = true,
                onBackClick = {
                    onEvent(Event.OnBackClick)
                },
            )
        },
    ) { innerPaddings ->
        Box(modifier = Modifier.fillMaxSize()) {

            GoogleMap(
                modifier = Modifier
                    .fillMaxSize()
                    .layerBackdrop(backdrop),
                cameraPositionState = cameraPositionState,
                contentPadding = innerPaddings,
                properties = MapProperties(
                    isMyLocationEnabled = false, // Set true if you have location permissions
                    isBuildingEnabled = true
                ),
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    compassEnabled = true
                ),
                onMapLoaded = {
                    onEvent(Event.OnMapLoaded)
                },
                onMapClick = { latLng ->
                    onEvent(Event.OnMarkerClicked(latLng))
                }
            ) {
                // Render the Polyline only if we have points
                if (state.routePoints.isNotEmpty()) {
                    Polyline(
                        points = state.routePoints,
                        color = AppColors.NeonAquaBlue,
                        width = 12f,
                        geodesic = true,
                        zIndex = 1f
                    )
                }
            }


            //App bar blur
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(innerPaddings.calculateTopPadding())
                    .align(Alignment.TopCenter)
                    .graphicsLayer {
                        compositingStrategy = CompositingStrategy.Offscreen
                    }
                    .drawBackdrop(
                        backdrop = backdrop,
                        shape = { RectangleShape },
                        highlight = null,
                        shadow = null,
                        effects = {
                            opacity(0.8f)
                            blur(4.dp.toPx())
                        },
                        onDrawSurface = {},
                        onDrawFront = {}
                    )
            )
        }
    }
}
