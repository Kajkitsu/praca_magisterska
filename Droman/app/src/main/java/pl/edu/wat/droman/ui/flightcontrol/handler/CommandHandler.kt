package pl.edu.wat.droman.ui.flightcontrol.handler

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import dji.common.error.DJIError
import dji.sdk.flightcontroller.FlightController
import dji.sdk.mission.MissionControl
import pl.edu.wat.droman.CompletionCallbackHandler
import pl.edu.wat.droman.data.model.mission.*
import pl.edu.wat.droman.data.service.ReceiveService
import pl.edu.wat.droman.ui.FeedbackUtils
import pl.edu.wat.droman.ui.flightcontrol.handler.waypoint.WaypointMissionHandler

class CommandHandler(
    private val cameraHandler: CameraHandler,
    private val statsHandler: StatusHandler,
    missionControl: MissionControl,
    private val flightController: FlightController,
    receiveService: ReceiveService,
) {
    companion object {
        const val TAG = "MissionHandler"
    }

    private val waypointMissionHandler = WaypointMissionHandler(
        statsHandler,
        cameraHandler,
        flightController,
        MissionControl.getInstance().waypointMissionOperator
    );

    private val commandLiveData: LiveData<Command> = receiveService.command
    private val observer = Observer<Command> {
        when (it) {
            is ShootPhotoCommand -> {
                execShootPhoto(it)
            }
            is StartGoHomeCommand -> {
                execGoHomeMission(it)
            }
            is StopGoHomeCommand -> {
                execStopGoHomeMission(it)
            }
            is LandCommand -> {
                execLandMission(it)
            }
            is TakeOffCommand -> {
                execTakeOffMission(it)
            }
            is StopWaypointCommand -> {
                waypointMissionHandler.execStopWaypointMission(it)
            }
            is PauseWaypointCommand -> {
                waypointMissionHandler.execPauseWaypointMission(it)
            }
            is ResumeWaypointCommand -> {
                waypointMissionHandler.execResumeWaypointMission(it)
            }
            is StartWaypointCommand -> {
                waypointMissionHandler.execStartMission(it)
            }
            is LoadWaypointCommand -> {
                waypointMissionHandler.execLoadWaypointMission(it)
            }
            is UploadWaypointCommand -> {
                waypointMissionHandler.execUploadWaypointMission(it)
            }
            is StartMotorsCommand -> {
                execStartMotorsMission(it)
            }
            is SetHomeLocationCommand -> {
                waypointMissionHandler.execSetHomeLocation(it)
            }
            is StopMotorsCommand -> {
                execStopMotorsMission(it)
            }
            else -> {
                FeedbackUtils.setResult("Not implemented yet for mission \"${it.type}\" handler")
            }
        }
    }

    init {
        commandLiveData.observeForever(observer)
    }

    fun destroy() {
        commandLiveData.removeObserver(observer)
        waypointMissionHandler?.destroy()
    }

    private fun execStopMotorsMission(mission: StopMotorsCommand) {
        val status = statsHandler.getLastStatus()
        if (!status.isFlying && status.motorsOn) {
            flightController.turnOffMotors(CompletionCallbackHandler<DJIError>(TAG))
        } else {
            FeedbackUtils.setResult("Forbidden state can't stop motors")
        }
    }

    private fun execStartMotorsMission(mission: StartMotorsCommand) {
        val status = statsHandler.getLastStatus()
        if (!status.isFlying && !status.motorsOn) {
            flightController.turnOnMotors(CompletionCallbackHandler<DJIError>(TAG))
        } else {
            FeedbackUtils.setResult("Forbidden state can't start motors")
        }
    }

    private fun execStopGoHomeMission(mission: StopGoHomeCommand) {
        val status = statsHandler.getLastStatus()
        if (status.isFlying && status.isHomeLocationSet && status.isGoingHome) {
            flightController.cancelGoHome(CompletionCallbackHandler<DJIError>(TAG))
        } else {
            FeedbackUtils.setResult("Forbidden state can't stop going home")
        }
    }

    private fun execTakeOffMission(mission: TakeOffCommand) {
        val status = statsHandler.getLastStatus()
        if (!status.isFlying && !status.motorsOn) {
            flightController.startTakeoff(CompletionCallbackHandler<DJIError>(TAG))
        } else {
            FeedbackUtils.setResult("Forbidden state can't start take off")
        }
    }

    private fun execLandMission(mission: LandCommand) {
        val status = statsHandler.getLastStatus()
        if (status.isFlying && !status.isGoingHome) {
            flightController.startLanding(CompletionCallbackHandler<DJIError>(TAG))
        } else {
            FeedbackUtils.setResult("Forbidden state can't start landing")
        }
    }

    private fun execGoHomeMission(missionStart: StartGoHomeCommand) {
        val status = statsHandler.getLastStatus()
        if (status.isFlying && status.isHomeLocationSet && !status.isGoingHome) {
            flightController.startGoHome(CompletionCallbackHandler<DJIError>(TAG))
        } else {
            FeedbackUtils.setResult("Forbidden state can't start going home")
        }
    }


    private fun execShootPhoto(mission: ShootPhotoCommand) {
        cameraHandler.shootPhoto()
    }

}
