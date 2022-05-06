package pl.edu.wat.droman.data.datasource

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*

class MqttClient(
    context: Context?,
    serverURI: String,
    clientID: String = ""
) {
    private var mqttClient = MqttAndroidClient(context, serverURI, clientID)

    private val waitForResponseTimeout = 10000L

    private val defaultCbClient = object : MqttCallback {
        override fun messageArrived(topic: String?, message: MqttMessage?) {
            Log.d(this.javaClass.name, "Receive message: ${message.toString()} from topic: $topic")
        }

        override fun connectionLost(cause: Throwable?) {
            Log.d(this.javaClass.name, "Connection lost ${cause.toString()}")
        }

        override fun deliveryComplete(token: IMqttDeliveryToken?) {
            Log.d(this.javaClass.name, "Delivery completed")
        }
    }


    suspend fun connect(
        username: String,
        password: String,
        lastWill: MqttDto? = null,
        keepAliveInterval: Int = 120,
        cbClient: MqttCallback? = defaultCbClient
    ) : Result<IMqttToken> = withContext(Dispatchers.IO) {
        cbClient?.let { it -> mqttClient.setCallback(it) }

        val options = MqttConnectOptions()
        options.userName = username
        options.password = password.toCharArray()
        options.keepAliveInterval = keepAliveInterval

        lastWill?.let { it ->
            options.setWill(
                it.topic,
                it.msg.toByteArray(), it.qos, it.retained
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
        data: MqttDto,
    ): Result<IMqttDeliveryToken> = withContext(Dispatchers.IO) {
        try {
            val message = MqttMessage()
            message.payload = data.msg.toByteArray()
            message.qos = data.qos
            message.isRetained = data.retained
            val token = mqttClient.publish(data.topic, message)
            token.waitForCompletion(waitForResponseTimeout)
            return@withContext Result.success(token)
        }
        catch (e: MqttException) {
            e.printStackTrace()
            return@withContext Result.failure(e)
        }
    }


//    fun subscribe(
//        topic: String,
//        qos: Int = 1,
//        cbSubscribe: IMqttActionListener = MqttResultActionListener()
//    ) {
//        try {
//            mqttClient.subscribe(topic, qos, null, cbSubscribe)
//        } catch (e: MqttException) {
//            e.printStackTrace()
//        }
//    }
//
//    fun unsubscribe(
//        topic: String,
//        cbUnsubscribe: IMqttActionListener = MqttResultActionListener()
//    ) {
//        try {
//            mqttClient.unsubscribe(topic, null, cbUnsubscribe)
//        } catch (e: MqttException) {
//            e.printStackTrace()
//        }
//    }



}