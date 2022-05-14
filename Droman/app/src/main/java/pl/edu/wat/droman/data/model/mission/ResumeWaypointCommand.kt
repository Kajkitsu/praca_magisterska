package pl.edu.wat.droman.data.model.mission

import com.google.gson.JsonObject

class ResumeWaypointCommand(jsonObject: JsonObject) : Command(type) {
    companion object {
        const val type = "resume_waypoint_mission"
    }
}