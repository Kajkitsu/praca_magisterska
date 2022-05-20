package pl.edu.wat.droman.data.model.command

import dji.common.error.DJIError
import dji.common.mission.waypoint.WaypointMissionState
import dji.common.util.CommonCallbacks
import pl.edu.wat.droman.ui.FeedbackUtils
import pl.edu.wat.droman.ui.LogLevel
import pl.edu.wat.droman.ui.flightcontrol.handler.AircraftControllers

class StopWaypointCommand(completionCallback : CommonCallbacks.CompletionCallback<DJIError>) : Command(type, completionCallback) {
    companion object {
        const val type = "stop_waypoint_mission"
    }

    override fun exec(aircraftControllers: AircraftControllers) {
        if (aircraftControllers.waypointMissionOperator.currentState == WaypointMissionState.EXECUTING
            || aircraftControllers.waypointMissionOperator.currentState == WaypointMissionState.EXECUTION_PAUSED
        ) {
            aircraftControllers.waypointMissionOperator.stopMission(
                completionCallback
            )
        } else {
            FeedbackUtils.setResult(
                string = "Mission is not executing",
                tag = TAG,
                level = LogLevel.WARN
            )
        }
    }
}