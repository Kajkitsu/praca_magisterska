package pl.edu.wat.droman.ui

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import androidx.multidex.MultiDex
import com.secneo.sdk.Helper
import com.squareup.otto.Bus
import com.squareup.otto.ThreadEnforcer
import dji.sdk.base.BaseProduct
import dji.sdk.products.Aircraft
import dji.sdk.sdkmanager.BluetoothProductConnector
import dji.sdk.sdkmanager.DJISDKManager
import kotlinx.coroutines.MainScope
import pl.edu.wat.droman.ui.callback.CompletionCallbackWithImpl
import pl.edu.wat.droman.ui.djiconnectioncontrol.DJIConnectionControlActivity
import pl.edu.wat.droman.ui.flightcontrol.FlightControlViewModelFactory

class DjiApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val br: BroadcastReceiver = OnDJIUSBAttachedReceiver()
        val filter = IntentFilter()
        filter.addAction(DJIConnectionControlActivity.ACCESSORY_ATTACHED)
        registerReceiver(br, filter)
    }

    override fun attachBaseContext(paramContext: Context) {
        super.attachBaseContext(paramContext)
        MultiDex.install(this)
        Helper.install(this@DjiApplication)
        instance = this
    }

    companion object {
        val mainScope = MainScope()
        private var product: BaseProduct? = null
        private var bluetoothConnector: BluetoothProductConnector? = null
        val eventBus = Bus(ThreadEnforcer.ANY)
        var instance: DjiApplication? = null
            private set


        @get:Synchronized
        private val productInstance: BaseProduct?
            get() {
                product = DJISDKManager.getInstance().product
                return product
            }

        @get:Synchronized
        val clientId: String by lazy {
            var serialNumber: String? = null
            aircraftInstance
                ?.flightController
                ?.getSerialNumber(
                    CompletionCallbackWithImpl(
                        tag = FlightControlViewModelFactory.TAG,
                        success = { serialNumber = it })
                )
            while (serialNumber == null) {
                Thread.sleep(10)
            }
            return@lazy serialNumber!!
        }


        @get:Synchronized
        val bluetoothProductConnector: BluetoothProductConnector?
            get() {
                bluetoothConnector = DJISDKManager.getInstance().bluetoothProductConnector
                return bluetoothConnector
            }
        val isAircraftConnected: Boolean
            get() = productInstance != null && productInstance is Aircraft

        @get:Synchronized
        val aircraftInstance: Aircraft?
            get() = if (!isAircraftConnected) {
                null
            } else productInstance as Aircraft?
    }
}