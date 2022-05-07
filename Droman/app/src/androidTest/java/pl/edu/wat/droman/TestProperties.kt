package pl.edu.wat.droman

import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.util.*

class TestProperties {
    val prop = Properties()
    init {
        prop["mosquitto.ip"] = "192.168.47.101"
        prop["mosquitto.user"] = "user"
        prop["mosquitto.password"] = "letmein"

//        var input: InputStream? = null
//
//        try {
//            input = FileInputStream("gradle.properties") //TODO
//
//            // load a properties file
//            prop.load(input)
//            // get the property value and print it out
//            System.out.println(prop.getProperty("user.password"))
//        } catch (ex: IOException) {
//            ex.printStackTrace()
//        } finally {
//            input?.close()
//        }
    }
    fun get(key: String): String {
        return prop[key] as String
    }

}