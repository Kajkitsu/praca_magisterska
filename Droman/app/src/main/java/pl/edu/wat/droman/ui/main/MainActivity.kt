package pl.edu.wat.droman.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dji.frame.util.V_JsonUtil
import dji.log.GlobalConfig
import dji.sdk.sdkmanager.DJISDKManager
import kotlinx.coroutines.launch
import pl.edu.wat.droman.R
import pl.edu.wat.droman.databinding.ActivityMainBinding
import pl.edu.wat.droman.ui.LogType
import pl.edu.wat.droman.ui.afterTextChanged
import pl.edu.wat.droman.ui.flightcontrol.FlightControlActivity
import pl.edu.wat.droman.ui.toastAndLog

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

    private lateinit var registrationCallback: RegistrationCallback;
    private lateinit var binding: ActivityMainBinding
    private val missingPermission: MutableList<String> = ArrayList()

    private lateinit var mainViewModel: MainViewModel

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isStarted = true
        mainViewModel = ViewModelProvider(
            this,
            MainViewModelFactory()
        )[MainViewModel::class.java]

        binding.btStartFlightMode
            .setOnClickListener {
                if(mainViewModel.connectState.value == true){
                    val nextActivity = Intent(this, FlightControlActivity::class.java)
                    nextActivity.putExtra("username",binding.edMqttUsername.text.toString())
                    nextActivity.putExtra("password",binding.edMqttPassword.text.toString())
                    nextActivity.putExtra("ipAddress",binding.edMqttAddress.text.toString())
                    startActivity(nextActivity)
                }
            }

        binding.btCheckConnection
            .setOnClickListener {
                binding.btCheckConnection.isEnabled = false
                binding.progressBar.visibility = View.VISIBLE
                mainViewModel.validateConnection(
                    binding.edMqttUsername.text.toString(),
                    binding.edMqttPassword.text.toString(),
                    binding.edMqttAddress.text.toString(),
                    applicationContext
                )
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

        binding.version.text = getVersionText()

        mainViewModel.connectState.observe(this@MainActivity, Observer {
            binding.btStartFlightMode.isEnabled = it
            binding.btCheckConnection.isEnabled = true
            if(binding.progressBar.visibility == View.VISIBLE){
                binding.progressBar.visibility = View.INVISIBLE
                if(it){
                    toastAndLog(MainViewModel.TAG,applicationContext,"Correct connection")
                }
                else{
                    toastAndLog(MainViewModel.TAG,applicationContext,"Failed connecting")
                }
            }
        })

        registrationCallback = RegistrationCallback(applicationContext)
        checkAndRequestPermissions()
    }


    override fun onDestroy() {
        DJISDKManager.getInstance().destroy()
        isStarted = false
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
            toastAndLog(
                TAG,
                applicationContext,
                "Missing permissions! Will not register SDK to connect to aircraft." + V_JsonUtil.toJson(
                    missingPermission
                ),
                LogType.ERROR
            )

        }
    }

    private fun getVersionText() = "Debug:" + GlobalConfig.DEBUG + ", " + resources.getString(
        R.string.sdk_version,
        DJISDKManager.getInstance().sdkVersion
    )
}