package pl.edu.wat.droman.data.model.command

import dji.common.error.DJIError
import dji.common.util.CommonCallbacks
import pl.edu.wat.droman.ui.FeedbackUtils
import pl.edu.wat.droman.ui.flightcontrol.handler.AircraftControllers

class TakeOffCommand(completionCallback : CommonCallbacks.CompletionCallback<DJIError>) : Command(type, completionCallback) {
    companion object {
        const val type = "take_off"
    }

    override fun exec(aircraftControllers: AircraftControllers) {
        val status = aircraftControllers.statsHandler.getLastStatus()
        if (!status.isFlying && !status.motorsOn) {
            aircraftControllers.flightController.startTakeoff(
                completionCallback
            )
        } else {
            FeedbackUtils.setResult("Forbidden state can't start take off", TAG)
        }
    }
}