package net.paceapp.features.main.tabhost

import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import net.paceapp.core.base.BaseViewModel
import net.paceapp.core.domain.repositories.UserRepository
import net.paceapp.core.garmin.GarminDeviceManager
import net.paceapp.features.main.tabhost.TabHostContract.Effect
import net.paceapp.features.main.tabhost.TabHostContract.Event
import net.paceapp.features.main.tabhost.TabHostContract.State
import javax.inject.Inject

@HiltViewModel
class TabHostViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val garminDeviceManager: GarminDeviceManager,
    @ApplicationContext appContext: Context,
) : BaseViewModel<State, Event, Effect>() {

    override fun setInitialState() = State()

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.Init -> initData()
        }
    }

    init {
        restoreGarminConnection(appContext)
    }

    private fun initData() {
        if (currentState.isInitialized) return
        setState { copy(isInitialized = true) }
    }


    fun restoreGarminConnection(context: Context) {
        viewModelScope.launch {
            val watchId = userRepository.getPairedWatchId()
            garminDeviceManager.restoreConnection(watchId, context)
        }
    }
}
