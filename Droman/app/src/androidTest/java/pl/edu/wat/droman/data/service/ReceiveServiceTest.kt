package pl.edu.wat.droman.data.service

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import pl.edu.wat.droman.data.ETopic
import pl.edu.wat.droman.data.model.MqttCredentials
import pl.edu.wat.droman.data.model.command.ShootPhotoCommand
import pl.edu.wat.droman.getOrAwaitValue

@RunWith(AndroidJUnit4::class)
class ReceiveServiceTest {
    private lateinit var appContext: Context
    private lateinit var receiveService: ReceiveService
    private lateinit var commandTopic: MqttService.Topic
    private lateinit var password: String
    private lateinit var user: String
    private lateinit var uri: String
    private val clientID = "android-test"

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun init() {
        appContext = InstrumentationRegistry.getInstrumentation().targetContext

        val metadata: Bundle = appContext.packageManager.getApplicationInfo(
            appContext.packageName,
            PackageManager.GET_META_DATA
        ).metaData

        password = metadata.getString("mosquitto.password")!!
        user = metadata.getString("mosquitto.user")!!
        uri = "tcp://" + metadata.getString("mosquitto.ip")
        val mqttService = MqttService(appContext, MqttCredentials(uri, clientID, user, password))
        commandTopic = mqttService.getTopic(ETopic.COMMAND.forClient(clientID))
        receiveService = ReceiveService(commandTopic)
    }

    @Test
    fun getMission() = runBlocking {
        //given
        val message = "{\"type\":\"" + ShootPhotoCommand.type + "\"}"

        //then
        val res = receiveService.getCommand()
        delay(3000)
        commandTopic.publish(message)

        //except
        assertEquals(ShootPhotoCommand.type, res.getOrAwaitValue(time = 500).type)
    }

}