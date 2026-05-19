package com.example.paceapp.features.main.notifications.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.EaseInOutBack
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.paceapp.R
import com.example.paceapp.core.components.AppBaseScreen
import com.example.paceapp.core.components.AppLoadingIndicator
import com.example.paceapp.core.components.CommonAppBar
import com.example.paceapp.core.components.NoDataView
import com.example.paceapp.core.extensions.defaultClickable
import com.example.paceapp.features.main.notifications.NotificationsContract.Event
import com.example.paceapp.features.main.notifications.NotificationsContract.State
import com.example.paceapp.theme.AppColors
import com.example.paceapp.theme.AppTheme
import com.wvelabs.core_ui.components.SwipeDirection
import com.wvelabs.core_ui.components.SwipeToActionBox
import com.wvelabs.core_ui.components.rememberSwipeActionState
import com.wvelabs.core_ui.extensions.defaultAnimSpec

@Composable
internal fun NotificationsContent(
    state: State,
    onEvent: (Event) -> Unit
) {
    val notificationLisState = rememberLazyListState()
    val swipeState = rememberSwipeActionState()
    //Screen
    AppBaseScreen(
        modifier = Modifier
            .fillMaxSize(),
        isLoading = state.isLoading,
        hasPattern = true,
        appLoader = {
            AppLoadingIndicator(
                isFullScreen = false,
                backgroundColor = AppColors.Transparent
            )
        },
        appBar = {
            //App bar
            CommonAppBar(
                title = stringResource(R.string.notifications),
                onBackClick = {
                    onEvent(Event.OnBackClick)
                },
                actions = {
                    //Clear all icon
                    AnimatedVisibility(
                        visible = state.notifications.isNotEmpty(),
                        enter = fadeIn(),
                        exit = fadeOut()

                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_clear_all),
                            contentDescription = null,
                            modifier = Modifier
                                .clip(CircleShape)
                                .animateEnterExit(
                                    exit = scaleOut(),
                                    enter = scaleIn(
                                        initialScale = 0.4f,
                                        animationSpec = defaultAnimSpec(
                                            duration = 300,
                                            easing = EaseInOutBack
                                        )
                                    ),
                                )
                                .defaultClickable { onEvent(Event.OnClearAllClick) }
                        )
                    }

                }
            )
        },
    ) { innerPaddings ->

        Crossfade(
            targetState = state.notifications.isEmpty() && !state.isLoading,
            animationSpec = defaultAnimSpec(duration = 300)
        ) { hasNoData ->
            if (hasNoData) {
                //Empty view
                NoDataView(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPaddings)
                        .padding(top = AppTheme.screenPadding),
                    title = stringResource(R.string.no_notification_title),
                    subtitle = stringResource(R.string.no_notification_subtitle),
                    imageRes = R.drawable.ic_empty_notifications,
                    imageShape = CircleShape,
                )
            } else {
                //Notifications
                LazyColumn(
                    state = notificationLisState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = innerPaddings.calculateTopPadding()),
                    contentPadding = PaddingValues(
                        top = AppTheme.screenPadding,
                        start = AppTheme.screenPadding,
                        end = AppTheme.screenPadding,
                        bottom = AppTheme.screenPadding + innerPaddings.calculateTopPadding()
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.notifications, key = { it.id }) { model ->
                        //Swipeable action box
                        SwipeToActionBox(
                            swipeThreshold = 92.dp,
                            direction = SwipeDirection.EndToStart,
                            actionBackgroundColor = AppColors.InfernoRed,
                            actionShape = RoundedCornerShape(16.dp),
                            itemId = model.id,
                            state = swipeState,
                            actions = {
                                //Delete button
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .defaultClickable(rippleColor = AppColors.White20) {
                                            onEvent(Event.OnDeleteClick(model))
                                            swipeState.closeAll()//Close all swiped item
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(R.drawable.ic_delete),
                                        contentDescription = "Delete",
                                        contentScale = ContentScale.None,
                                    )
                                }
                            },
                            content = {
                                //Notification item UI
                                NotificationItem(modifier = Modifier.fillMaxWidth(), model = model)
                            }
                        )
                    }
                }
            }
        }
    }
}
