package pl.edu.wat.droman.ui

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData


object FeedbackUtils {
    private const val MESSAGE_UPDATE = 1
    private const val MESSAGE_TOAST = 2
    var logLiveData = MutableLiveData<String>()

    private val mUIHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MESSAGE_UPDATE -> showMessage(msg.obj as String)
                MESSAGE_TOAST -> showToast(msg.obj as String)
                else -> super.handleMessage(msg)
            }
        }
    }

    private fun showMessage(msg: String) {
        logLiveData.postValue(msg)
    }

    private fun showToast(msg: String) {
        Toast.makeText(DjiApplication.instance, msg, Toast.LENGTH_SHORT).show()
    }

    fun setResult(
        string: String?,
        tag: String?,
        level: LogLevel = LogLevel.INFO
    ) {
        string?.let {
            when (level) {
                LogLevel.ERROR -> {
                    Log.e(tag, string)
                }
                LogLevel.WARN -> {
                    Log.w(tag, string)
                }
                LogLevel.INFO -> {
                    Log.i(tag, string)
                }
                LogLevel.DEBUG -> {
                    Log.d(tag, string)
                }
            }
        }
        if (level.level >= 2) {
            val msg = Message()
            msg.what = MESSAGE_TOAST
            msg.obj = "$tag: $string"
            mUIHandler.sendMessage(msg)
        }
        val msg = Message()
        msg.what = MESSAGE_UPDATE
        msg.obj = "$tag: $string"
        mUIHandler.sendMessage(msg)
    }
}

enum class LogLevel(val level: Int) {
    ERROR(3), WARN(2), DEBUG(1), INFO(0)
}