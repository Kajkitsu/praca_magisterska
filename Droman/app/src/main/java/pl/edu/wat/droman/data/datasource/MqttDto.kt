package pl.edu.wat.droman.data.datasource

data class MqttDto(
    val topic: String,
    val msg: String,
    val qos: Int = 1,
    val retained: Boolean = false,
    )
