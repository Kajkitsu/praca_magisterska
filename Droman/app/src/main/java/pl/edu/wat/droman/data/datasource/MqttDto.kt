package pl.edu.wat.droman.data.datasource

import pl.edu.wat.droman.data.ETopic

data class MqttDto(
    val topic: String,
    val payload: ByteArray,
    val qos: Int = 1,
    val retained: Boolean = false,
) {
    constructor(topic: ETopic, msg: String) : this(topic.name, msg.toByteArray())
    constructor(topic: ETopic, payload: ByteArray) : this(topic.name, payload)
    constructor(topic: String, msg: String) : this(topic,msg.toByteArray())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MqttDto

        if (topic != other.topic) return false
        if (!payload.contentEquals(other.payload)) return false
        if (qos != other.qos) return false
        if (retained != other.retained) return false

        return true
    }

    override fun hashCode(): Int {
        var result = topic.hashCode()
        result = 31 * result + payload.contentHashCode()
        result = 31 * result + qos
        result = 31 * result + retained.hashCode()
        return result
    }
}
