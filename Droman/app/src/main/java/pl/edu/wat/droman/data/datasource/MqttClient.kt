package pl.edu.wat.droman.data.datasource

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*

class MqttClient(
    context: Context?,
    serverURI: String,
    clientID: String = "",
    private val waitForResponseTimeout: Long = 10000L
) {

    private var mqttClient = MqttAndroidClient(context, serverURI, clientID)

    suspend fun connect(
        username: String,
        password: String,
        cbClient: MqttCallback,
        lastWill: MqttDto? = null,
        keepAliveInterval: Int = 120
    ): Result<IMqttToken> = withContext(Dispatchers.IO) {

        val options = MqttConnectOptions()
        options.userName = username
        options.password = password.toCharArray()
        options.keepAliveInterval = keepAliveInterval
        mqttClient.setCallback(cbClient)

        lastWill?.let { it ->
            options.setWill(
                it.topic,
                it.msg, it.qos, it.retained
            )
        }

        try {
            val token = mqttClient.connect(options)
            token.waitForCompletion(waitForResponseTimeout)
            return@withContext Result.success(token)
        } catch (e: MqttException) {
            e.printStackTrace()
            return@withContext Result.failure(e)
        }
    }

    suspend fun disconnect(): Result<IMqttToken> = withContext(Dispatchers.IO) {
        try {
            val token = mqttClient.disconnect()
            token.waitForCompletion(waitForResponseTimeout)
            return@withContext Result.success(token)
        } catch (e: MqttException) {
            e.printStackTrace()
            return@withContext Result.failure(e)
        }
    }

    fun isConnected(): Boolean {
        return mqttClient.isConnected
    }

    suspend fun publish(
        topic: String,
        payload: ByteArray,
        qos: Int,
        retained: Boolean,
    ): Result<IMqttDeliveryToken> = withContext(Dispatchers.IO) {
        try {
            val message = MqttMessage()
            message.payload = payload
            message.qos = qos
            message.isRetained = retained
            val token = mqttClient.publish(topic, message)
            token.waitForCompletion(waitForResponseTimeout)
            return@withContext Result.success(token)
        } catch (e: MqttException) {
            e.printStackTrace()
            return@withContext Result.failure(e)
        }
    }


    suspend fun subscribe(
        topic: String,
        qos: Int = 1
    ): Result<IMqttToken> = withContext(Dispatchers.IO) {
        try {
            val token = mqttClient.subscribe(topic, qos)
            token.waitForCompletion(waitForResponseTimeout)
            return@withContext Result.success(token)
        } catch (e: MqttException) {
            e.printStackTrace()
            return@withContext Result.failure(e)
        }
    }


    suspend fun unsubscribe(
        topic: String
    ): Result<IMqttToken> = withContext(Dispatchers.IO) {
        try {
            val token = mqttClient.unsubscribe(topic)
            token.waitForCompletion(waitForResponseTimeout)
            return@withContext Result.success(token)
        } catch (e: MqttException) {
            e.printStackTrace()
            return@withContext Result.failure(e)
        }
    }

}