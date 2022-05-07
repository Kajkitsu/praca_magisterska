package pl.edu.wat.droman.data.service

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.TestCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import pl.edu.wat.droman.TestProperties
import pl.edu.wat.droman.data.datasource.MqttDto
import pl.edu.wat.droman.data.model.MqttCredentials
import pl.edu.wat.droman.data.repository.MqttRepository
import java.util.*

@RunWith(AndroidJUnit4::class)
class MqttServiceTest {

    private lateinit var appContext: Context
    private lateinit var mqttService: MqttService
    private val properties = TestProperties()
    private val password = properties.get("mosquitto.password")
    private val user = properties.get("mosquitto.user")
    private val uri = "tcp://"+properties.get("mosquitto.ip")
    private val clientID = "android-test"

    @Before
    fun init() {
        appContext = InstrumentationRegistry.getInstrumentation().targetContext
        mqttService = MqttService(appContext, MqttCredentials(uri,clientID,user,password))
    }

    @Test
    fun publish() = runBlocking {
        //given

        val message = "message:"+ UUID.randomUUID()
        val topicVal = "/test"

        //then
        val topic = mqttService.getTopic(topicVal)
        val res = topic.publish(message)

        //except
        Assert.assertTrue(res.isSuccess)
    }

    @Test
    fun subscribe() = runBlocking {
        //given

        val message = "message:"+ UUID.randomUUID()
        val topicVal = "/test"

        //then
        val topic = mqttService.getTopic(topicVal)
        val res = topic.subscribe()

        //except
        Assert.assertTrue(res.isSuccess)
        Assert.assertTrue(topic.isSubscribed())
    }

    @Test
    fun unsubscribe() = runBlocking {
    //given

        val message = "message:"+ UUID.randomUUID()
        val topicVal = "/test"

        //then
        val topic = mqttService.getTopic(topicVal)
        topic.subscribe()
        val res = topic.unsubscribe()

        //except
        Assert.assertTrue(res.isSuccess)
        Assert.assertFalse(topic.isSubscribed())
    }


    @Test
    fun getData() = runBlocking {
        //given

        val message = "message:"+ UUID.randomUUID()
        val topicVal = "/test"

        //then
        val topic = mqttService.getTopic(topicVal)
        topic.subscribe()
        delay(3000)
        topic.publish(message)
        delay(3000)
        val res = topic.getData()

        //except
        Assert.assertEquals(message,res.value.toString())
    }


}