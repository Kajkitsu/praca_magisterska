package pl.edu.wat.droman.data.model.mission

import com.google.gson.JsonObject

class StopGoHomeCommand(jsonObject: JsonObject) : Command(type) {
    companion object {
        const val type = "stop_go_home"
    }
}