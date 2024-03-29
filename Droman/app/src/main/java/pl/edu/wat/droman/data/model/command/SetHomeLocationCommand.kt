package pl.edu.wat.droman.data.model.command

import com.google.gson.JsonObject
import dji.common.error.DJIError
import dji.common.model.LocationCoordinate2D
import dji.common.util.CommonCallbacks
import pl.edu.wat.droman.callback.CompletionCallbackImpl
import pl.edu.wat.droman.ui.FeedbackUtils
import pl.edu.wat.droman.ui.flightcontrol.handler.AircraftControllers

class SetHomeLocationCommand(jsonObject: JsonObject, completionCallback : CommonCallbacks.CompletionCallback<DJIError>) : Command(type, completionCallback) {
    val longitude: Double = jsonObject.get("longitude").asDouble
    val latitude: Double = jsonObject.get("latitude").asDouble

    companion object {
        const val type = "set_home_location"
    }

    override fun exec(aircraftControllers: AircraftControllers) {
        aircraftControllers.flightController.setHomeLocation(
            LocationCoordinate2D(latitude, longitude),
            CompletionCallbackImpl<DJIError>(
                tag = TAG,
                success = {
                    FeedbackUtils.setResult(
                        "Success setting home location",
                        tag = TAG
                    )
                })
        )
    }
}