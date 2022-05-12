package pl.edu.wat.droman.data.service

import android.graphics.Bitmap
import android.util.Log
import dji.common.flightcontroller.FlightControllerState
import pl.edu.wat.droman.data.model.FlightStatus
import java.io.ByteArrayOutputStream


class UpdateService(
    private val statusTopic: MqttService.Topic,
    private val videoTopic: MqttService.Topic,
) {

    init {
//        flightController.setStateCallback(getStatusCallback()) //TODO("are you sure")
    }

    //    fun getCameraCallback() = Camera.VideoDataCallback { bytes, i ->
//        GlobalScope.launch(Dispatchers.IO) {
//            val result = videoTopic.publish(bytes)
//            if (result.isFailure) {
//                Log.e(
//                    this.javaClass.name,
//                    "Failure saving ${videoTopic.getValue()} with data. Failure ${result.exceptionOrNull()}"
//                )
//            } else {
//                Log.d(
//                    this.javaClass.name,
//                    "Success saving ${videoTopic.getValue()} with data size:${bytes.size}"
//                )
//            }
//        }
//    }
//
    suspend fun saveCallback(state: FlightControllerState) {
        val data = FlightStatus.gen(state).toJson()
        val result = statusTopic.publish(
            data
        )
        if (result.isFailure) {
            Log.e(
                this.javaClass.name,
                "Failure saving ${statusTopic.getValue()} with data. Failure ${result.exceptionOrNull()}"
            )
        } else {
            Log.d(
                this.javaClass.name,
                "Success saving ${statusTopic.getValue()} with data:${data}"
            )
        }
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
