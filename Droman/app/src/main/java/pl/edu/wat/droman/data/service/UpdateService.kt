package pl.edu.wat.droman.data.service

import android.graphics.Bitmap
import android.util.Log
import kotlinx.coroutines.delay
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import pl.edu.wat.droman.data.model.FlightStatus
import java.io.ByteArrayOutputStream


class UpdateService(
    private val statusTopic: MqttService.Topic,
    private val videoTopic: MqttService.Topic,
) {

    suspend fun saveCallback(data: FlightStatus): Result<IMqttDeliveryToken> {
        val result = statusTopic.publish(
            data.toJson()
        )
        if (result.isFailure) {
            Log.e(
                this.javaClass.name,
                "Failure saving ${statusTopic.getValue()} with data. Failure ${result.exceptionOrNull()}"
            )
        } else {
            Log.d(
                this.javaClass.name,
                "Success saving ${statusTopic.getValue()} with data:${data.toJson()}"
            )
        }
        return result
    }

    suspend fun savePicture(bitmap: Bitmap) {
        return savePicture(bitmap.convertToByteArray())
    }

    suspend fun savePicture(byteArray: ByteArray) {
        val result = videoTopic.publish(
            byteArray
        )
        if (result.isFailure) {
            Log.e(
                this.javaClass.name,
                "Failure saving ${statusTopic.getValue()} with data. Failure ${result.exceptionOrNull()}"
            )
        } else {
            Log.d(
                this.javaClass.name,
                "Success saving ${statusTopic.getValue()} with bitmap data"
            )
        }
    }

    fun Bitmap.convertToByteArray(): ByteArray {
        val stream = ByteArrayOutputStream()
        this.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }
}
