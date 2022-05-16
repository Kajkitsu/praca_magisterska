package pl.edu.wat.droman.ui.flightcontrol.handler

import android.graphics.Bitmap
import android.os.Environment
import dji.common.camera.SettingsDefinitions
import dji.common.error.DJIError
import dji.sdk.camera.Camera
import dji.sdk.media.MediaFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pl.edu.wat.droman.ui.callback.CompletionCallbackImpl
import pl.edu.wat.droman.GlobalConfig
import pl.edu.wat.droman.data.service.UpdateService
import pl.edu.wat.droman.ui.FeedbackUtils
import pl.edu.wat.droman.ui.LogLevel
import pl.edu.wat.droman.ui.flightcontrol.DownloadListenerImpl
import java.io.File

class CameraHandler(
    private val camera: Camera,
    viewModelScope: CoroutineScope,
    updateService: UpdateService
) {
    companion object {
        const val TAG = "CameraHandler"
    }

    private var supportDownloadMediaMode = false

    init {
        initCheckingIfSupportMediaDownloadMode()
        @Suppress("DEPRECATION")
        camera.setMediaFileCallback {
            if (supportDownloadMediaMode) {
                viewModelScope.launch(Dispatchers.IO) {
                    it.getFullView()
                        ?.let { byte -> updateService.savePicture(byte) }
                }
            } else {
                viewModelScope.launch(Dispatchers.IO) {
                    it.getSusPreview()?.let { it1 -> updateService.savePicture(it1) }
                        ?: run {
                            FeedbackUtils.setResult(
                                "Cannot get preview",
                                level = LogLevel.ERROR,
                                tag = TAG
                            )
                        }
                }
            }
        }
    }

    fun shootPhoto() {
        camera.startShootPhoto(CompletionCallbackImpl<DJIError>(TAG))
    }

    private fun initCheckingIfSupportMediaDownloadMode() {
        camera.setMode(
            SettingsDefinitions.CameraMode.MEDIA_DOWNLOAD, CompletionCallbackImpl<DJIError>(
                success = {
                    FeedbackUtils.setResult(
                        "Media download mode supported",
                        LogLevel.DEBUG
                    )
                    supportDownloadMediaMode = true
                    setBackToShootPhotoMode()
                },
                failure = {
                    FeedbackUtils.setResult(
                        "Media download mode unsupported",
                        LogLevel.ERROR
                    )
                    setBackToShootPhotoMode()
                    supportDownloadMediaMode = false
                },
                tag = TAG

            )
        )
    }

    private fun setBackToShootPhotoMode() {
        camera.setMode(
            SettingsDefinitions.CameraMode.SHOOT_PHOTO,
            CompletionCallbackImpl<DJIError>(TAG, success = {
                FeedbackUtils.setResult(
                    "Set back to shoot photo mode",
                    LogLevel.DEBUG
                )
            })
        )
    }

    private suspend fun MediaFile.getSusPreview(): Bitmap? {
        if (this.preview != null) {
            return this.preview
        }
        this.fetchPreview(CompletionCallbackImpl<DJIError>(TAG))
        var inc = 0
        while (this.preview == null && inc < 200) {
            delay(100)
            inc++
        }
        return this.preview
    }

    private suspend fun MediaFile.getFullView(): Bitmap? {
        val destDir =
            File("${Environment.getExternalStorageDirectory().path}/${GlobalConfig.FOLDER_FOR_HQ_MEDIA}/")
        val downloadHandler = DownloadListenerImpl<String>(camera)
        this.fetchFileData(destDir, this.fileName, DownloadListenerImpl<String>(camera))
        return downloadHandler.getBitmap()
    }

    fun destroy() {
        @Suppress("DEPRECATION")
        camera.setMediaFileCallback(null)
    }
}