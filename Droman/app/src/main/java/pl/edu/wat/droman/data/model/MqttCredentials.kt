package pl.edu.wat.droman.data.model

data class MqttCredentials(
    val serverURI: String,
    val clientID: String,
    val username: String,
    val password: String
)