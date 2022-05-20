package pl.edu.wat.droman.data.model.command

import dji.common.error.DJIError
import dji.common.mission.waypoint.WaypointMissionState
import dji.common.util.CommonCallbacks
import pl.edu.wat.droman.ui.FeedbackUtils
import pl.edu.wat.droman.ui.LogLevel
import pl.edu.wat.droman.ui.flightcontrol.handler.AircraftControllers

class ResumeWaypointCommand(completionCallback : CommonCallbacks.CompletionCallback<DJIError>) : Command(type, completionCallback) {
    companion object {
        const val type = "resume_waypoint_mission"
    }

    override fun exec(aircraftControllers: AircraftControllers) {
        if (aircraftControllers.waypointMissionOperator.currentState == WaypointMissionState.EXECUTION_PAUSED) {
            aircraftControllers.waypointMissionOperator.resumeMission(
                completionCallback
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