package pl.edu.wat.droman

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import dji.common.error.DJIError
import dji.common.util.CommonCallbacks
import dji.common.util.CommonCallbacks.CompletionCallback
import pl.edu.wat.droman.ui.FeedbackUtils
import pl.edu.wat.droman.ui.FeedbackUtils.setResult
import pl.edu.wat.droman.ui.LogLevel
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    afterObserve: () -> Unit = {},
    failure: () -> T = { throw TimeoutException("LiveData value was never set.") },
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(o: T?) {
            data = o
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }
    this.observeForever(observer)

    afterObserve.invoke()

    // Don't wait indefinitely if the LiveData is not set.
    if (!latch.await(time, timeUnit)) {
        this.removeObserver(observer)
        return failure.invoke()
    }

    @Suppress("UNCHECKED_CAST")
    return data as T
}


class CompletionCallbackWithHandler<T>(
    tag: String,
    private val success: (T) -> Unit = {
        setResult(
            string = it.toString(),
            tag = tag,
            level = LogLevel.INFO
        )
    },
    private val failure: (DJIError) -> Unit = {
        setResult(
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

class CompletionCallbackHandler<T:DJIError>(
    tag: String,
    private val success: () -> Unit = {
        setResult(
            "Success",
            LogLevel.INFO,
            tag
        )
    },
    private val failure: (DJIError) -> Unit = {
        setResult(
            it.toString(),
            LogLevel.ERROR,
            tag
        )
    },
) : CompletionCallback<T> {
    override fun onResult(djiError: T?) {
        if (djiError != null) {
            failure.invoke(djiError)
        } else {
            success.invoke()
        }
    }
}
