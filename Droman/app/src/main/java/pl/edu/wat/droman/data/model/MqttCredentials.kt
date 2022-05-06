package pl.edu.wat.droman.data.model

import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.MqttCallback
import pl.edu.wat.droman.data.datasource.MqttDto

data class MqttCredentials(
    val serverURI: String,
    val clientID: String,
    val username:   String,
    val password:   String
)