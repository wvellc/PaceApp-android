package com.example.paceapp.features.main.notifications

import androidx.lifecycle.viewModelScope
import com.example.paceapp.core.base.BaseViewModel
import com.example.paceapp.features.main.notifications.NotificationsContract.Effect
import com.example.paceapp.features.main.notifications.NotificationsContract.Event
import com.example.paceapp.features.main.notifications.NotificationsContract.State
import com.example.paceapp.features.main.notifications.data.NotificationDummyData
import com.example.paceapp.features.main.notifications.domain.usecases.MapNotificationsToUiUseCase
import com.example.paceapp.features.main.notifications.models.NotificationUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val mapNotificationsUseCase: MapNotificationsToUiUseCase,
) : BaseViewModel<State, Event, Effect>() {

    override fun setInitialState() = State()

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.Init -> initData()
            is Event.OnBackClick -> {
                setEffect { Effect.NavigateBack }
            }

            is Event.OnDeleteClick -> handleOnDeleteClick(event.notification)
            is Event.OnClearAllClick -> handleOnClearAllClick()
            is Event.OnClearAllNotifications -> clearAllNotifications()
        }
    }


    private fun initData() {
        if (currentState.isInitialized) return
        fetchNotifications()
        setState { copy(isInitialized = true) }
    }

    private fun fetchNotifications() {
        viewModelScope.launch {
            val notifications =
                mapNotificationsUseCase(NotificationDummyData.getDummyNotifications())
            delay(500)//dummy delay
            setState { copy(isLoading = false, notifications = notifications) }
        }
    }

    private fun handleOnDeleteClick(notification: NotificationUI) {
        setState { copy(notifications = notifications - notification) }
    }

    private fun handleOnClearAllClick() {
        setEffect { Effect.ShowClearAllAlert }
    }

    private fun clearAllNotifications() {
        setState { copy(notifications = emptyList()) }
    }

}
