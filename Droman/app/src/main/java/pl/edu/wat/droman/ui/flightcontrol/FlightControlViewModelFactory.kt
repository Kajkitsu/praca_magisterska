package pl.edu.wat.droman.ui.flightcontrol

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pl.edu.wat.droman.data.ETopic
import pl.edu.wat.droman.data.datasource.MqttDto
import pl.edu.wat.droman.data.model.MqttCredentials
import pl.edu.wat.droman.data.service.MqttService
import pl.edu.wat.droman.data.service.ReceiveService
import pl.edu.wat.droman.data.service.UpdateService
import pl.edu.wat.droman.ui.FeedbackUtils

class FlightControlViewModelFactory(
    private val username: String,
    private val password: String,
    private val ipAddress: String,
    private val context: Context,
    private val clientId: String
) : ViewModelProvider.Factory {
    companion object {
        const val TAG = "FlightControlViewModelFactory"
    }


    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
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
                receiveService = ReceiveService(
                    mqttService.getTopic(ETopic.COMMAND.path + "/" + clientId)
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }


}