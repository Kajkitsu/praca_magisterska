package pl.edu.wat.droman.data.model.mission

import com.google.gson.JsonObject

class TakeOffCommand(jsonObject: JsonObject) : Command(type) {
    companion object {
        const val type = "take_off"
    }
}