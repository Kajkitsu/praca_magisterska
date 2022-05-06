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
        //given
        val mqttRepository = MqttRepository(
            context = appContext,
            mqttCredentials = MqttCredentials("tcp://192.168.1.101","android-test","mark","zaq1@WSX")
        );
        val message = "message:"+UUID.randomUUID()

        //then
        val res = mqttRepository.publish(MqttDto("/test",message))

        //except
        assertTrue(res.isSuccess)
        assertEquals(message,res.getOrThrow().message.toString())
    }

    @Test
    fun publishWithFailureExampleData() = runBlocking {
        //given
        val mqttRepository = MqttRepository(
            context = appContext,
            mqttCredentials = MqttCredentials("tcp://192.168.1.13","android-test","mark","zaq1@WSX")
        );
        val message = "message:"+UUID.randomUUID()

        //then
        val res = mqttRepository.publish(MqttDto("/test",message))

        //except
        assertTrue(res.isFailure)
    }

    @Test
    fun publishAndSubscribe() = runBlocking {
        //given
        val mqttRepository = MqttRepository(
            context = appContext,
            mqttCredentials = MqttCredentials("tcp://192.168.1.101","android-test","mark","zaq1@WSX")
        );
        val message = "message:"+UUID.randomUUID()

        //then
        val subscribeRe = mqttRepository.subscribe("/test")
        Thread.sleep(2000)
        val publishRes = mqttRepository.publish(MqttDto("/test",message))
        Thread.sleep(2000)
        val receivedMsg = mqttRepository.getData("/test")

        //except
        assertTrue(subscribeRe.isSuccess)
        assertTrue(publishRes.isSuccess)
        assertEquals(message, receivedMsg.value?.toString())
    }
}