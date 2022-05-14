package pl.edu.wat.droman.data.model.mission

import com.google.gson.JsonObject

class PauseWaypointCommand(jsonObject: JsonObject) : Command(type) {
    companion object {
        const val type = "pause_waypoint_mission"
    }
}