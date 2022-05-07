package pl.edu.wat.droman.ui.mqtttest

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pl.edu.wat.droman.data.model.MqttCredentials
import pl.edu.wat.droman.data.service.MqttService

class MqttTestViewModelFactory(
    private val context: Context,
    private val ipAddress: String,
    private val username: String,
    private val password: String
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MqttTestViewModel::class.java)) {
            return MqttTestViewModel(
                mqttService = MqttService(
                    context = context,
                    mqttCredentials = MqttCredentials(
                        "tcp://$ipAddress",
                        "androidTestView",
                        username,
                        password
                    )
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}