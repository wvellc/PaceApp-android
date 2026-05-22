package net.paceapp.features.main.createevent

import androidx.lifecycle.viewModelScope
import net.paceapp.core.base.BaseViewModel
import net.paceapp.core.enums.DistanceUnits
import net.paceapp.features.main.createevent.CreateEventContract.Effect
import net.paceapp.features.main.createevent.CreateEventContract.Event
import net.paceapp.features.main.createevent.CreateEventContract.State
import net.paceapp.session.AppSessionManager
import com.wvelabs.core_ui.utils.DateTimeHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateEventViewModel @Inject constructor(
    private val sessionManager: AppSessionManager
) : BaseViewModel<State, Event, Effect>() {

    override fun setInitialState() = State()

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.Init -> initData()
            is Event.OnBackClick -> {
                setEffect { Effect.NavigateBack }
            }

            is Event.OnDateSelected -> handleDateSelected(event.millis)
        }
    }


    private fun initData() {
        if (currentState.isInitialized) return
        getDistanceUnits()
        setState { copy(isInitialized = true) }
    }

    private fun getDistanceUnits() {
        viewModelScope.launch {
            val userDetails = sessionManager.getUserDetails()
            val units = userDetails?.distanceUnits ?: DistanceUnits.MILES
            setState { copy(distanceUnits = units) }
        }
    }

    private fun handleDateSelected(millis: Long?) {
        setState {
            copy(
                selectedDate = DateTimeHelper.getLocalDateTime(millis ?: 0L),
            )
        }
    }
}
