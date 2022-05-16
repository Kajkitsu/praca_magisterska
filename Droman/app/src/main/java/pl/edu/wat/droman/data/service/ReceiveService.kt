package pl.edu.wat.droman.data.service

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.eclipse.paho.client.mqttv3.MqttMessage
import pl.edu.wat.droman.data.model.command.Command
import pl.edu.wat.droman.data.model.command.CommandFactory

class ReceiveService(
    private val commandTopic: MqttService.Topic,
) {
    private val commandFactory: CommandFactory = CommandFactory()
    private val _command: MutableLiveData<Command> = MutableLiveData<Command>()
    val command: LiveData<Command> = _command

    private val observer = Observer<MqttMessage> {
        val newMission = commandFactory.from(it.toString())
        newMission.let { itMission -> _command.postValue(itMission) }
    }

    init {
        GlobalScope.launch(Dispatchers.IO) {
            commandTopic.subscribe()
        }
        commandTopic.getData()
            .observeForever(observer)
    }

    fun destroy() {
        commandTopic.getData().removeObserver(observer)
    }

}



