package pl.edu.wat.droman.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.dji.frame.util.V_JsonUtil
import dji.sdk.sdkmanager.DJISDKManager
import pl.edu.wat.droman.GlobalConfig
import pl.edu.wat.droman.R
import pl.edu.wat.droman.afterTextChanged
import pl.edu.wat.droman.databinding.ActivityMainBinding
import pl.edu.wat.droman.ui.DjiApplication
import pl.edu.wat.droman.ui.FeedbackUtils
import pl.edu.wat.droman.ui.LogLevel
import pl.edu.wat.droman.ui.flightcontrol.FlightControlActivity

/** Main activity that displays three choices to user  */
class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
        var isStarted = false
            private set
        private val REQUIRED_PERMISSION_LIST = arrayOf(
            Manifest.permission.VIBRATE,  // Gimbal rotation
            Manifest.permission.INTERNET,  // API requests
            Manifest.permission.ACCESS_WIFI_STATE,  // WIFI connected products
            Manifest.permission.ACCESS_COARSE_LOCATION,  // Maps
            Manifest.permission.ACCESS_NETWORK_STATE,  // WIFI connected products
            Manifest.permission.ACCESS_FINE_LOCATION,  // Maps
            Manifest.permission.CHANGE_WIFI_STATE,  // Changing between WIFI and USB connection
            Manifest.permission.WRITE_EXTERNAL_STORAGE,  // Log files
            Manifest.permission.BLUETOOTH,  // Bluetooth connected products
            Manifest.permission.BLUETOOTH_ADMIN,  // Bluetooth connected products
            Manifest.permission.READ_EXTERNAL_STORAGE,  // Log files
            Manifest.permission.RECORD_AUDIO // Speaker accessory
        )
        private const val REQUEST_PERMISSION_CODE = 12345
    }

    private lateinit var registrationCallback: RegistrationCallback
    private lateinit var binding: ActivityMainBinding
    private val missingPermission: MutableList<String> = ArrayList()

    private lateinit var mainViewModel: MainViewModel

    private var nextActivity: Intent? = null

    private fun setProgressBarStat(force: Boolean = false) {
        if (mainViewModel.isDuringConnecting.value == true || force)
            binding.progressBar.visibility = View.VISIBLE
    }

    private fun setStartButtonState() {
        binding.btStartFlightMode.isEnabled =
            mainViewModel.clientId.value != null
                    && mainViewModel.connectState.value == true
                    && mainViewModel.isDuringConnecting.value == false
                    && mainViewModel.isDeviceConnected.value == true
    }

    private fun setCheckButtonState() {
        binding.btCheckConnection.isEnabled =
            (mainViewModel.clientId.value == null || mainViewModel.connectState.value == false)
                    && mainViewModel.isDuringConnecting.value == false
                    && mainViewModel.isDeviceConnected.value == true
    }

    private fun updateUI() {
        setProgressBarStat()
        setCheckButtonState()
        setStartButtonState()
    }

    private fun startFlightControl() {
        if (mainViewModel.connectState.value == true && mainViewModel.clientId.value != null) {
            nextActivity = Intent(this, FlightControlActivity::class.java)
            nextActivity!!.putExtra("username", binding.edMqttUsername.text.toString())
            nextActivity!!.putExtra("password", binding.edMqttPassword.text.toString())
            nextActivity!!.putExtra("ipAddress", binding.edMqttAddress.text.toString())
            nextActivity!!.putExtra("clientId", mainViewModel.clientId.value)
            startActivity(nextActivity)
            setProgressBarStat(true)
        } else {
            Toast.makeText(this, "Wrong state", Toast.LENGTH_LONG).show()
        }
    }


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        DjiApplication.eventBus.register(this)

        isStarted = true
        mainViewModel = ViewModelProvider(
            this,
            MainViewModelFactory()
        )[MainViewModel::class.java]

        binding.version.text = getVersionText()

        binding.btStartFlightMode
            .setOnClickListener {
                startFlightControl()
            }
        binding.btCheckConnection
            .setOnClickListener {
                checkConnection()
            }
        binding.edMqttUsername.afterTextChanged {
            mainViewModel.invalidate()
        }
        binding.edMqttPassword.afterTextChanged {
            mainViewModel.invalidate()
        }
        binding.edMqttAddress.afterTextChanged {
            mainViewModel.invalidate()
        }

        mainViewModel.isDeviceConnected.observe(this) {
            mainViewModel.fetchClientId()
            updateUI()
        }
        mainViewModel.clientId.observe(this) {
            updateUI()
            binding.clientId.text = "Client id: ${it ?: "not set"}"
        }
        mainViewModel.connectState.observe(this) { updateUI() }
        mainViewModel.isDuringConnecting.observe(this) {
            updateUI()
        }

        if (GlobalConfig.DEVELOPER_MODE) {
            initCredentialsValue()
        }

        registrationCallback = RegistrationCallback(
            registrationSuccess = {
                FeedbackUtils.setResult(
                    tag = RegistrationCallback.TAG,
                    string = "SDK registration succeeded!"
                )
            },
            deviceConnected = {
                FeedbackUtils.setResult(
                    tag = RegistrationCallback.TAG,
                    string = "product connect!"
                )
                mainViewModel.isDeviceConnected.postValue(true)
            },
            deviceDisconnected = {
                FeedbackUtils.setResult(
                    tag = RegistrationCallback.TAG,
                    string = "product disconnect!",
                    level = LogLevel.WARN
                )
                mainViewModel.isDeviceConnected.postValue(false)
            })

        checkAndRequestPermissions()
    }

    private fun checkConnection() {
        mainViewModel.validateConnection(
            binding.edMqttUsername.text.toString(),
            binding.edMqttPassword.text.toString(),
            binding.edMqttAddress.text.toString(),
            applicationContext
        )
        mainViewModel.fetchClientId()
    }


    private fun initCredentialsValue() {
        val metadata: Bundle = applicationContext.packageManager.getApplicationInfo(
            applicationContext.packageName,
            PackageManager.GET_META_DATA
        ).metaData
        binding.edMqttUsername.text.append(metadata.getString("mosquitto.user"))
        binding.edMqttPassword.text.append(metadata.getString("mosquitto.password"))
        binding.edMqttAddress.text.append(metadata.getString("mosquitto.ip"))
    }


    override fun onDestroy() {
        DJISDKManager.getInstance().destroy()
        isStarted = false
        DjiApplication.eventBus.unregister(this)
        super.onDestroy()
    }

    /**
     * Checks if there is any missing permissions, and
     * requests runtime permission if needed.
     */
    private fun checkAndRequestPermissions() {
        // Check for permissions
        for (eachPermission in REQUIRED_PERMISSION_LIST) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    eachPermission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                missingPermission.add(eachPermission)
            }
        }
        // Request for missing permissions
        if (missingPermission.isEmpty()) {
            registrationCallback.startSDKRegistration(this)
        } else {
            ActivityCompat.requestPermissions(
                this,
                missingPermission.toTypedArray(),
                REQUEST_PERMISSION_CODE
            )
        }
    }

    /**
     * Result of runtime permission request
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Check for granted permission and remove from missing list
        if (requestCode == REQUEST_PERMISSION_CODE) {
            for (i in grantResults.indices.reversed()) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    missingPermission.remove(permissions[i])
                }
            }
        }
        // If there is enough permission, we will start the registration
        if (missingPermission.isEmpty()) {
            registrationCallback.startSDKRegistration(this)
        } else {
            FeedbackUtils.setResult(
                tag = TAG,
                string = getUserPermissionNotGranted(),
                level = LogLevel.ERROR
            )

        }
    }

    private fun getUserPermissionNotGranted(): String? {
        return "Missing permissions! Will not register SDK to connect to aircraft. ${
            V_JsonUtil.toJson(missingPermission)
        }"
    }

    private fun getVersionText() =
        "Developer mode:" + GlobalConfig.DEVELOPER_MODE + ", Simulator mode:" + GlobalConfig.SIMULATOR_MODE + ", " + resources.getString(
            R.string.sdk_version,
            DJISDKManager.getInstance().sdkVersion
        )
}