package pl.edu.wat.droman.ui.flightcontrol

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dji.common.camera.SettingsDefinitions
import dji.common.error.DJIError
import dji.sdk.base.BaseProduct
import dji.sdk.camera.Camera
import dji.sdk.media.DownloadListener
import dji.sdk.media.MediaFile
import dji.sdk.products.Aircraft
import dji.sdk.sdkmanager.DJISDKManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pl.edu.wat.droman.data.service.UpdateService


class FlightControlViewModel(
    private var updateService: UpdateService,
) : ViewModel() {

    private var aircraft: Aircraft?
    private val djiManager: DJISDKManager = DJISDKManager.getInstance()


    companion object {
        val TAG = "FlightControlViewModel"
    }

    init {
        aircraft = getAircraft()

        aircraft?.let { it ->
            it.flightController
                ?.setStateCallback { state ->
                    viewModelScope.launch(Dispatchers.IO) {
                        updateService.saveCallback(state)
                    }
                }
            it.camera.setShootPhotoMode(SettingsDefinitions.ShootPhotoMode.INTERVAL) {
                it?.let { error -> Log.e(TAG, error.toString()) }

            }
            it.camera.setMode(SettingsDefinitions.CameraMode.SHOOT_PHOTO) {
                it?.let { error -> Log.e(TAG, error.toString()) }
            }
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


            it.camera.setMediaFileCallback {
                // it.getFullview() TODO
                viewModelScope.launch(Dispatchers.IO) {
                    it.getSusPreview()?.let { bitmap -> updateService.savePicture(bitmap) }
                }
                viewModelScope.launch(Dispatchers.IO) {
//                    it.getFullview(it.camera)?.let { byte -> updateService.savePicture(byte) }
                }
            }
        }
    }


    private suspend fun MediaFile.getSusPreview(): Bitmap? {
        if (this.preview != null) {
            return this.preview
        }
        this.fetchPreview {
            it?.let { er -> Log.e(TAG, er.toString()) }
        }
        while (this.preview == null) {
            delay(100)
        }
        return this.preview
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


    fun getAircraft(): Aircraft? {
        val product: BaseProduct? = djiManager.product
        if (product is Aircraft) {
            return product
        }
        return null
//        throw RuntimeException("djiManager.product == null")
    }

}
