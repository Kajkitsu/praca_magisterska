package pl.edu.wat.droman.ui.main

import dji.common.error.DJIError
import dji.common.error.DJISDKError
import dji.sdk.base.BaseComponent
import dji.sdk.base.BaseProduct
import dji.sdk.sdkmanager.DJISDKInitEvent
import dji.sdk.sdkmanager.DJISDKManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pl.edu.wat.droman.ui.FeedbackUtils
import pl.edu.wat.droman.ui.LogLevel
import java.util.concurrent.atomic.AtomicBoolean

class RegistrationCallback(
    private val registrationSuccess: () -> Unit,
    private val deviceConnected: () -> Unit,
    private val deviceDisconnected: () -> Unit,
) :
    DJISDKManager.SDKManagerCallback {

    companion object {
        const val TAG = "RegistrationCallback"
    }

    private val isRegistrationInProgress = AtomicBoolean(false)

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
            registrationSuccess.invoke()
        } else {
            FeedbackUtils.setResult(
                tag =
                TAG, string =
                "SDK registration failed, check network and retry!" + error.description, level =
                LogLevel.ERROR
            )
        }
    }

    override fun onProductDisconnect() {
        deviceDisconnected.invoke()
    }

    override fun onProductConnect(product: BaseProduct) {
        deviceConnected.invoke()
    }

    override fun onProductChanged(product: BaseProduct) {}
    override fun onComponentChange(
        key: BaseProduct.ComponentKey?,
        oldComponent: BaseComponent?,
        newComponent: BaseComponent?
    ) {
        FeedbackUtils.setResult(tag = TAG, string = "$key changed")
    }

    override fun onInitProcess(event: DJISDKInitEvent, totalProcess: Int) {
//        FeedbackUtils.setResultToToastAndLog(tag =
//, string =
//            applicationContext,
//            "onInitProcess," + event + "totalProcess," + totalProcess
//        )
    }

    override fun onDatabaseDownloadProgress(current: Long, total: Long) {
//        FeedbackUtils.setResultToToastAndLog(tag =
//, string =
//            applicationContext,
//            "onDatabaseDownloadProgress" + (100 * current / total).toInt()
//        )
    }


}