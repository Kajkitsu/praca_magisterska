package pl.edu.wat.droman.data.model.command

import com.google.gson.JsonObject
import dji.common.mission.waypoint.WaypointMissionState
import pl.edu.wat.droman.ui.FeedbackUtils
import pl.edu.wat.droman.ui.LogLevel
import pl.edu.wat.droman.ui.flightcontrol.handler.AircraftControllers
import pl.edu.wat.droman.ui.flightcontrol.handler.CommandHandler
import pl.edu.wat.droman.ui.flightcontrol.handler.waypoint.WaypointMissionFactory
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
    val waypoints: List<Waypoint> =
        jsonObject.get("waypoints").asJsonArray.map { Waypoint(it.asJsonObject) }
    private val waypointMissionFactory = WaypointMissionFactory()

    class Waypoint(jsonObject: JsonObject) {
        val attitude: Double = jsonObject.get("attitude").asDouble
        val longitude: Double = jsonObject.get("longitude").asDouble
        val latitude: Double = jsonObject.get("latitude").asDouble
    }

    companion object {
        const val type = "load_waypoint_mission"
    }

    override fun exec(commandHandler: AircraftControllers) {
        if (commandHandler.waypointMissionOperator.currentState == WaypointMissionState.READY_TO_UPLOAD
            || commandHandler.waypointMissionOperator.currentState == WaypointMissionState.READY_TO_EXECUTE
        ) {
            waypointMissionFactory.createWaypointMission(this)?.let {
                val error = commandHandler.waypointMissionOperator.loadMission(it)
                if (error != null) {
                    FeedbackUtils.setResult(
                        error.toString(),
                        level = LogLevel.ERROR,
                        tag = CommandHandler.TAG
                    )
                } else {
                    FeedbackUtils.setResult("Success loading mission", level = LogLevel.DEBUG)
                }
            }
        } else {
            FeedbackUtils.setResult("The mission can be loaded only when the operator state is READY_TO_UPLOAD or READY_TO_EXECUTE")
        }
    }
}