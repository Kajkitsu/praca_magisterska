package pl.edu.wat.droman.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dji.sdk.sdkmanager.DJISDKManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.edu.wat.droman.data.model.MqttCredentials
import pl.edu.wat.droman.data.service.MqttService
import pl.edu.wat.droman.ui.DjiApplication
import java.util.*

@SuppressLint("StaticFieldLeak")
class MainViewModel : ViewModel() {
    companion object {
        val TAG = "MainViewModel"
    }

    var mqttService: MqttService? = null
    private val _connect = MutableLiveData<Boolean>()
    val connectState: LiveData<Boolean> = _connect

    private var _clientId = MutableLiveData<String>()
    var clientId:LiveData<String> = _clientId

    fun fetchClientId(){
        viewModelScope.launch(Dispatchers.IO) {
            _clientId.postValue(DjiApplication.getClientId())
        }
    }

    fun validateConnection(username: String, password: String, uri: String, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            mqttService = MqttService(
                context, MqttCredentials(
                    "tcp://$uri",
                    DJISDKManager.getInstance()?.product?.model?.displayName + ":" + UUID.randomUUID(),
                    username,
                    password
                )
            )
            if (mqttService != null) {
                val res = mqttService!!.validate()
                _connect.postValue(res)
            } else {
                Log.e(TAG, "Error mqttService not set")
                _connect.postValue(false)
            }
        }
    }

    fun invalidate() {
        viewModelScope.launch(Dispatchers.IO) {
            mqttService?.destroy()
            mqttService = null
            _connect.postValue(false)
        }
    }
}