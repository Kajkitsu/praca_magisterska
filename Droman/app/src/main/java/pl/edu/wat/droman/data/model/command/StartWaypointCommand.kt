package pl.edu.wat.droman.data.model.command

import dji.common.error.DJIError
import dji.common.mission.waypoint.WaypointMissionState
import dji.common.util.CommonCallbacks
import pl.edu.wat.droman.callback.CompletionCallbackImpl
import pl.edu.wat.droman.ui.FeedbackUtils
import pl.edu.wat.droman.ui.LogLevel
import pl.edu.wat.droman.ui.flightcontrol.handler.AircraftControllers

class StartWaypointCommand(completionCallback : CommonCallbacks.CompletionCallback<DJIError>) : Command(type, completionCallback) {
    companion object {
        const val type = "start_waypoint_mission"
    }

    override fun exec(aircraftControllers: AircraftControllers) {
        if (aircraftControllers.waypointMissionOperator.currentState == WaypointMissionState.READY_TO_EXECUTE) {
            aircraftControllers.waypointMissionOperator.startMission(CompletionCallbackImpl<DJIError>(
                tag = TAG,
                success = {
                    FeedbackUtils.setResult("Mission started", TAG)
                }
            ))
        } else {
            FeedbackUtils.setResult(
                "Mission is not ready to execute",
                level = LogLevel.WARN,
                tag = TAG
            )
        }
    }

}