package pl.edu.wat.droman.data.model.command

import dji.common.error.DJIError
import dji.common.util.CommonCallbacks
import pl.edu.wat.droman.ui.FeedbackUtils
import pl.edu.wat.droman.ui.flightcontrol.handler.AircraftControllers

class LandCommand(completionCallback : CommonCallbacks.CompletionCallback<DJIError>) : Command(type, completionCallback) {
    companion object {
        const val type = "land"
    }

    override fun exec(aircraftControllers: AircraftControllers) {
        val status = aircraftControllers.statsHandler.getLastStatus()
        if (status.isFlying && !status.isGoingHome) {
            aircraftControllers.flightController.startLanding(completionCallback)
        } else {
            FeedbackUtils.setResult("Forbidden state can't start landing", tag = TAG)
        }
    }
}