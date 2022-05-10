package pl.edu.wat.droman.data.service

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dji.common.flightcontroller.FlightControllerState
import pl.edu.wat.droman.data.model.FlightStatus

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
        val result = statusTopic.publish(
            FlightStatus.gen(state).toJson()
        )
        if (result.isFailure) {
            Log.e(
                this.javaClass.name,
                "Failure saving ${statusTopic.getValue()} with data. Failure ${result.exceptionOrNull()}"
            )
        } else {
            Log.d(
                this.javaClass.name,
                "Success saving ${statusTopic.getValue()} with data:$state"
            )
        }
    }
}
