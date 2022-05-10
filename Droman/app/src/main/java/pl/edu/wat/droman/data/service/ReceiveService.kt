package pl.edu.wat.droman.data.service

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.eclipse.paho.client.mqttv3.MqttMessage
import pl.edu.wat.droman.data.model.Mission

class ReceiveService(
    private val taskTopic: MqttService.Topic,
) {
    init {
        GlobalScope.launch(Dispatchers.IO) {
            taskTopic.subscribe()
        }
    }

    fun getMission(): Mission? {
        return taskTopic
            .getData()
            .mapToMission()
    }

}

private fun LiveData<MqttMessage>.mapToMission(): Mission? {
    if (this.value != null) {
        return Mission.from(this.value.toString())
    } else {
        return null
    }
}



