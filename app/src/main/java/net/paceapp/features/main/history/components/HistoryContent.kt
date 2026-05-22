package net.paceapp.features.main.history.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.paceapp.R
import net.paceapp.core.components.AppBaseScreen
import net.paceapp.core.components.CommonAppBar
import net.paceapp.core.components.NoDataView
import net.paceapp.core.components.SearchTextField
import net.paceapp.core.utils.AppConstants
import net.paceapp.features.main.history.HistoryContract.Event
import net.paceapp.features.main.history.HistoryContract.State
import net.paceapp.theme.AppColors
import net.paceapp.theme.AppTheme
import com.wvelabs.core_ui.components.SwipeDirection
import com.wvelabs.core_ui.components.SwipeToActionBox
import com.wvelabs.core_ui.components.rememberSwipeActionState
import com.wvelabs.core_ui.extensions.defaultAnimSpec

@Composable
internal fun HistoryContent(
    state: State,
    onEvent: (Event) -> Unit
) {

    val historyListState = rememberLazyListState()
    var isFilterVisible by remember { mutableStateOf(false) }
    val historySwipeState = rememberSwipeActionState()
    AppBaseScreen(
        modifier = Modifier
            .fillMaxSize(),
        isLoading = state.isLoading,
        hasPattern = true,
        appBar = {
            //App bar
            CommonAppBar(
                showBackButton = false,
                showAppLogo = true,
                onBackClick = {
                    onEvent(Event.OnBackClick)
                },
            )
        }
    ) { innerPaddings ->
        //Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPaddings.calculateTopPadding())
                .padding(horizontal = AppTheme.screenPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            //Search field
            SearchTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                state = state.searchTextState,
                trailingIcon = {
                    BadgedFilterIcon(
                        modifier = Modifier,
                        hasActiveFilters = state.activeFilter != null,
                        onClick = {
                            isFilterVisible = true
                        }
                    )
                }
            )

            Crossfade(
                targetState = state.historyList.isEmpty() && !state.isLoading,
                animationSpec = defaultAnimSpec(duration = 300)
            ) { hasNoData ->
                if (hasNoData) {
                    //No Data

                    NoDataView(
                        title = stringResource(R.string.no_history_title),
                        imageRes = R.drawable.ic_empty_history,
                        imageShape = CircleShape,
                        onImageClick = {
                            AppConstants.showComingSoonDialog()
                        }
                    )
                } else {
                    //History list
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        state = historyListState,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(
                            top = AppTheme.screenPadding,
                            bottom = AppTheme.bottomNavBarPadding + AppTheme.screenPadding
                        )
                    ) {
                        items(state.historyList, key = { it.id }) { history ->
                            //Swipe to action item
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
                                        onEvent(Event.OnDuplicateHistoryClick(history))
                                        historySwipeState.closeAll()
                                    }

                                    //Delete button
                                    SwipeActionButtons(id = R.drawable.ic_delete) {
                                        onEvent(Event.OnDeleteHistoryClick(history))
                                        historySwipeState.closeAll()
                                    }

                                },
                                content = {
                                    //History item UI
                                    HistoryItem(history = history, onClick = {
                                        onEvent(Event.OnHistoryClick(history))
                                    })
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    //Filter sheet
    FilterBottomSheet(
        isVisible = isFilterVisible,
        initialFilter = state.activeFilter, // Pass current state down
        onApplyFilter = { filter ->
            onEvent(Event.OnFilterChange(filter))
            isFilterVisible = false
        },
        onDismiss = { isFilterVisible = false },
    )

}


@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun HistoryPreview() = HistoryContent(
    state = State()
) {

}

