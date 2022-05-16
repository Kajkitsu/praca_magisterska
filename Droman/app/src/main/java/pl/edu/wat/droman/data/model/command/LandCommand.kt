package pl.edu.wat.droman.data.model.command

import pl.edu.wat.droman.ui.FeedbackUtils
import pl.edu.wat.droman.ui.flightcontrol.handler.AircraftControllers

class LandCommand : Command(type) {
    companion object {
        const val type = "land"
    }

    override fun exec(aircraftControllers: AircraftControllers) {
        val status = aircraftControllers.statsHandler.getLastStatus()
        if (status.isFlying && !status.isGoingHome) {
            aircraftControllers.flightController.startLanding(getCompletionCallback())
        } else {
            FeedbackUtils.setResult("Forbidden state can't start landing", tag = TAG)
        }
    }
}