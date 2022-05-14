package pl.edu.wat.droman.data.model.mission

import com.google.gson.JsonObject

class UploadWaypointCommand(jsonObject: JsonObject) : Command(type) {
    companion object {
        const val type = "upload_waypoint_mission"
    }

}