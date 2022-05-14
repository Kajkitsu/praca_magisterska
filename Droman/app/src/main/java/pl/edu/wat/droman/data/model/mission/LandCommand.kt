package pl.edu.wat.droman.data.model.mission

import com.google.gson.JsonObject

class LandCommand(jsonObject: JsonObject) : Command(type) {
    companion object {
        const val type = "land"
    }
}