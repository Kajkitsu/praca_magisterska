package pl.edu.wat.droman.data.model.command

import dji.common.mission.waypoint.WaypointMissionState
import pl.edu.wat.droman.ui.FeedbackUtils
import pl.edu.wat.droman.ui.LogLevel
import pl.edu.wat.droman.ui.flightcontrol.handler.AircraftControllers

class PauseWaypointCommand : Command(type) {
    companion object {
        const val type = "pause_waypoint_mission"
    }

    override fun exec(aircraftControllers: AircraftControllers) {
        if (aircraftControllers.waypointMissionOperator.currentState == WaypointMissionState.EXECUTING) {
            aircraftControllers.waypointMissionOperator.pauseMission(getCompletionCallback())
        } else {
            FeedbackUtils.setResult(
                string = "Mission is not executing",
                tag = TAG,
                level = LogLevel.WARN
            )
        }
    }
}