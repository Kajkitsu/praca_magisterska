package pl.edu.wat.droman.ui.mqtttest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.CalendarContract.CalendarCache.URI
import androidx.lifecycle.ViewModelProvider
import pl.edu.wat.droman.R
import pl.edu.wat.droman.databinding.ActivityLoginBinding
import pl.edu.wat.droman.databinding.ActivityMqttTestBinding
import pl.edu.wat.droman.ui.login.LoginViewModel
import pl.edu.wat.droman.ui.login.LoginViewModelFactory

class MqttTestActivity : AppCompatActivity() {

    enum class IntentValues {
        USERNAME, PASSWORD, ADDRESS
    }

    private lateinit var mqttTestViewModel: MqttTestViewModel
    private lateinit var binding: ActivityMqttTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMqttTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mqttUsername = savedInstanceState!!.getString(IntentValues.USERNAME.name)!!
        val mqttPassword = savedInstanceState.getString(IntentValues.PASSWORD.name)!!
        val mqttAddress = savedInstanceState.getString(IntentValues.ADDRESS.name)!!


        mqttTestViewModel = ViewModelProvider(this, MqttTestViewModelFactory(applicationContext,mqttAddress,mqttUsername,mqttPassword))
            .get(MqttTestViewModel::class.java)


//        val password = binding.password
//        val login = binding.login
//        val loading = binding.loading
    }
}