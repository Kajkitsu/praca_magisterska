package pl.edu.wat.droman.data.model.mission

import com.google.gson.JsonObject

class SetHomeLocationCommand(jsonObject: JsonObject) : Command(type) {
    val longitude: Double = jsonObject.get("longitude").asDouble
    val latitude: Double = jsonObject.get("latitude").asDouble

    companion object {
        const val type = "set_home_location"
    }
}