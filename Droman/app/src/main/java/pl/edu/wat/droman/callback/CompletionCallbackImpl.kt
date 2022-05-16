package pl.edu.wat.droman.callback

import dji.common.error.DJIError
import dji.common.util.CommonCallbacks
import pl.edu.wat.droman.ui.FeedbackUtils
import pl.edu.wat.droman.ui.LogLevel


class CompletionCallbackImpl<T : DJIError>(
    tag: String,
    private val success: () -> Unit,
    private val failure: (DJIError) -> Unit = {
        FeedbackUtils.setResult(
            "${it.description} ${it.errorCode}",
            tag,
            LogLevel.ERROR
        )
    },
) : CommonCallbacks.CompletionCallback<T> {
    override fun onResult(djiError: T?) {
        if (djiError != null) {
            failure.invoke(djiError)
        } else {
            success.invoke()
        }
    }
}

