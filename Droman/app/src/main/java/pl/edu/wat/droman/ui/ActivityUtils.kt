package pl.edu.wat.droman.ui

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.Toast

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}

enum class LogType{
    WARN, ERROR, DEBUG
}

fun toastAndLog(tag:String, applicationContext: Context, text:String, type: LogType = LogType.DEBUG, length: Int = Toast.LENGTH_LONG) {
    Toast.makeText(
        applicationContext,
        text,
        length
    ).show()
    when (type) {
        LogType.ERROR -> {
            Log.e(tag,text)
        }
        LogType.WARN -> {
            Log.w(tag,text)
        }
        else -> {
            Log.d(tag,text)
        }
    }
}