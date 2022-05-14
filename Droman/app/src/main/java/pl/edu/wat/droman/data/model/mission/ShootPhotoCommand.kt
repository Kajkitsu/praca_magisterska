package pl.edu.wat.droman.data.model.mission

import com.google.gson.JsonObject

class ShootPhotoCommand(jsonObject: JsonObject) : Command(type) {
    companion object {
        const val type = "shoot_photo"
    }

}