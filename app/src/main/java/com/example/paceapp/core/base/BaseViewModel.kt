package com.example.paceapp.core.base

import com.example.paceapp.session.AppSessionManager
import com.wvelabs.core_ui.base.CoreViewModel
import com.wvelabs.core_ui.base.ViewEvent
import com.wvelabs.core_ui.base.ViewSideEffect
import com.wvelabs.core_ui.base.ViewState
import javax.inject.Inject

/**
 * The parent ViewModel for EVERY feature in the app.
 * It provides built-in Navigation and Auto-Retry logic for API calls.
 */
abstract class BaseViewModel<S : ViewState, E : ViewEvent, Ef : ViewSideEffect> :
    CoreViewModel<S, E, Ef>() {

}