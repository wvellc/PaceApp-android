package net.paceapp.features.main.notifications

import net.paceapp.features.main.notifications.models.NotificationUI
import com.wvelabs.core_ui.base.ViewEvent
import com.wvelabs.core_ui.base.ViewSideEffect
import com.wvelabs.core_ui.base.ViewState

class NotificationsContract {
    data class State(
        val isInitialized: Boolean = false,
        val isLoading: Boolean = true,
        val notifications: List<NotificationUI> = emptyList(),
    ) : ViewState

    sealed class Event : ViewEvent {


        data object Init : Event()
        data object OnBackClick : Event()
        data object OnClearAllClick : Event()
        data object OnClearAllNotifications : Event()

        data class OnDeleteClick(val notification: NotificationUI) : Event()
    }

    sealed class Effect : ViewSideEffect {
        data object NavigateBack : Effect()
        data object ShowClearAllAlert : Effect()
    }
}
