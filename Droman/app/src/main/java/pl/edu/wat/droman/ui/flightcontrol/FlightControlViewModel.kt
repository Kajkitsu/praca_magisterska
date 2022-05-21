package pl.edu.wat.droman.ui.flightcontrol

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import pl.edu.wat.droman.data.service.ReceiveService
import pl.edu.wat.droman.data.service.UpdateService
import pl.edu.wat.droman.ui.DjiApplication
import pl.edu.wat.droman.ui.FeedbackUtils
import pl.edu.wat.droman.ui.LogLevel
import pl.edu.wat.droman.ui.flightcontrol.handler.CameraHandler
import pl.edu.wat.droman.ui.flightcontrol.handler.CommandHandler
import pl.edu.wat.droman.ui.flightcontrol.handler.StatusHandler


class FlightControlViewModel(
    private var updateService: UpdateService,
    private var receiveService: ReceiveService
) : ViewModel() {

    private var cameraHandler: CameraHandler? = null
    private var statusHandler: StatusHandler? = null
    private var commandHandler: CommandHandler? = null

    companion object {
        const val TAG = "FlightControlViewModel"
    }

    init {
        DjiApplication.aircraftInstance?.let { aircraft ->
            FeedbackUtils.setResult("Aircraft found", level = LogLevel.DEBUG, tag = TAG)
            statusHandler = StatusHandler(aircraft.flightController, viewModelScope, updateService)
            cameraHandler = CameraHandler(aircraft.camera, viewModelScope, updateService)
            commandHandler = CommandHandler(
                cameraHandler!!,
                statusHandler!!,
                aircraft.flightController,
                receiveService
            )
        } ?: run {
            FeedbackUtils.setResult("Aircraft not found", level = LogLevel.WARN, tag = TAG)
        }
    }

    fun destroy() {
        commandHandler?.destroy()
        cameraHandler?.destroy()
        statusHandler?.destroy()
    }
}

