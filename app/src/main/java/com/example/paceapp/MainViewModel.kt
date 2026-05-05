package com.example.paceapp

import androidx.lifecycle.ViewModel
import com.example.paceapp.core.garmin.GarminConnectHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val garminHelper: GarminConnectHelper
) : ViewModel() {

    // Expose the global state directly to the UI
//    val isSdkReady: StateFlow<Boolean> = garminHelper.isSdkReady

    init {
        // Fire this off as soon as the app starts.
//        viewModelScope.launch {
//            // Setting autoUI = true allows the SDK to automatically handle the missing Garmin Connect app scenario
//            garminHelper.initializeSdk(autoUI = true)
//        }
    }
}