package pl.edu.wat.droman.ui.flightcontrol

import android.graphics.Bitmap
import dji.common.camera.SettingsDefinitions
import dji.common.error.DJIError
import dji.sdk.camera.Camera
import dji.sdk.media.DownloadListener
import kotlinx.coroutines.delay
import pl.edu.wat.droman.CallbackToastHandler
import pl.edu.wat.droman.CompletionCallbackWithToastHandler
import pl.edu.wat.droman.ui.ToastUtils


class DownloadHandler<B>(private val camera: Camera) : DownloadListener<B> {
    companion object {
        val TAG = "DownloadHandler"
    }

    private var prevMode: SettingsDefinitions.CameraMode? = null

    private var bitmap: Bitmap? = null
    private var path: String? = null

    override fun onStart() {
        camera.getMode(
            CompletionCallbackWithToastHandler<SettingsDefinitions.CameraMode>(
                success = { prevMode = it }, tag = TAG
            )
        )
        camera.setMode(SettingsDefinitions.CameraMode.MEDIA_DOWNLOAD, CallbackToastHandler())
    }

    override fun onRateUpdate(total: Long, current: Long, arg2: Long) {}
    override fun onProgress(total: Long, current: Long) {}

    override fun onSuccess(obj: B) {
        if (obj is Bitmap) {
            bitmap = obj
            ToastUtils.setResultToToast("Success! The bitmap's byte count is: " + bitmap!!.byteCount)
        } else if (obj is String) {
            path = obj
            ToastUtils.setResultToToast("The file has been stored, its path is $obj")
        }
        camera.setMode(
            prevMode ?: SettingsDefinitions.CameraMode.SHOOT_PHOTO,
            CallbackToastHandler()
        )
    }

    override fun onFailure(djiError: DJIError?) {
        camera.setMode(
            prevMode ?: SettingsDefinitions.CameraMode.SHOOT_PHOTO,
            CallbackToastHandler()
        )
    }

    override fun onRealtimeDataUpdate(bytes: ByteArray?, l: Long, b: Boolean) {}

    @Synchronized
    suspend fun getBitmap(): Bitmap? { //Method not tested, due to Dji Mini 2 firmware limitations
        var inc = 0
        while (this.bitmap == null && this.path == null && inc < 100) {
            delay(100)
            inc++
        }
        return this.bitmap
    }
}