package pl.edu.wat.droman.ui.callback

import dji.common.error.DJIError
import dji.common.util.CommonCallbacks
import pl.edu.wat.droman.ui.FeedbackUtils
import pl.edu.wat.droman.ui.LogLevel

class CompletionCallbackWithImpl<T>(
    tag: String,
    private val success: (T) -> Unit = {
        FeedbackUtils.setResult(
            string = it.toString(),
            tag = tag,
            level = LogLevel.INFO
        )
    },
    private val failure: (DJIError) -> Unit = {
        FeedbackUtils.setResult(
            tag = tag,
            level = LogLevel.ERROR,
            string = it.toString()
        )
    },
) : CommonCallbacks.CompletionCallbackWith<T> {
    override fun onSuccess(p0: T?) {
        if (p0 != null) {
            success.invoke(p0)
        }
    }

    override fun onFailure(p0: DJIError?) {
        if (p0 != null) {
            failure.invoke(p0)
        }
    }
}
