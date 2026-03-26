package com.example.paceapp.features.splash

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

// App-specific base classes and managers
import com.example.paceapp.core.base.BaseViewModel
import com.example.paceapp.session.AppSessionManager

// Screen imports
import com.example.paceapp.features.splash.SplashContract.Effect
import com.example.paceapp.features.splash.SplashContract.Event
import com.example.paceapp.features.splash.SplashContract.State

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val sessionManager: AppSessionManager
) : BaseViewModel<State, Event, Effect>() {

    override fun setInitialState() = State()

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.Init -> initData()
        }
    }

    private fun initData() {
        if (state.value.isInitialized) return

        // TODO: Initialization logic here

        setState { copy(isInitialized = true) }
    }
}
