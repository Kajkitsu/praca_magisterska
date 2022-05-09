package pl.edu.wat.droman.data.repository

import android.content.Context
import android.util.Log
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
    private val messageArrivedFun: (topic: String, message: MqttMessage) -> Unit = { topic, message ->
        Log.d("MqttRepository", "Receive message: $message from topic: $topic")
    },
    private val connectionLostFun: (throwable: Throwable) -> Unit = { cause ->
        Log.d("MqttRepository", "Connection lost $cause")
    },
    private val deliveryCompleteFun: (token: IMqttDeliveryToken) -> Unit = {
        Log.d("MqttRepository", "Delivery completed")
    }
) {
    private val mqttClient: MqttClient =
        MqttClient(context, mqttCredentials.serverURI, mqttCredentials.clientID)


    suspend fun publish(mqttDto: MqttDto): Result<IMqttDeliveryToken> {
        if (!mqttClient.isConnected()) {
            val result = connect()
            if (result.isFailure) {
                return Result.failure(result.exceptionOrNull()!!)
            }
        }
        return mqttClient.publish(
            mqttDto.topic,
            mqttDto.msg,
            mqttDto.qos,
            mqttDto.retained
        )
    }

    suspend fun subscribe(topic: String, qos: Int = 1): Result<IMqttToken> {
        if (!mqttClient.isConnected()) {
            val result = connect()
            if (result.isFailure) {
                return Result.failure(result.exceptionOrNull()!!)
            }
        }
        return mqttClient.subscribe(topic, qos)
    }

    suspend fun unsubscribe(topic: String): Result<IMqttToken> {
        if (!mqttClient.isConnected()) {
            val result = connect()
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
            cbClient = object : MqttCallback {
                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    if (topic != null && message != null) {
                        messageArrivedFun(topic, message)
                    }
                }

                override fun connectionLost(cause: Throwable?) {
                    if (cause != null) {
                        connectionLostFun(cause)
                    }
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                    if (token != null) {
                        deliveryCompleteFun(token)
                    }
                }
            })
    }

    suspend fun destroy() {
        mqttClient.disconnect()
    }

}