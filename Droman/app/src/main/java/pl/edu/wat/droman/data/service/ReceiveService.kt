package pl.edu.wat.droman.data.service

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.eclipse.paho.client.mqttv3.MqttMessage
import pl.edu.wat.droman.data.model.command.Command
import pl.edu.wat.droman.data.model.command.CommandFactory
import pl.edu.wat.droman.ui.DjiApplication

class ReceiveService(
    private val commandTopic: MqttService.Topic,
) {
    private val commandFactory: CommandFactory = CommandFactory()

    init {
        DjiApplication.mainScope.launch(Dispatchers.IO) {
            commandTopic.subscribe()
        }
    }

    fun getCommand(): LiveData<Command> {
        return  commandTopic.getData()
            .map { commandFactory.from(it.toString())  }
    }

}



