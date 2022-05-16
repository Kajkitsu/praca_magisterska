package pl.edu.wat.droman.ui.flightcontrol.handler

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import dji.common.error.DJIError
import dji.common.flightcontroller.simulator.InitializationData
import dji.common.model.LocationCoordinate2D
import dji.sdk.flightcontroller.FlightController
import dji.sdk.mission.MissionControl
import pl.edu.wat.droman.GlobalConfig
import pl.edu.wat.droman.callback.CompletionCallbackImpl
import pl.edu.wat.droman.data.model.FlightStatus
import pl.edu.wat.droman.data.model.command.Command
import pl.edu.wat.droman.data.service.ReceiveService
import pl.edu.wat.droman.ui.FeedbackUtils
import pl.edu.wat.droman.ui.LogLevel
import pl.edu.wat.droman.ui.flightcontrol.handler.waypoint.WaypointMissionOperatorListenerImpl

class CommandHandler(
    cameraHandler: CameraHandler,
    private val statsHandler: StatusHandler,
    private val flightController: FlightController,
    receiveService: ReceiveService,
) {
    companion object {
        const val TAG = "MissionHandler"
    }

    private val waypointMissionOperator = MissionControl.getInstance().waypointMissionOperator

    private val aircraftControllers = AircraftControllers(
        cameraHandler,
        statsHandler,
        flightController,
        waypointMissionOperator
    );

    private val commandLiveData: LiveData<Command> = receiveService.getCommand()
    private val commandObserver = Observer<Command> {
        it.exec(aircraftControllers)
    }
    private val statusObserver = Observer<FlightStatus> {
        if (it.isLandingConfirmationNeeded) {
            flightController.confirmLanding(CompletionCallbackImpl<DJIError>(TAG))
        }
    }
    private val waypointMissionOperatorListener = WaypointMissionOperatorListenerImpl()


    init {
        commandLiveData.observeForever(commandObserver)
        statsHandler.status.observeForever(statusObserver)

        if (GlobalConfig.SIMULATOR_MODE) {
            flightController.simulator
                .start(
                    InitializationData.createInstance(LocationCoordinate2D(22.0, 113.0), 10, 10),
                    CompletionCallbackImpl<DJIError>(tag = TAG)
                )
        }

        setMaxFlightHeight(flightController)
        setMaxFlightRadius(flightController)
        waypointMissionOperator.addListener(waypointMissionOperatorListener)

    }

    private fun setMaxFlightHeight(flightController: FlightController) {
        flightController.setMaxFlightHeight(
            GlobalConfig.MAX_HEIGHT,
            CompletionCallbackImpl<DJIError>(
                tag = TAG,
                success = {
                    FeedbackUtils.setResult(
                        string = "The maximum height is set to 500m",
                        level = LogLevel.DEBUG,
                        tag = TAG
                    )
                })
        )
    }

    private fun setMaxFlightRadius(flightController: FlightController) {
        flightController.setMaxFlightRadius(
            GlobalConfig.MAX_RADIUS,
            CompletionCallbackImpl<DJIError>(
                tag = TAG,
                success = {
                    FeedbackUtils.setResult(
                        string = "The maximum radius is set to 500m",
                        level = LogLevel.DEBUG,
                        tag = TAG
                    )
                })
        )
    }

    fun destroy() {
        commandLiveData.removeObserver(commandObserver)
        waypointMissionOperator.removeListener(waypointMissionOperatorListener)
        statsHandler.status.removeObserver(statusObserver)
        if (GlobalConfig.SIMULATOR_MODE) {
            flightController.simulator.stop(CompletionCallbackImpl<DJIError>(tag = TAG))
        }


    }


}
