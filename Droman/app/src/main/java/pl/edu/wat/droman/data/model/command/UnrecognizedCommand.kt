package pl.edu.wat.droman.data.model.command

import dji.common.error.DJIError
import dji.common.util.CommonCallbacks
import pl.edu.wat.droman.ui.FeedbackUtils
import pl.edu.wat.droman.ui.LogLevel
import pl.edu.wat.droman.ui.flightcontrol.handler.AircraftControllers

class UnrecognizedCommand(private val string: String, completionCallback : CommonCallbacks.CompletionCallback<DJIError>) : Command(type, completionCallback) {

    companion object {
        const val TAG = "UnrecognizedCommand"
        const val type = "unrecognized_command"
    }

    override fun exec(aircraftControllers: AircraftControllers) {
        FeedbackUtils.setResult(
            "Executing unrecognized command: \"$string\"",
            level = LogLevel.WARN,
            tag = ""
        )
    }

}
