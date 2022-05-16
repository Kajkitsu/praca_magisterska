package pl.edu.wat.droman.ui.flightcontrol.handler.waypoint

import dji.common.error.DJIError
import dji.common.mission.waypoint.WaypointMissionDownloadEvent
import dji.common.mission.waypoint.WaypointMissionExecutionEvent
import dji.common.mission.waypoint.WaypointMissionState
import dji.common.mission.waypoint.WaypointMissionUploadEvent
import dji.sdk.mission.waypoint.WaypointMissionOperatorListener
import pl.edu.wat.droman.ui.FeedbackUtils

class WaypointMissionOperatorListenerImpl : WaypointMissionOperatorListener {
    override fun onDownloadUpdate(waypointMissionDownloadEvent: WaypointMissionDownloadEvent) {}
    override fun onUploadUpdate(waypointMissionUploadEvent: WaypointMissionUploadEvent) {
        if (waypointMissionUploadEvent.error != null) {
            FeedbackUtils.setResult(waypointMissionUploadEvent.error!!.description)
        } else {
            if (waypointMissionUploadEvent.previousState == WaypointMissionState.UPLOADING
                && waypointMissionUploadEvent.currentState == WaypointMissionState.READY_TO_EXECUTE
            ) {
                FeedbackUtils.setResult("Mission is uploaded successfully")
            }
        }
    }

    override fun onExecutionUpdate(waypointMissionExecutionEvent: WaypointMissionExecutionEvent) {}
    override fun onExecutionStart() {}
    override fun onExecutionFinish(error: DJIError?) {}
}