package pl.edu.wat.droman.data.model.command

import dji.common.error.DJIError
import pl.edu.wat.droman.CompletionCallbackHandler
import pl.edu.wat.droman.ui.FeedbackUtils
import pl.edu.wat.droman.ui.flightcontrol.handler.AircraftControllers
import pl.edu.wat.droman.ui.flightcontrol.handler.CommandHandler

class TakeOffCommand : Command(type) {
    companion object {
        const val type = "take_off"
    }

    override fun exec(commandHandler: AircraftControllers) {
        val status = commandHandler.statsHandler.getLastStatus()
        if (!status.isFlying && !status.motorsOn) {
            commandHandler.flightController.startTakeoff(
                CompletionCallbackHandler<DJIError>(
                    CommandHandler.TAG
                )
            )
        } else {
            FeedbackUtils.setResult("Forbidden state can't start take off")
        }
    }
}