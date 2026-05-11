package com.example.paceapp.features.main.history.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.paceapp.core.components.AppBaseScreen
import com.example.paceapp.core.components.CommonAppBar
import com.example.paceapp.core.components.SearchTextField
import com.example.paceapp.features.main.history.HistoryContract.Event
import com.example.paceapp.features.main.history.HistoryContract.State
import com.example.paceapp.theme.AppTheme

@Composable
internal fun HistoryContent(
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
                .padding(horizontal = AppTheme.screenPadding)
                .imePadding(),
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
                        hasActiveFilters = state.hasFilterApplied,
                        onClick = {
                            onEvent(Event.OnFilterClick)
                        }
                    )
                }
            )

            //History list
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                state = historyListState,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(
                    top = 16.dp,
                    bottom = AppTheme.bottomNavBarPadding + AppTheme.screenPadding
                )
            ) {
                items(state.historyList, key = { it.id }) { history ->
                    HistoryItem(history = history) {

                    }
                }
            }
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun HistoryPreview() = HistoryContent(
    state = State()
) { }

