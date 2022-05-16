package pl.edu.wat.droman.data.model.command

import dji.common.error.DJIError
import pl.edu.wat.droman.ui.callback.CompletionCallbackImpl
import pl.edu.wat.droman.ui.FeedbackUtils
import pl.edu.wat.droman.ui.flightcontrol.handler.AircraftControllers
import pl.edu.wat.droman.ui.flightcontrol.handler.CommandHandler

class StopGoHomeCommand : Command(type) {
    companion object {
        const val type = "stop_go_home"
    }

    override fun exec(commandHandler: AircraftControllers) {
        val status = commandHandler.statsHandler.getLastStatus()
        if (status.isFlying && status.isHomeLocationSet && status.isGoingHome) {
            commandHandler.flightController.cancelGoHome(
                CompletionCallbackImpl<DJIError>(
                    CommandHandler.TAG
                )
            )
        } else {
            FeedbackUtils.setResult("Forbidden state can't stop going home")
        }
    }
}