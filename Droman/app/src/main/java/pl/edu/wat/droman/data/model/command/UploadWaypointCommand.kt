package pl.edu.wat.droman.data.model.command

import dji.common.error.DJIError
import dji.common.mission.waypoint.WaypointMissionState
import dji.common.util.CommonCallbacks
import pl.edu.wat.droman.callback.CompletionCallbackImpl
import pl.edu.wat.droman.ui.FeedbackUtils
import pl.edu.wat.droman.ui.LogLevel
import pl.edu.wat.droman.ui.flightcontrol.handler.AircraftControllers

class UploadWaypointCommand(completionCallback : CommonCallbacks.CompletionCallback<DJIError>) : Command(type, completionCallback) {
    companion object {
        const val type = "upload_waypoint_mission"
    }

    override fun exec(aircraftControllers: AircraftControllers) {
        if (WaypointMissionState.READY_TO_RETRY_UPLOAD == aircraftControllers.waypointMissionOperator.currentState || WaypointMissionState.READY_TO_UPLOAD == aircraftControllers.waypointMissionOperator.currentState) {
            aircraftControllers.waypointMissionOperator.uploadMission(
                CompletionCallbackImpl<DJIError>(
                    tag = TAG,
                    success = {
                        FeedbackUtils.setResult(
                            "Mission uploaded",
                            tag = TAG
                        )
                    },
                    failure = {
                        FeedbackUtils.setResult(
                            "Mission upload failed $it",
                            tag = TAG,
                            level = LogLevel.ERROR
                        )
                    },
                )
            )
        } else {
            FeedbackUtils.setResult("Wait for mission to be loaded", TAG)
        }
    }

}