package pl.edu.wat.droman.data.model.mission

import com.google.gson.JsonObject

class StartMotorsCommand(jsonObject: JsonObject) : Command(type) {
    companion object {
        const val type = "start_motors"
    }
}