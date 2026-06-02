package net.paceapp.features.main.eventmap.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
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
    // Manage Camera State
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            state.startLocation ?: LatLng(0.0, 0.0),
            2f // Far zoom initially before data loads
        )
    }

    // Auto-animate camera to start location once route points load
    LaunchedEffect(state.startLocation) {
        state.startLocation?.let { startPoint ->
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(startPoint, 14f),
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
                onBackClick = {
                    onEvent(Event.OnBackClick)
                },
            )
        },
    ) { innerPaddings ->

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
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

                // Optional: You can also add Marker() here for Start/End points
            }
        }
    }
}
