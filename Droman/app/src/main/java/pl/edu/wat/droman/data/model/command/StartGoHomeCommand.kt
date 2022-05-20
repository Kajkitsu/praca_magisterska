package pl.edu.wat.droman.data.model.command

import dji.common.error.DJIError
import dji.common.util.CommonCallbacks
import pl.edu.wat.droman.ui.FeedbackUtils
import pl.edu.wat.droman.ui.flightcontrol.handler.AircraftControllers

class StartGoHomeCommand(completionCallback : CommonCallbacks.CompletionCallback<DJIError>) : Command(type, completionCallback) {
    companion object {
        const val type = "go_home"
    }

    override fun exec(aircraftControllers: AircraftControllers) {
        val status = aircraftControllers.statsHandler.getLastStatus()
        if (status.isFlying && status.isHomeLocationSet && !status.isGoingHome) {
            aircraftControllers.flightController.startGoHome(
                completionCallback
            )
        } else {
            FeedbackUtils.setResult("Forbidden state can't start going home", tag = TAG)
        }
    }

}