package pl.edu.wat.droman.data.model.command

import dji.common.error.DJIError
import dji.common.mission.waypoint.WaypointMissionState
import pl.edu.wat.droman.callback.CompletionCallbackImpl
import pl.edu.wat.droman.ui.FeedbackUtils
import pl.edu.wat.droman.ui.LogLevel
import pl.edu.wat.droman.ui.flightcontrol.handler.AircraftControllers
import pl.edu.wat.droman.ui.flightcontrol.handler.CommandHandler

class UploadWaypointCommand : Command(type) {
    companion object {
        const val type = "upload_waypoint_mission"
    }

    override fun exec(commandHandler: AircraftControllers) {
        if (WaypointMissionState.READY_TO_RETRY_UPLOAD == commandHandler.waypointMissionOperator.currentState || WaypointMissionState.READY_TO_UPLOAD == commandHandler.waypointMissionOperator.currentState) {
            commandHandler.waypointMissionOperator.uploadMission(
                CompletionCallbackImpl<DJIError>(
                    tag = CommandHandler.TAG,
                    success = {
                        FeedbackUtils.setResult(
                            "Mission uploaded",
                            tag = CommandHandler.TAG
                        )
                    },
                    failure = {
                        FeedbackUtils.setResult(
                            "Mission upload failed $it",
                            tag = CommandHandler.TAG,
                            level = LogLevel.ERROR
                        )
                    },
                )
            )
        } else {
            FeedbackUtils.setResult("Wait for mission to be loaded")
        }
    }

}