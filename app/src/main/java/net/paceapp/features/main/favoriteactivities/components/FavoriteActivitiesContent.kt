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
import com.wvelabs.core_ui.extensions.defaultAnimSpec
import net.paceapp.R
import net.paceapp.core.components.ActivityListItem
import net.paceapp.core.components.AppBaseScreen
import net.paceapp.core.components.CommonAppBar
import net.paceapp.core.components.NoDataView
import net.paceapp.features.main.favoriteactivities.FavoriteActivitiesContract.Event
import net.paceapp.features.main.favoriteactivities.FavoriteActivitiesContract.State
import net.paceapp.theme.AppTheme

@Composable
internal fun FavoriteActivitiesContent(
    state: State,
    onEvent: (Event) -> Unit
) {
    val historyListState = rememberLazyListState()

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
                        /*//Swipe to action item
                        SwipeToActionBox(
                            actionModifier = Modifier.padding(start = 16.dp),
                            direction = SwipeDirection.EndToStart,
                            actionBackgroundColor = AppColors.Transparent,
                            itemId = history.id,
                            state = historySwipeState,
                            actions = {
                                //Duplicate button
                                SwipeActionButtons(
                                    id = R.drawable.ic_duplicate,
                                    background = AppColors.NeonAquaBlue,
                                ) {
                                    onEvent(HistoryContract.Event.OnDuplicateHistoryClick(history))
                                    historySwipeState.closeAll()
                                }

                                //Delete button
                                SwipeActionButtons(id = R.drawable.ic_delete) {
                                    onEvent(HistoryContract.Event.OnDeleteHistoryClick(history))
                                    historySwipeState.closeAll()
                                }

                            },
                            content = {*/
                        //History item UI
                        ActivityListItem(history = activity, onClick = {
                            onEvent(Event.OnActivityClick(activity))
                        })
//                            }
//                        )
                    }
                }
            }
        }
    }
}
