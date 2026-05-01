package com.example.paceapp.features.splash

// App-specific base classes and managers

// Screen imports
import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.paceapp.core.base.BaseViewModel
import com.example.paceapp.core.domain.enums.AuthDestination
import com.example.paceapp.core.domain.usecases.AuthRouteManager
import com.example.paceapp.core.utils.AppConstants
import com.example.paceapp.features.splash.SplashContract.Effect
import com.example.paceapp.features.splash.SplashContract.Event
import com.example.paceapp.features.splash.SplashContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

const val WATCH_APP_ID = "bec1b23d90564b958370b9ded9266942"

@HiltViewModel
class SplashViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val authRouteManager: AuthRouteManager,
) : BaseViewModel<State, Event, Effect>() {

    override fun setInitialState() = State()

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.Init -> initData()
            is Event.OnGetStarted -> handleOnGetStartedClick()
        }
    }

    private fun initData() {
        if (currentState.isInitialized) return
        checkAuthentication()
        setState { copy(isInitialized = true) }
    }

    private fun checkAuthentication() {
        viewModelScope.launch {
            delay(AppConstants.SPLASH_DELAY)
            val destination = authRouteManager.getNextDestination()
            when (destination) {
                AuthDestination.TAB_HOST -> setEffect { Effect.NavigateToTabHost }
                AuthDestination.BUILD_PROFILE -> setEffect { Effect.NavigateToBuildProfile }
                AuthDestination.LOGIN -> setState { copy(showGetStarted = true) }
            }
        }
    }

    private fun handleOnGetStartedClick() {
        setEffect { Effect.NavigateToLogin }
    }
}
