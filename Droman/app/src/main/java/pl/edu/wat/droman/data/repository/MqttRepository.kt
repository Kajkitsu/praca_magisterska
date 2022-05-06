package pl.edu.wat.droman.data.repository

import android.content.Context
import org.eclipse.paho.client.mqttv3.*
import pl.edu.wat.droman.data.datasource.MqttClient
import pl.edu.wat.droman.data.datasource.MqttDto
import pl.edu.wat.droman.data.model.MqttCredentials

class MqttRepository(
    context: Context,
    private val mqttCredentials: MqttCredentials,
    private val lastWill: MqttDto? = null,
    private val keepAliveInterval: Int = 120,
    private val cbClient: MqttCallback? = null
) {
    private val mqttClient: MqttClient =
        MqttClient(context, mqttCredentials.serverURI, mqttCredentials.clientID)

    suspend fun publish(mqttDto: MqttDto): Result<IMqttDeliveryToken> {
        if (!mqttClient.isConnected()) {
            val result = connect();
            if(result.isFailure) {
                return Result.failure(result.exceptionOrNull()!!)
            }
        }
        return mqttClient.publish(mqttDto)
    }

    private suspend fun connect(): Result<IMqttToken> {
        return mqttClient.connect(
            username = mqttCredentials.username,
            password = mqttCredentials.password,
            lastWill = lastWill,
            keepAliveInterval = keepAliveInterval,
            cbClient = cbClient
        )
    }

}