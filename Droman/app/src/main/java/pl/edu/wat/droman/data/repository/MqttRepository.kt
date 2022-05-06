package pl.edu.wat.droman.data.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttMessage
import pl.edu.wat.droman.data.datasource.MqttClient
import pl.edu.wat.droman.data.datasource.MqttDto
import pl.edu.wat.droman.data.model.MqttCredentials

class MqttRepository(
    context: Context,
    private val mqttCredentials: MqttCredentials,
    private val lastWill: MqttDto? = null,
    private val keepAliveInterval: Int = 120,
) {
    private val mqttClient: MqttClient =
        MqttClient(context, mqttCredentials.serverURI, mqttCredentials.clientID)

    private val messagesArrived: HashMap<String, MutableLiveData<MqttMessage>> = HashMap()

    private val defaultCbClient = object : MqttCallback {
        override fun messageArrived(topic: String?, message: MqttMessage?) {
            Log.d(this.javaClass.name, "Receive message: ${message.toString()} from topic: $topic")
            if (topic != null && message != null) {
                getData(topic)
                    .postValue(message)
            }
        }

        override fun connectionLost(cause: Throwable?) {
            Log.d(this.javaClass.name, "Connection lost ${cause.toString()}")
        }

        override fun deliveryComplete(token: IMqttDeliveryToken?) {
            Log.d(this.javaClass.name, "Delivery completed")
        }
    }

    fun getData(topic: String): MutableLiveData<MqttMessage> {
        return messagesArrived
            .getOrPut(topic) { MutableLiveData() }
    }


    suspend fun publish(mqttDto: MqttDto): Result<IMqttDeliveryToken> {
        if (!mqttClient.isConnected()) {
            val result = connect();
            if (result.isFailure) {
                return Result.failure(result.exceptionOrNull()!!)
            }
        }
        return mqttClient.publish(
            mqttDto.topic,
            mqttDto.msg.toByteArray(),
            mqttDto.qos,
            mqttDto.retained
        )
    }

    suspend fun subscribe(topic: String, qos: Int = 1): Result<IMqttToken> {
        if (!mqttClient.isConnected()) {
            val result = connect();
            if (result.isFailure) {
                return Result.failure(result.exceptionOrNull()!!)
            }
        }
        return mqttClient.subscribe(topic, qos)
    }

    suspend fun unsubscribe(topic: String): Result<IMqttToken> {
        if (!mqttClient.isConnected()) {
            val result = connect();
            if (result.isFailure) {
                return Result.failure(result.exceptionOrNull()!!)
            }
        }
        return mqttClient.unsubscribe(topic)
    }

    private suspend fun connect(): Result<IMqttToken> {
        return mqttClient.connect(
            username = mqttCredentials.username,
            password = mqttCredentials.password,
            lastWill = lastWill,
            keepAliveInterval = keepAliveInterval,
            cbClient = defaultCbClient
        )
    }

}