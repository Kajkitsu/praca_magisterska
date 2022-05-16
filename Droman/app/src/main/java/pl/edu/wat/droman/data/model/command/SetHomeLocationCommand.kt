package pl.edu.wat.droman.data.model.command

import com.google.gson.JsonObject
import dji.common.error.DJIError
import dji.common.model.LocationCoordinate2D
import pl.edu.wat.droman.ui.callback.CompletionCallbackImpl
import pl.edu.wat.droman.ui.FeedbackUtils
import pl.edu.wat.droman.ui.flightcontrol.handler.AircraftControllers
import pl.edu.wat.droman.ui.flightcontrol.handler.CommandHandler

class SetHomeLocationCommand(jsonObject: JsonObject) : Command(type) {
    val longitude: Double = jsonObject.get("longitude").asDouble
    val latitude: Double = jsonObject.get("latitude").asDouble

    companion object {
        const val type = "set_home_location"
    }

    override fun exec(commandHandler: AircraftControllers) {
        commandHandler.flightController.setHomeLocation(
            LocationCoordinate2D(latitude, longitude),
            CompletionCallbackImpl<DJIError>(
                tag = CommandHandler.TAG,
                success = {
                    FeedbackUtils.setResult(
                        "Success setting home location",
                        tag = CommandHandler.TAG
                    )
                })
        )
    }
}