package pl.edu.wat.droman.data.model.mission

import com.google.gson.JsonObject

class StartWaypointCommand(jsonObject: JsonObject) : Command(type) {
    companion object {
        const val type = "start_waypoint_mission"
    }

}