package pl.edu.wat.droman.data.service

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dji.common.flightcontroller.FlightControllerState
import dji.sdk.flightcontroller.FlightController
import junit.framework.TestCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import pl.edu.wat.droman.data.model.MqttCredentials
import pl.edu.wat.droman.getOrAwaitValue
import java.util.*


@RunWith(AndroidJUnit4::class)
class UpdateServiceTest {
    private lateinit var mqttService: MqttService
    private val clientID = "android-test"

    @Before
    fun init() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val metadata: Bundle = appContext.packageManager.getApplicationInfo(
            appContext.packageName,
            PackageManager.GET_META_DATA
        ).metaData
        val password = metadata.getString("mosquitto.password")!!
        val user = metadata.getString("mosquitto.user")!!
        val uri = "tcp://" + metadata.getString("mosquitto.ip")
        mqttService = MqttService(appContext, MqttCredentials(uri,clientID,user,password))
    }

    @Test
    fun publish() = runBlocking {
        //given
        val topic = mqttService.getTopic("/test")
        topic.subscribe()
        val resultData = topic.getData()
        val updateService = UpdateService(
            statusTopic = topic,
        )
        val state = FlightControllerState()
        state.velocityZ = 0.5F
        state.velocityX = 2.8F

        //then
        updateService.getStatusCallback().onUpdate(state)
        val data = resultData.getOrAwaitValue(time = 5)

        //except
        Assert.assertNotNull(data)
        Assert.assertNotNull(data.payload)
        Assert.assertEquals(0.5F,data.payload)
    }


}