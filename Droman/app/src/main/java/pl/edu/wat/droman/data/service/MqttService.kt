package pl.edu.wat.droman.data.service

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttMessage
import pl.edu.wat.droman.data.datasource.MqttDto
import pl.edu.wat.droman.data.model.MqttCredentials
import pl.edu.wat.droman.data.repository.MqttRepository

class MqttService(
    context: Context,
    mqttCredentials: MqttCredentials,
    lastWill: MqttDto? = null,
    keepAliveInterval: Int = 120,
) {

    private val subscribedTopics: MutableSet<String> = HashSet()
    private val messagesArrived: HashMap<String, MutableLiveData<MqttMessage>> = HashMap()
    private val connectionLostFun: (throwable: Throwable) -> Unit = { cause ->
        Log.d(this.javaClass.name, "Connection lost $cause")

        Log.d(this.javaClass.name, "Trying to subscribe last topic")
        subscribedTopics.stream()
            .forEach { runBlocking { launch(Dispatchers.IO) { mqttRepository.subscribe(it) } } }
    }
    private val deliveryCompleteFun: (token: IMqttDeliveryToken) -> Unit = {
        Log.d(this.javaClass.name, "Delivery completed")
    }
    private val messageArrivedFun: (topic: String, message: MqttMessage) -> Unit =
        { topic, message ->
            if (subscribedTopics.contains(topic)) {
                Log.d(this.javaClass.name, "Receive message: $message from topic: $topic")
                getTopicLastData(topic).postValue(message)

            } else {
                Log.e(
                    this.javaClass.name,
                    "Receive message: $message from unsubscribed topic: $topic"
                )
            }
        }

    private val mqttRepository = MqttRepository(
        context,
        mqttCredentials,
        lastWill,
        keepAliveInterval,
        messageArrivedFun,
        connectionLostFun,
        deliveryCompleteFun
    )


    private fun getTopicLastData(topic: String): MutableLiveData<MqttMessage> {
        return messagesArrived
            .getOrPut(topic) { MutableLiveData() }
    }

    class Topic(private val value: String, private val mqttService: MqttService) {
        suspend fun publish(payload: ByteArray): Result<IMqttDeliveryToken> {
            return mqttService.publish(MqttDto(value,payload))
        }

        suspend fun publish(payload: String): Result<IMqttDeliveryToken> {
            return mqttService.publish(MqttDto(value,payload))
        }

        fun isSubscribed(): Boolean {
            return mqttService.getSubscribed().contains(value)
        }

        suspend fun subscribe(): Result<IMqttToken> {
            return mqttService.subscribe(value)
        }

        suspend fun unsubscribe(): Result<IMqttToken> {
            return mqttService.unsubscribe(value)
        }

        fun getData(): LiveData<MqttMessage> {
            if(!isSubscribed()){
                Log.w(this.javaClass.name,"getData without subscribing topic $value")
            }
            return mqttService.getLiveData(value)
        }
    }

    private fun getLiveData(value: String): LiveData<MqttMessage> {
        return getTopicLastData(value)
    }

    private suspend fun unsubscribe(value: String): Result<IMqttToken> {
        val res = mqttRepository.unsubscribe(value)
        if (res.isSuccess) {
            subscribedTopics.remove(value)
        }
        return res
    }

    private suspend fun subscribe(value: String): Result<IMqttToken> {
        val res = mqttRepository.subscribe(value)
        if (res.isSuccess) {
            subscribedTopics.add(value)
        }
        return res
    }

    private fun getSubscribed(): Set<String> {
        return subscribedTopics
    }

    private suspend fun publish(data: MqttDto): Result<IMqttDeliveryToken> {
        return mqttRepository.publish(data)
    }

    fun getTopic(value: String): Topic {
        return Topic(value = value, this)
    }
}