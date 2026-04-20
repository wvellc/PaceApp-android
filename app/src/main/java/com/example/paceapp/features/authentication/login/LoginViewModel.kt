package com.example.paceapp.features.authentication.login

// App-specific base classes and managers

// Screen imports
import com.example.paceapp.core.base.BaseViewModel
import com.example.paceapp.features.authentication.data.enums.LoginTypes
import com.example.paceapp.features.authentication.login.LoginContract.Effect
import com.example.paceapp.features.authentication.login.LoginContract.Event
import com.example.paceapp.features.authentication.login.LoginContract.State
import com.example.paceapp.session.AppSessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val sessionManager: AppSessionManager
) : BaseViewModel<State, Event, Effect>() {

    override fun setInitialState() = State()

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.Init -> initData()
            is Event.OnBackClicked -> {
                setEffect { Effect.NavigateBack }
            }

            is Event.OnLoginTypeSelected -> handleOnLoginTypeSelected(event.loginType)
        }
    }


    private fun initData() {
        if (state.value.isInitialized) return

        // TODO: Initialization logic here

        setState { copy(isInitialized = true) }
    }

    private fun handleOnLoginTypeSelected(loginType: LoginTypes) {
        setState { copy(selectedLoginType = loginType) }
    }
}
