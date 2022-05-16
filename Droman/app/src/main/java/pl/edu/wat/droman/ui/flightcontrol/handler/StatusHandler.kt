package pl.edu.wat.droman.ui.flightcontrol.handler

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dji.sdk.flightcontroller.FlightController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pl.edu.wat.droman.GlobalConfig
import pl.edu.wat.droman.data.model.FlightStatus
import pl.edu.wat.droman.data.service.UpdateService

class StatusHandler(
    private val flightController: FlightController,
    viewModelScope: CoroutineScope,
    updateService: UpdateService
) {
    companion object {
        const val TAG = "StatusHandler"
    }

    private var lastUpdateCnt = 0
    private lateinit var lastStatus: FlightStatus
    private var _status: MutableLiveData<FlightStatus> = MutableLiveData()
    var status: LiveData<FlightStatus> = _status

    init {
        flightController
            .setStateCallback { state ->
                viewModelScope.launch(Dispatchers.IO) {
                    delay(5000) //to wait until connection establish
                    lastStatus = FlightStatus.gen(state)
                    if (lastUpdateCnt == 0) {
                        updateService.saveCallback(lastStatus)
                    }
                    _status.postValue(lastStatus)
                    lastUpdateCnt++
                    lastUpdateCnt %= GlobalConfig.STATE_RATE_LIMIT
                }
            }
    }


    fun getLastStatus(): FlightStatus {
        return status.value!!
    }

    fun destroy() {
        flightController.setStateCallback(null)
    }
}


