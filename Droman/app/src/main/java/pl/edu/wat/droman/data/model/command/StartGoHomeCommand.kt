package pl.edu.wat.droman.data.model.command

import pl.edu.wat.droman.ui.FeedbackUtils
import pl.edu.wat.droman.ui.flightcontrol.handler.AircraftControllers

class StartGoHomeCommand : Command(type) {
    companion object {
        const val type = "go_home"
    }

    override fun exec(aircraftControllers: AircraftControllers) {
        val status = aircraftControllers.statsHandler.getLastStatus()
        if (status.isFlying && status.isHomeLocationSet && !status.isGoingHome) {
            aircraftControllers.flightController.startGoHome(
                getCompletionCallback()
            )
        } else {
            FeedbackUtils.setResult("Forbidden state can't start going home", tag = TAG)
        }
    }

}