package pl.edu.wat.droman.ui.flightcontrol

import android.graphics.Bitmap
import android.util.Log
import dji.common.camera.SettingsDefinitions
import dji.common.error.DJIError
import dji.common.util.CommonCallbacks
import dji.sdk.camera.Camera
import dji.sdk.media.DownloadListener
import pl.edu.wat.droman.ui.ToastUtils


class DownloadHandler<B>(private val camera: Camera) : DownloadListener<B> {
    companion object {
        val TAG = "DownloadHandler"
    }

    private var prevMode: SettingsDefinitions.CameraMode? = null;



    override fun onStart() {
        camera.getMode(
            object : CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.CameraMode> {
            override fun onFailure(p0: DJIError?) {
                Log.e(FlightControlViewModelFactory.TAG, p0.toString())
            }

            override fun onSuccess(p0: SettingsDefinitions.CameraMode?) {
                prevMode = p0
            }
        })
//        camera.setMode(SettingsDefinitions.CameraMode.MEDIA_DOWNLOAD)TODO
    }

    override fun onRateUpdate(total: Long, current: Long, arg2: Long) {}
    override fun onProgress(total: Long, current: Long) {}
    override fun onSuccess(obj: B) {
        if (obj is Bitmap) {
            val bitmap = obj as Bitmap
            ToastUtils.setResultToToast("Success! The bitmap's byte count is: " + bitmap.byteCount)
        } else if (obj is String) {
            ToastUtils.setResultToToast("The file has been stored, its path is $obj")
        }
//        prevMode?.let { camera.setMode(it,) } TODO
    }

    override fun onFailure(djiError: DJIError?) {

    }

    override fun onRealtimeDataUpdate(bytes: ByteArray?, l: Long, b: Boolean) {}
}