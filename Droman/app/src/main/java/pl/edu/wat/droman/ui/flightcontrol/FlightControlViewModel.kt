package pl.edu.wat.droman.ui.flightcontrol

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dji.sdk.base.BaseProduct
import dji.sdk.products.Aircraft
import dji.sdk.sdkmanager.DJISDKManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.edu.wat.droman.data.service.UpdateService

class FlightControlViewModel(
    private var updateService: UpdateService,
) : ViewModel() {

    private var aircraft: Aircraft? = null
    private val djiManager: DJISDKManager = DJISDKManager.getInstance()


    companion object {
        val TAG = "FlightControlViewModel"
    }

    init {
        aircraft = getAircraft()

        aircraft?.flightController
            ?.setStateCallback { state ->
                viewModelScope.launch(Dispatchers.IO) {
                    updateService.saveCallback(state)
                }
            }
    }


    fun getAircraft(): Aircraft? {
        val product: BaseProduct? = djiManager.product
        if (product is Aircraft) {
            return product
        }
        return null
    }

}