package pl.edu.wat.droman.data.service

import android.util.Log
import dji.common.flightcontroller.FlightControllerState
import dji.sdk.flightcontroller.FlightController
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pl.edu.wat.droman.data.model.FlightStatus

@DelicateCoroutinesApi
class UpdateService(
    private val statusTopic: MqttService.Topic,
) {

    init {
//        flightController.setStateCallback(getStatusCallback()) //TODO("are you sure")
    }

    fun getStatusCallback() = FlightControllerState.Callback {
        GlobalScope.launch(Dispatchers.IO) {
            val result = statusTopic.publish(
                FlightStatus.gen(it).toString()
            )
            if (result.isFailure) {
                Log.e(
                    this.javaClass.name,
                    "Failure saving ${statusTopic.getValue()} with data. Failure ${result.exceptionOrNull()}"
                )
            } else {
                Log.d(
                    this.javaClass.name,
                    "Success saving ${statusTopic.getValue()} with data. Failure $it"
                )
            }
        }
    }
}
