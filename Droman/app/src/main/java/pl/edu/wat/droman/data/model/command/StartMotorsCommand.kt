package pl.edu.wat.droman.data.model.command

import dji.common.error.DJIError
import pl.edu.wat.droman.callback.CompletionCallbackImpl
import pl.edu.wat.droman.ui.FeedbackUtils
import pl.edu.wat.droman.ui.flightcontrol.handler.AircraftControllers
import pl.edu.wat.droman.ui.flightcontrol.handler.CommandHandler

class StartMotorsCommand : Command(type) {
    companion object {
        const val type = "start_motors"
    }

    override fun exec(commandHandler: AircraftControllers) {
        val status = commandHandler.statsHandler.getLastStatus()
        if (!status.isFlying && !status.motorsOn) {
            commandHandler.flightController.turnOnMotors(
                CompletionCallbackImpl<DJIError>(
                    CommandHandler.TAG
                )
            )
        } else {
            FeedbackUtils.setResult("Forbidden state can't start motors")
        }
    }
}