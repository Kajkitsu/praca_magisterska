package pl.edu.wat.droman.data.model.mission

import com.google.gson.JsonObject

class StartGoHomeCommand(jsonObject: JsonObject) : Command(type) {
    companion object {
        const val type = "go_home"
    }

}