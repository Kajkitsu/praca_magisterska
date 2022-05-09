package pl.edu.wat.droman.ui.flightcontrol

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dji.sdk.sdkmanager.DJISDKManager
import pl.edu.wat.droman.data.ETopic
import pl.edu.wat.droman.data.datasource.MqttDto
import pl.edu.wat.droman.data.model.MqttCredentials
import pl.edu.wat.droman.data.service.MqttService
import pl.edu.wat.droman.data.service.UpdateService

class FlightControlViewModelFactory(
    private val username: String,
    private val password: String,
    private val ipAddress: String,
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val djisdkManager = DJISDKManager.getInstance()
        val clientId = djisdkManager?.product?.model?.displayName.let { it ?: "" }
        if (modelClass.isAssignableFrom(FlightControlViewModel::class.java)) {
            val mqttService = MqttService(
                context = context,
                mqttCredentials = MqttCredentials("tcp://$ipAddress", clientId, username, password),
                lastWill = MqttDto(ETopic.LAST_WILL, clientId),
                birth = MqttDto(ETopic.BIRTH, clientId)
            )
            return FlightControlViewModel(
                updateService = UpdateService(
                    mqttService.getTopic(ETopic.STATE.path + "/" + clientId),
                    mqttService.getTopic(ETopic.PICTURE.path + "/" + clientId)
                ),
                djisdkManager = djisdkManager
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}