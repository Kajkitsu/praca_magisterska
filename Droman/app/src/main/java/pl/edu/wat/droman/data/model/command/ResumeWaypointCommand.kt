package pl.edu.wat.droman.data.model.command

import dji.common.error.DJIError
import dji.common.mission.waypoint.WaypointMissionState
import pl.edu.wat.droman.CompletionCallbackHandler
import pl.edu.wat.droman.ui.FeedbackUtils
import pl.edu.wat.droman.ui.LogLevel
import pl.edu.wat.droman.ui.flightcontrol.handler.AircraftControllers
import pl.edu.wat.droman.ui.flightcontrol.handler.CommandHandler

class ResumeWaypointCommand : Command(type) {
    companion object {
        const val type = "resume_waypoint_mission"
    }

    override fun exec(commandHandler: AircraftControllers) {
        if (commandHandler.waypointMissionOperator.currentState == WaypointMissionState.EXECUTION_PAUSED) {
            commandHandler.waypointMissionOperator.resumeMission(
                CompletionCallbackHandler<DJIError>(
                    tag = CommandHandler.TAG
                )
            )
        } else {
            FeedbackUtils.setResult(
                string = "The mission has not been interrupted",
                tag = CommandHandler.TAG,
                level = LogLevel.WARN
            )
        }
    }
}