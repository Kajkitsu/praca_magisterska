package pl.edu.wat.droman.ui.flightcontrol

import android.graphics.Bitmap
import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dji.common.camera.SettingsDefinitions
import dji.sdk.base.BaseProduct
import dji.sdk.camera.Camera
import dji.sdk.media.MediaFile
import dji.sdk.products.Aircraft
import dji.sdk.sdkmanager.DJISDKManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pl.edu.wat.droman.CallbackToastHandler
import pl.edu.wat.droman.GlobalConfig
import pl.edu.wat.droman.data.service.UpdateService
import pl.edu.wat.droman.ui.DjiApplication
import java.io.File


class FlightControlViewModel(
    private var updateService: UpdateService,
) : ViewModel() {

    private val djiManager: DJISDKManager = DJISDKManager.getInstance()
    private var lastStateUpdate = 0
    private var mediaManager = DjiApplication.aircraftInstance?.camera?.mediaManager


    companion object {
        val TAG = "FlightControlViewModel"
    }

    init {
        getAircraft()?.let { aircraft ->
            aircraft.flightController
                ?.setStateCallback { state ->
                    viewModelScope.launch(Dispatchers.IO) {
                        if (lastStateUpdate == 0) {
                            updateService.saveCallback(state)
                        }
                        lastStateUpdate++
                        lastStateUpdate %= GlobalConfig.stateRateLimit
                    }
                }
            aircraft.camera.setMode(
                SettingsDefinitions.CameraMode.SHOOT_PHOTO,
                CallbackToastHandler()
            )


//            it.camera.setPhotoTimeIntervalSettings(
//                SettingsDefinitions.PhotoTimeIntervalSettings(
//                    Int.MAX_VALUE,
//                    5
//                )
//            ) {
//                it?.let { error -> Log.e(TAG, error.toString()) }
//            }
//            it.camera.startShootPhoto {
//                it?.let { error -> Log.e(TAG, error.toString()) }
//            }


            aircraft.camera.setMediaFileCallback {
                // it.getFullview() TODO
                viewModelScope.launch(Dispatchers.IO) {
                    updateService.savePicture(it.getSusPreview())
                }
//                viewModelScope.launch(Dispatchers.IO) { unsupported in Dji Mini 2 firmware limitations
//                    it.getFullview(camera = aircraft.camera)
//                        ?.let { byte -> updateService.savePicture(byte) }
//                }
            }
        }
    }


    private suspend fun MediaFile.getSusPreview(): Bitmap {
        if (this.preview != null) {
            return this.preview
        }
        var inc = 0
        while (this.preview == null && inc < 10000) {
            delay(100)
            inc++
        }
        return this.preview!!
    }

    private suspend fun MediaFile.getFullview(camera: Camera): Bitmap? {
        val destDir = File(Environment.getExternalStorageDirectory().path + "/Dji_Sdk_Test/")
        val downloadHandler = DownloadHandler<String>(camera)
        this.fetchFileData(destDir, this.fileName, DownloadHandler<String>(camera))
        return downloadHandler.getBitmap()
    }

//    private suspend fun MediaFile.getSusFullImageBytaArray(): ByteArray {
//        this.fetchFileByteData()
//
//    }

//    private fun getState(serialNumber: MutableLiveData<String>): CommonCallbacks.CompletionCallback<*>? =
//        object : CommonCallbacks.CompletionCallbackWith<String> {
//            override fun onSuccess(p0: String?) {
//                serialNumber.postValue(p0)
//            }
//
//            override fun onFailure(p0: DJIError?) {
//                Log.e(FlightControlViewModelFactory.TAG, p0.toString())
//            }
//        }

    fun destroy() {
        getAircraft()?.let { aircraft ->
            aircraft.flightController
                ?.setStateCallback(null)
            aircraft.camera
                ?.setMediaFileCallback(null)
        }
    }


    fun getAircraft(): Aircraft? {
        val product: BaseProduct? = djiManager.product
        if (product is Aircraft) {
            return product
        }
        return null
//        throw RuntimeException("djiManager.product == null")
    }

}

