package pl.edu.wat.droman.ui.flightcontrol.handler.waypoint

import androidx.lifecycle.Observer
import dji.common.error.DJIError
import dji.common.mission.waypoint.WaypointMissionDownloadEvent
import dji.common.mission.waypoint.WaypointMissionExecutionEvent
import dji.common.mission.waypoint.WaypointMissionState
import dji.common.mission.waypoint.WaypointMissionUploadEvent
import dji.common.model.LocationCoordinate2D
import dji.sdk.flightcontroller.FlightController
import dji.sdk.mission.waypoint.WaypointMissionOperator
import dji.sdk.mission.waypoint.WaypointMissionOperatorListener
import pl.edu.wat.droman.CompletionCallbackHandler
import pl.edu.wat.droman.GlobalConfig.MAX_HEIGHT
import pl.edu.wat.droman.GlobalConfig.MAX_RADIUS
import pl.edu.wat.droman.data.model.FlightStatus
import pl.edu.wat.droman.data.model.mission.*
import pl.edu.wat.droman.ui.FeedbackUtils
import pl.edu.wat.droman.ui.LogLevel
import pl.edu.wat.droman.ui.flightcontrol.handler.CameraHandler
import pl.edu.wat.droman.ui.flightcontrol.handler.StatusHandler


class WaypointMissionHandler(
    private val statsHandler: StatusHandler,
    private val cameraHandler: CameraHandler,
    private val flightController: FlightController,
    private val waypointMissionOperator: WaypointMissionOperator
) {
    companion object {
        const val TAG = "WaypointMissionHandler"
    }

    private val statusObserver = Observer<FlightStatus> {
        if (it.isLandingConfirmationNeeded) {
            flightController.confirmLanding(CompletionCallbackHandler<DJIError>(TAG))
        }
    }

    private val waypointMissionFactory = WaypointMissionFactory()

    private val waypointMissionOperatorListener = object : WaypointMissionOperatorListener {
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

    init {
        statsHandler.status.observeForever(statusObserver)

        flightController.setMaxFlightHeight(
            MAX_HEIGHT,
            CompletionCallbackHandler<DJIError>(
                tag = TAG,
                success = {
                    FeedbackUtils.setResult(
                        string = "The maximum height is set to 500m",
                        level = LogLevel.DEBUG,
                        tag = TAG
                    )
                })
        )
        flightController.setMaxFlightRadius(
            MAX_RADIUS,
            CompletionCallbackHandler<DJIError>(
                tag = TAG,
                success = {
                    FeedbackUtils.setResult(
                        string = "The maximum radius is set to 500m",
                        level = LogLevel.DEBUG,
                        tag = TAG
                    )
                })
        )
        waypointMissionOperator.addListener(waypointMissionOperatorListener)
    }

    fun execLoadWaypointMission(mission: LoadWaypointCommand) {
        if (waypointMissionOperator.currentState == WaypointMissionState.READY_TO_UPLOAD
            || waypointMissionOperator.currentState == WaypointMissionState.READY_TO_EXECUTE
        ) {
            waypointMissionFactory.createWaypointMission(mission)?.let {
               val error = waypointMissionOperator.loadMission(it)
                if(error != null){
                    FeedbackUtils.setResult(error.toString(),level = LogLevel.ERROR, tag = TAG)
                }
                else {
                    FeedbackUtils.setResult("Success loading mission", level = LogLevel.DEBUG)
                }
            }

        } else {
            FeedbackUtils.setResult("The mission can be loaded only when the operator state is READY_TO_UPLOAD or READY_TO_EXECUTE");
        }
    }

    fun execSetHomeLocation(command: SetHomeLocationCommand) {
        flightController.setHomeLocation(
            LocationCoordinate2D(command.latitude, command.longitude),
            CompletionCallbackHandler<DJIError>(tag = TAG, success = {FeedbackUtils.setResult("Success setting home location", tag = TAG)})
        )
    }


    fun execUploadWaypointMission(mission: UploadWaypointCommand) {
        if (WaypointMissionState.READY_TO_RETRY_UPLOAD == waypointMissionOperator.currentState || WaypointMissionState.READY_TO_UPLOAD == waypointMissionOperator.currentState) {
            waypointMissionOperator.uploadMission(CompletionCallbackHandler<DJIError>(
                tag = TAG,
                success = {FeedbackUtils.setResult("Mission uploaded", tag = TAG)},
                failure = {FeedbackUtils.setResult("Mission upload failed $it", tag = TAG, level = LogLevel.ERROR)},
            ))
        } else {
            FeedbackUtils.setResult("Wait for mission to be loaded")
        }
    }

    fun execResumeWaypointMission(mission: ResumeWaypointCommand) {
        if (waypointMissionOperator.currentState == WaypointMissionState.EXECUTION_PAUSED) {
            waypointMissionOperator.resumeMission(
                CompletionCallbackHandler<DJIError>(
                    tag = TAG
                )
            )
        } else {
            FeedbackUtils.setResult(
                string = "The mission has not been interrupted",
                tag = TAG,
                level = LogLevel.WARN
            )
        }
    }

    fun execPauseWaypointMission(mission: PauseWaypointCommand) {
        if (waypointMissionOperator.currentState == WaypointMissionState.EXECUTING) {
            waypointMissionOperator.pauseMission(
                CompletionCallbackHandler<DJIError>(
                    tag = TAG
                )
            )
        } else {
            FeedbackUtils.setResult(
                string = "Mission is not executing",
                tag = TAG,
                level = LogLevel.WARN
            )
        }
    }

    fun execStopWaypointMission(mission: StopWaypointCommand) {
        if (waypointMissionOperator.currentState == WaypointMissionState.EXECUTING
            || waypointMissionOperator.currentState == WaypointMissionState.EXECUTION_PAUSED
        ) {
            waypointMissionOperator.stopMission(CompletionCallbackHandler<DJIError>(tag = TAG))
        } else {
            FeedbackUtils.setResult(
                string = "Mission is not executing",
                tag = TAG,
                level = LogLevel.WARN
            )

        }
    }

    fun execStartMission(mission: StartWaypointCommand) {
        if (waypointMissionOperator.currentState == WaypointMissionState.READY_TO_EXECUTE) {
            waypointMissionOperator.startMission(CompletionCallbackHandler<DJIError>(
                tag = TAG,
                success = {
                    FeedbackUtils.setResult("Mission started")
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

    fun destroy() {
        waypointMissionOperator.removeListener(waypointMissionOperatorListener)
        statsHandler.status.removeObserver(statusObserver)
    }
}