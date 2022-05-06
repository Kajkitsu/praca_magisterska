package pl.edu.wat.droman.data.repository

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import pl.edu.wat.droman.data.datasource.MqttDto
import pl.edu.wat.droman.data.model.MqttCredentials
import java.lang.RuntimeException
import java.util.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class MqttRepositoryTest {

    private lateinit var appContext: Context

    @Before
    fun init() {
        appContext = InstrumentationRegistry.getInstrumentation().targetContext

    }

    @Test
    fun publishExampleData() = runBlocking {
//        Thread.sleep(3000);
        val mqttRepository = MqttRepository(
            context = appContext,
            mqttCredentials = MqttCredentials("tcp://192.168.1.101","andorid-test","mark","zaq1@WSX")
        );
        val message = "message:"+UUID.randomUUID()
        val res = mqttRepository.publish(MqttDto("/test",message))
        assertTrue(res.isSuccess)
        assertEquals(res.getOrThrow().message.toString(),message)
    }

    @Test
    fun publishWithFailureExampleData() = runBlocking {
//        Thread.sleep(3000);
        val mqttRepository = MqttRepository(
            context = appContext,
            mqttCredentials = MqttCredentials("tcp://192.168.1.13","andorid-test","mark","zaq1@WSX")
        );
        val message = "message:"+UUID.randomUUID()
        val res = mqttRepository.publish(MqttDto("/test",message))
        assertTrue(res.isFailure)
    }
}