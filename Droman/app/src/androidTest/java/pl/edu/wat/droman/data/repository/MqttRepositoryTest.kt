package pl.edu.wat.droman.data.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
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

    private lateinit var mqttRepository: MqttRepository

    @Before
    fun createLogHistory() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        mqttRepository = MqttRepository(
            context = appContext,
            mqttCredentials = MqttCredentials("tcp://192.168.1.101","andorid-test","mark","zaq1@WSX")
        );
        Thread.sleep(3000);
    }

    @Test
    fun connect() {

        assertTrue(mqttRepository.isConnected())
    }


    @Test
    fun publishExampleData() {
//        Thread.sleep(3000);
        mqttRepository.publish(MqttDto("/test","message:"+UUID.randomUUID()))
        assertTrue(true)
    }
}