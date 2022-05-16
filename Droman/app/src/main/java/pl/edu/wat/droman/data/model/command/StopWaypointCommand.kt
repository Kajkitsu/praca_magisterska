package pl.edu.wat.droman.data.model.command

import dji.common.mission.waypoint.WaypointMissionState
import pl.edu.wat.droman.ui.FeedbackUtils
import pl.edu.wat.droman.ui.LogLevel
import pl.edu.wat.droman.ui.flightcontrol.handler.AircraftControllers

class StopWaypointCommand : Command(type) {
    companion object {
        const val type = "stop_waypoint_mission"
    }

    override fun exec(aircraftControllers: AircraftControllers) {
        if (aircraftControllers.waypointMissionOperator.currentState == WaypointMissionState.EXECUTING
            || aircraftControllers.waypointMissionOperator.currentState == WaypointMissionState.EXECUTION_PAUSED
        ) {
            aircraftControllers.waypointMissionOperator.stopMission(
                getCompletionCallback()
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