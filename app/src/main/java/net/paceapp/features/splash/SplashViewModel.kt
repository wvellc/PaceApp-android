package net.paceapp.features.splash

// App-specific base classes and managers

// Screen imports
import android.content.Context
import androidx.lifecycle.viewModelScope
import net.paceapp.core.base.BaseViewModel
import net.paceapp.core.enums.AuthDestination
import net.paceapp.core.domain.usecases.AuthRouteManager
import net.paceapp.core.utils.AppConstants
import net.paceapp.features.splash.SplashContract.Effect
import net.paceapp.features.splash.SplashContract.Event
import net.paceapp.features.splash.SplashContract.State
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
