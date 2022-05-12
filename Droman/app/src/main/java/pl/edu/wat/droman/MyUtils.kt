package pl.edu.wat.droman

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import dji.common.error.DJIError
import dji.common.util.CommonCallbacks
import pl.edu.wat.droman.ui.flightcontrol.FlightControlViewModelFactory
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    afterObserve: () -> Unit = {},
    failure: () -> T = {throw TimeoutException("LiveData value was never set.")},
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

fun <T> getCallback(
    tag: String,
    success: (T) -> Unit = {Log.d(tag, it.toString())},
    failure: (DJIError) -> Unit = { Log.e(tag, it.toString())},
): CommonCallbacks.CompletionCallbackWith<T> =
    object : CommonCallbacks.CompletionCallbackWith<T> {
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

