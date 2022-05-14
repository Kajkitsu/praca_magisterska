package pl.edu.wat.droman.data.model.mission

import com.google.gson.JsonObject
import java.util.*

class LoadWaypointCommand(jsonObject: JsonObject) : Command(type) {
    val id: Int = Random().nextInt()
    val finishedAction: String = jsonObject.get("finished_action").asString
    val autoFlightSpeed: Float = jsonObject.get("auto_flight_speed").asFloat
    val maxFlightSpeed: Float = jsonObject.get("max_flight_speed").asFloat
    val heading: Float? = jsonObject.get("heading")?.asFloat
    val pointOfInterestLongitude: Double? = jsonObject.get("point_of_interest_longitude")?.asDouble
    val pointOfInterestLatitude: Double? = jsonObject.get("point_of_interest_latitude")?.asDouble
    val headingMode: String = jsonObject.get("heading_mode").asString
    val waypoints: List<Waypoint> = jsonObject.get("waypoints").asJsonArray.map { Waypoint(it.asJsonObject) }

    class Waypoint(jsonObject: JsonObject) {
        val attitude: Double = jsonObject.get("attitude").asDouble
        val longitude: Double = jsonObject.get("longitude").asDouble
        val latitude: Double = jsonObject.get("latitude").asDouble
    }

    companion object {
        const val type = "load_waypoint_mission"
    }
}