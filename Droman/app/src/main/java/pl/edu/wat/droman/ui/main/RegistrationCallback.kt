package pl.edu.wat.droman.ui.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dji.common.error.DJIError
import dji.common.error.DJISDKError
import dji.sdk.base.BaseComponent
import dji.sdk.base.BaseProduct
import dji.sdk.sdkmanager.DJISDKInitEvent
import dji.sdk.sdkmanager.DJISDKManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pl.edu.wat.droman.ui.LogType
import pl.edu.wat.droman.ui.toastAndLog
import java.util.concurrent.atomic.AtomicBoolean

class RegistrationCallback(private val applicationContext: Context) :
    DJISDKManager.SDKManagerCallback {

    companion object {
        const val TAG = "RegistrationCallback"
    }

    private val isRegistrationInProgress = AtomicBoolean(false)
    private val deviceConnected = AtomicBoolean(false)

    fun startSDKRegistration(mainActivity: MainActivity) {
        if (isRegistrationInProgress.compareAndSet(false, true)) {
            this.let { registrationCallback ->
                GlobalScope.launch(Dispatchers.IO) {
                    DJISDKManager.getInstance().registerApp(mainActivity, registrationCallback)
                }
            }
        }

    }

    override fun onRegister(error: DJIError) {
        isRegistrationInProgress.set(false)
        if (error === DJISDKError.REGISTRATION_SUCCESS) {
            DJISDKManager.getInstance().startConnectionToProduct()
            toastAndLog(TAG, applicationContext, "SDK registration succeeded!")
        } else {
            toastAndLog(
                TAG,
                applicationContext,
                "SDK registration failed, check network and retry!" + error.description,
                LogType.ERROR
            )
        }
    }

    override fun onProductDisconnect() {
        deviceConnected.set(false)
        toastAndLog(TAG, applicationContext, "product disconnect!", LogType.WARN)
    }

    override fun onProductConnect(product: BaseProduct) {
        deviceConnected.set(true)
        toastAndLog(TAG, applicationContext, "product connect!")
    }

    fun isConnected(): Boolean {
        return deviceConnected.get()
    }

    override fun onProductChanged(product: BaseProduct) {}
    override fun onComponentChange(
        key: BaseProduct.ComponentKey?,
        oldComponent: BaseComponent?,
        newComponent: BaseComponent?
    ) {
        toastAndLog(TAG, applicationContext, "$key changed")
    }

    override fun onInitProcess(event: DJISDKInitEvent, totalProcess: Int) {
//        toastAndLog(
//            TAG,
//            applicationContext,
//            "onInitProcess," + event + "totalProcess," + totalProcess
//        )
    }

    override fun onDatabaseDownloadProgress(current: Long, total: Long) {
//        toastAndLog(
//            TAG,
//            applicationContext,
//            "onDatabaseDownloadProgress" + (100 * current / total).toInt()
//        )
    }


}