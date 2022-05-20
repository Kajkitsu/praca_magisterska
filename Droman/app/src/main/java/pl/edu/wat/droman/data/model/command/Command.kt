package pl.edu.wat.droman.data.model.command

import dji.common.error.DJIError
import dji.common.util.CommonCallbacks
import pl.edu.wat.droman.callback.CompletionCallbackImpl
import pl.edu.wat.droman.callback.CompletionCallbackWithImpl
import pl.edu.wat.droman.ui.FeedbackUtils
import pl.edu.wat.droman.ui.LogLevel
import pl.edu.wat.droman.ui.flightcontrol.handler.AircraftControllers

abstract class Command(val type: String, val completionCallback: CommonCallbacks.CompletionCallback<DJIError>) {
    abstract fun exec(aircraftControllers: AircraftControllers)

    companion object {
        const val TAG = "Command"
    }
}