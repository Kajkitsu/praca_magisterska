package pl.edu.wat.droman.data.service

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import pl.edu.wat.droman.data.model.MqttCredentials
import java.util.*

@RunWith(AndroidJUnit4::class)
class MqttServiceTest {

    private lateinit var appContext: Context
    private lateinit var mqttService: MqttService
    private lateinit var password: String
    private lateinit var user: String
    private lateinit var uri: String
    private val clientID = "android-test"

    @Before
    fun init() {
        appContext = InstrumentationRegistry.getInstrumentation().targetContext
        mqttService = MqttService(appContext, MqttCredentials(uri,clientID,user,password))

        val metadata: Bundle = appContext.packageManager.getApplicationInfo(
            appContext.packageName,
            PackageManager.GET_META_DATA
        ).metaData

        password = metadata.getString("mosquitto.password")!!
        user = metadata.getString("mosquitto.user")!!
        uri = "tcp://" + metadata.getString("mosquitto.ip")
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