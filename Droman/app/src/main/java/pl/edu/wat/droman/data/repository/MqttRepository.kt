package pl.edu.wat.droman.data.repository

import android.content.Context
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.MqttCallback
import pl.edu.wat.droman.data.datasource.MqttClient
import pl.edu.wat.droman.data.datasource.MqttDto
import pl.edu.wat.droman.data.model.MqttCredentials

class MqttRepository(
    context: Context,
    private val mqttCredentials: MqttCredentials,
    private val lastWill: MqttDto? = null,
    private val keepAliveInterval:   Int               = 120,
    private val cbConnect: IMqttActionListener? = null,
    private val cbClient: MqttCallback? = null
) {
    private val mqttClient: MqttClient =
        MqttClient(context, mqttCredentials.serverURI, mqttCredentials.clientID)

    init {
        connect()

    }

    fun isConnected(): Boolean {
        return mqttClient.isConnected()
    }

    fun publish(mqttDto: MqttDto, cbPublish: IMqttActionListener? = null) {
        if(!mqttClient.isConnected()){
            connect()
        }
        cbPublish?.let { mqttClient.publish(mqttDto, it) }
            ?: run {
                mqttClient.publish(mqttDto)
            }
    }

    private fun connect(){
        mqttClient.connect(
            username = mqttCredentials.username,
            password = mqttCredentials.password,
            lastWill = lastWill,
            keepAliveInterval = keepAliveInterval,
            cbConnect = cbConnect,
            cbClient = cbClient
        )
    }

}