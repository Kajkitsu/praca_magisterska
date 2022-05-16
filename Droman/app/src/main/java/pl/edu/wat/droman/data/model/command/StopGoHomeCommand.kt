package pl.edu.wat.droman.data.model.command

import pl.edu.wat.droman.ui.FeedbackUtils
import pl.edu.wat.droman.ui.flightcontrol.handler.AircraftControllers

class StopGoHomeCommand : Command(type) {
    companion object {
        const val type = "stop_go_home"
    }

    override fun exec(aircraftControllers: AircraftControllers) {
        val status = aircraftControllers.statsHandler.getLastStatus()
        if (status.isFlying && status.isHomeLocationSet && status.isGoingHome) {
            aircraftControllers.flightController.cancelGoHome(
                getCompletionCallback()
            )
        } else {
            FeedbackUtils.setResult("Forbidden state can't stop going home", TAG)
        }
    }
}