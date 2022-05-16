package pl.edu.wat.droman.data.model.command

import dji.common.error.DJIError
import dji.common.mission.waypoint.WaypointMissionState
import pl.edu.wat.droman.ui.callback.CompletionCallbackImpl
import pl.edu.wat.droman.ui.FeedbackUtils
import pl.edu.wat.droman.ui.LogLevel
import pl.edu.wat.droman.ui.flightcontrol.handler.AircraftControllers
import pl.edu.wat.droman.ui.flightcontrol.handler.CommandHandler

class StopWaypointCommand : Command(type) {
    companion object {
        const val type = "stop_waypoint_mission"
    }

    override fun exec(commandHandler: AircraftControllers) {
        if (commandHandler.waypointMissionOperator.currentState == WaypointMissionState.EXECUTING
            || commandHandler.waypointMissionOperator.currentState == WaypointMissionState.EXECUTION_PAUSED
        ) {
            commandHandler.waypointMissionOperator.stopMission(
                CompletionCallbackImpl<DJIError>(
                    tag = CommandHandler.TAG
                )
            )
        } else {
            FeedbackUtils.setResult(
                string = "Mission is not executing",
                tag = CommandHandler.TAG,
                level = LogLevel.WARN
            )
        }
    }
}