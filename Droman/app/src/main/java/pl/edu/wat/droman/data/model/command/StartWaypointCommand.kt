package pl.edu.wat.droman.data.model.command

import dji.common.error.DJIError
import dji.common.mission.waypoint.WaypointMissionState
import pl.edu.wat.droman.CompletionCallbackHandler
import pl.edu.wat.droman.ui.FeedbackUtils
import pl.edu.wat.droman.ui.LogLevel
import pl.edu.wat.droman.ui.flightcontrol.handler.AircraftControllers
import pl.edu.wat.droman.ui.flightcontrol.handler.CommandHandler

class StartWaypointCommand : Command(type) {
    companion object {
        const val type = "start_waypoint_mission"
    }

    override fun exec(commandHandler: AircraftControllers) {
        if (commandHandler.waypointMissionOperator.currentState == WaypointMissionState.READY_TO_EXECUTE) {
            commandHandler.waypointMissionOperator.startMission(CompletionCallbackHandler<DJIError>(
                tag = CommandHandler.TAG,
                success = {
                    FeedbackUtils.setResult("Mission started")
                }
            ))
        } else {
            FeedbackUtils.setResult(
                "Mission is not ready to execute",
                level = LogLevel.WARN,
                tag = CommandHandler.TAG
            )
        }
    }

}