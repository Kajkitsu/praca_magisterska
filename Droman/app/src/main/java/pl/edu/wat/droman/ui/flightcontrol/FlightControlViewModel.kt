package pl.edu.wat.droman.ui.flightcontrol

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dji.sdk.base.BaseProduct
import dji.sdk.products.Aircraft
import dji.sdk.sdkmanager.DJISDKManager
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

    private val djiManager: DJISDKManager = DJISDKManager.getInstance()
    private var lastStateUpdate = 0
    private var supportDownloadMediaMode = true
    private var mediaManager = DjiApplication.aircraftInstance?.camera?.mediaManager

    private var cameraHandler: CameraHandler? = null
    private var statusHandler: StatusHandler? = null
    private var commandHandler: CommandHandler? = null


    companion object {
        val TAG = "FlightControlViewModel"
    }

    init {
        getAircraft()?.let { aircraft ->
            FeedbackUtils.setResult("Aircraft found", level = LogLevel.DEBUG)
            statusHandler = StatusHandler(aircraft.flightController, viewModelScope, updateService)
            cameraHandler = CameraHandler(aircraft.camera, viewModelScope, updateService)
            commandHandler = CommandHandler(
                cameraHandler!!,
                statusHandler!!,
                aircraft.flightController,
                receiveService
            )
        } ?: run {
            FeedbackUtils.setResult("Aircraft not found", level = LogLevel.WARN)
        }
    }

    //
    fun destroy() {
        commandHandler?.destroy()
        cameraHandler?.destroy()
        statusHandler?.destroy()
        receiveService.destroy()
    }


    fun getAircraft(): Aircraft? {
        val product: BaseProduct? = djiManager.product
        if (product is Aircraft) {
            return product
        }
        return null
//        throw RuntimeException("djiManager.product == null")
    }

}

