package pl.edu.wat.droman.data.model.command

import dji.common.mission.waypoint.WaypointMissionState
import pl.edu.wat.droman.ui.FeedbackUtils
import pl.edu.wat.droman.ui.LogLevel
import pl.edu.wat.droman.ui.flightcontrol.handler.AircraftControllers

class ResumeWaypointCommand : Command(type) {
    companion object {
        const val type = "resume_waypoint_mission"
    }

    override fun exec(aircraftControllers: AircraftControllers) {
        if (aircraftControllers.waypointMissionOperator.currentState == WaypointMissionState.EXECUTION_PAUSED) {
            aircraftControllers.waypointMissionOperator.resumeMission(
                getCompletionCallback()
            )
        } else {
            FeedbackUtils.setResult(
                string = "The mission has not been interrupted",
                tag = TAG,
                level = LogLevel.WARN
            )
        }
    }
}