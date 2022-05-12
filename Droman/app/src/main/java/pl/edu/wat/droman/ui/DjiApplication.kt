package pl.edu.wat.droman.ui

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import pl.edu.wat.droman.ui.OnDJIUSBAttachedReceiver
import android.content.IntentFilter
import androidx.lifecycle.MutableLiveData
import pl.edu.wat.droman.ui.djiconnectioncontrol.DJIConnectionControlActivity
import androidx.multidex.MultiDex
import com.secneo.sdk.Helper
import pl.edu.wat.droman.ui.DjiApplication
import dji.sdk.base.BaseProduct
import dji.sdk.sdkmanager.BluetoothProductConnector
import com.squareup.otto.Bus
import com.squareup.otto.ThreadEnforcer
import kotlin.jvm.Synchronized
import dji.sdk.sdkmanager.DJISDKManager
import dji.sdk.products.Aircraft
import pl.edu.wat.droman.getCallback
import pl.edu.wat.droman.getOrAwaitValue
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
        private var product: BaseProduct? = null
        private var bluetoothConnector: BluetoothProductConnector? = null
        val eventBus = Bus(ThreadEnforcer.ANY)
        var instance: DjiApplication? = null
            private set

        /**
         * Gets instance of the specific product connected after the
         * API KEY is successfully validated. Please make sure the
         * API_KEY has been added in the Manifest
         */
        @get:Synchronized
        private val productInstance: BaseProduct?
            get() {
                product = DJISDKManager.getInstance().product
                return product
            }

        @get:Synchronized
        val clientId: String by lazy {
            var serialNumber:String? = null
            aircraftInstance
                ?.flightController
                ?.getSerialNumber(getCallback(tag = FlightControlViewModelFactory.TAG, success = {serialNumber = it}))
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