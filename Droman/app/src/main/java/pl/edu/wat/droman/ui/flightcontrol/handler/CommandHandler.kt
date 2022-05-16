package pl.edu.wat.droman.ui.flightcontrol.handler

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import dji.sdk.flightcontroller.FlightController
import dji.sdk.mission.MissionControl
import pl.edu.wat.droman.data.model.command.Command
import pl.edu.wat.droman.data.service.ReceiveService

class CommandHandler(
    cameraHandler: CameraHandler,
    statsHandler: StatusHandler,
    flightController: FlightController,
    receiveService: ReceiveService,
) {
    companion object {
        const val TAG = "MissionHandler"
    }

    private val aircraftControllers = AircraftControllers(
        cameraHandler,statsHandler,flightController,
        MissionControl.getInstance().waypointMissionOperator
    );

    private val commandLiveData: LiveData<Command> = receiveService.command
    private val observer = Observer<Command> {
        it.exec(aircraftControllers)
    }

    init {
        commandLiveData.observeForever(observer)
    }

    fun destroy() {
        commandLiveData.removeObserver(observer)
    }


}
