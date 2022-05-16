package pl.edu.wat.droman.data.model.command

import dji.common.error.DJIError
import pl.edu.wat.droman.callback.CompletionCallbackImpl
import pl.edu.wat.droman.ui.FeedbackUtils
import pl.edu.wat.droman.ui.LogLevel
import pl.edu.wat.droman.ui.flightcontrol.handler.AircraftControllers

abstract class Command(val type: String) {
    abstract fun exec(aircraftControllers: AircraftControllers)

    companion object {
        const val TAG = "Command"
    }

    fun getCompletionCallback(): CompletionCallbackImpl<DJIError> {
        return CompletionCallbackImpl<DJIError>(
            TAG,
            success = {
                FeedbackUtils.setResult(
                    "Success executing command $type",
                    level = LogLevel.DEBUG,
                    tag = TAG
                )
            }
        )

    }
}