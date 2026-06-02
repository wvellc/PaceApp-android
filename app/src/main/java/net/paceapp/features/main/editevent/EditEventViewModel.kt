package net.paceapp.features.main.editevent

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.wvelabs.core_ui.alerts.MessageType
import dagger.hilt.android.lifecycle.HiltViewModel
import net.paceapp.R
import net.paceapp.core.base.BaseViewModel
import net.paceapp.core.components.Validator
import net.paceapp.core.components.ValidatorType
import net.paceapp.core.domain.usecases.ValidateEventUseCase
import net.paceapp.features.main.editevent.EditEventContract.Effect
import net.paceapp.features.main.editevent.EditEventContract.Event
import net.paceapp.features.main.editevent.EditEventContract.State
import net.paceapp.features.main.editevent.navigation.EditEventRoute
import javax.inject.Inject

@HiltViewModel
class EditEventViewModel @Inject constructor(
    private val validateEventUseCase: ValidateEventUseCase,
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel<State, Event, Effect>() {

    override fun setInitialState() = State()

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.Init -> initData()
            is Event.OnBackClick -> {
                setEffect { Effect.NavigateBack }
            }

            is Event.OnSaveButtonClick -> handleSaveButtonClick()
        }
    }


    private fun initData() {
        if (currentState.isInitialized) return

        val args = savedStateHandle.toRoute<EditEventRoute>()
        //Set argument data
        setState {
            copy(
                currentEventName = args.eventName,
                currentLocation = args.location,
            )
        }

        setState { copy(isInitialized = true) }
    }


    private fun handleSaveButtonClick() {
        val eventName = currentState.eventNameState.text.trim().toString()
        val location = currentState.locationState.text.trim().toString()
        val error = validateEventUseCase(eventName, location)
        if (error != null) {
            showToast(error, type = MessageType.Warning)
            return
        }
        //TODO:Save details
        setEffect { Effect.NavigateBack }
    }

}
