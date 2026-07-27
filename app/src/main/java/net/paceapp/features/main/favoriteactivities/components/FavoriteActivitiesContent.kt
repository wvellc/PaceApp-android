package net.paceapp.features.main.favoriteactivities.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.wvelabs.core_ui.components.SwipeDirection
import com.wvelabs.core_ui.components.SwipeToActionBox
import com.wvelabs.core_ui.components.rememberSwipeActionState
import com.wvelabs.core_ui.extensions.defaultAnimSpec
import net.paceapp.R
import net.paceapp.core.components.ActivityListItem
import net.paceapp.core.components.AppBaseScreen
import net.paceapp.core.components.CommonAppBar
import net.paceapp.core.components.NoDataView
import net.paceapp.features.main.favoriteactivities.FavoriteActivitiesContract.Event
import net.paceapp.features.main.favoriteactivities.FavoriteActivitiesContract.State
import net.paceapp.features.main.history.components.SwipeActionButtons
import net.paceapp.theme.AppColors
import net.paceapp.theme.AppTheme

@Composable
internal fun FavoriteActivitiesContent(
    state: State,
    onEvent: (Event) -> Unit
) {
    val historyListState = rememberLazyListState()
    val swipeState = rememberSwipeActionState()

    AppBaseScreen(
        modifier = Modifier
            .fillMaxSize(),
        isLoading = state.isLoading,
        hasPattern = true,
        appBar = {
            //App bar
            CommonAppBar(
                title = stringResource(R.string.favorites),
                onBackClick = {
                    onEvent(
                        Event.OnBackClick
                    )
                },
            )
        }
    ) { innerPaddings ->
        Crossfade(
            modifier = Modifier.fillMaxSize(),
            targetState = state.favorites.isEmpty() && !state.isLoading,
            animationSpec = defaultAnimSpec(duration = 300)
        ) { hasNoData ->
            if (hasNoData) {
                //No Data

                NoDataView(
                    title = stringResource(R.string.no_favorites_title),
                )
            } else {
                //History list
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPaddings),
                    state = historyListState,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(AppTheme.screenPadding)
                ) {
                    items(state.favorites, key = { it.id }) { activity ->
                        //Swipe left to un-favorite (single destructive action, mirrors iOS).
                        SwipeToActionBox(
                            actionModifier = Modifier.padding(start = 16.dp),
                            direction = SwipeDirection.EndToStart,
                            actionBackgroundColor = AppColors.Transparent,
                            itemId = activity.id,
                            state = swipeState,
                            actions = {
                                SwipeActionButtons(
                                    id = R.drawable.ic_unfavorite,
                                    background = AppColors.Error,
                                ) {
                                    onEvent(Event.OnUnfavoriteClick(activity))
                                    swipeState.closeAll()
                                }
                            },
                            content = {
                                ActivityListItem(history = activity, onClick = {
                                    onEvent(Event.OnActivityClick(activity))
                                })
                            }
                        )
                    }
                }
            }
        }
    }
}
