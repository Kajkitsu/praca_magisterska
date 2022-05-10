package pl.edu.wat.droman.ui.flightcontrol

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dji.common.error.DJIError
import dji.common.util.CommonCallbacks
import dji.sdk.base.BaseProduct
import dji.sdk.products.Aircraft
import dji.sdk.sdkmanager.DJISDKManager
import pl.edu.wat.droman.data.ETopic
import pl.edu.wat.droman.data.datasource.MqttDto
import pl.edu.wat.droman.data.model.MqttCredentials
import pl.edu.wat.droman.data.service.MqttService
import pl.edu.wat.droman.data.service.UpdateService
import pl.edu.wat.droman.getOrAwaitValue

class FlightControlViewModelFactory(
    private val username: String,
    private val password: String,
    private val ipAddress: String,
    private val context: Context
) : ViewModelProvider.Factory {
    companion object {
        const val TAG = "FlightControlViewModelFactory"
    }

    private val djiManager: DJISDKManager = DJISDKManager.getInstance()


    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        val clientId = getClientId();

        if (modelClass.isAssignableFrom(FlightControlViewModel::class.java)) {
            val mqttService = MqttService(
                context = context,
                mqttCredentials = MqttCredentials("tcp://$ipAddress", clientId, username, password),
                lastWill = MqttDto(ETopic.LAST_WILL, clientId),
                birth = MqttDto(ETopic.BIRTH, clientId)
            )
            return FlightControlViewModel(
                updateService = UpdateService(
                    mqttService.getTopic(ETopic.STATE.path + "/" + clientId),
                    mqttService.getTopic(ETopic.PICTURE.path + "/" + clientId)
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

    private fun getClientId(): String {
        val serialNumber: MutableLiveData<String> = MutableLiveData<String>()

        getAircraft()
            ?.flightController
            ?.getSerialNumber(getState(serialNumber))

        return serialNumber.getOrAwaitValue(time = 1, failure = {return@getOrAwaitValue "noSerialId"} )
    }

    private fun getState(serialNumber: MutableLiveData<String>): CommonCallbacks.CompletionCallbackWith<String> =
        object : CommonCallbacks.CompletionCallbackWith<String> {
            override fun onSuccess(p0: String?) {
                serialNumber.postValue(p0)
            }

            override fun onFailure(p0: DJIError?) {
                Log.e(TAG, p0.toString())
            }
        }

    private fun getAircraft(): Aircraft? {
        val product: BaseProduct? = djiManager.product
        if (product is Aircraft) {
            return product
        }
        return null
    }

}