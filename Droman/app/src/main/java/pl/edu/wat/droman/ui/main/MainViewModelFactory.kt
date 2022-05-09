package pl.edu.wat.droman.ui.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dji.sdk.sdkmanager.DJISDKManager
import pl.edu.wat.droman.data.model.MqttCredentials
import pl.edu.wat.droman.data.service.MqttService
import java.util.*

class MainViewModelFactory() : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel()
             as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}