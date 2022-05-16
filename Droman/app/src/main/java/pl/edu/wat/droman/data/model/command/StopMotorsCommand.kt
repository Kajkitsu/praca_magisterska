package pl.edu.wat.droman.data.model.command

import pl.edu.wat.droman.ui.FeedbackUtils
import pl.edu.wat.droman.ui.flightcontrol.handler.AircraftControllers

class StopMotorsCommand : Command(type) {
    companion object {
        const val type = "stop_motors"
    }

    override fun exec(aircraftControllers: AircraftControllers) {
        val status = aircraftControllers.statsHandler.getLastStatus()
        if (!status.isFlying && status.motorsOn) {
            aircraftControllers.flightController.turnOffMotors(
                getCompletionCallback()
            )
        } else {
            FeedbackUtils.setResult("Forbidden state can't stop motors", TAG)
        }
    }
}