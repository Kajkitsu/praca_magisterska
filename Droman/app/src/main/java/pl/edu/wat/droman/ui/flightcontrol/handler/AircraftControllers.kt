package pl.edu.wat.droman.ui.flightcontrol.handler

import dji.sdk.flightcontroller.FlightController
import dji.sdk.mission.waypoint.WaypointMissionOperator

class AircraftControllers(
    val cameraHandler: CameraHandler,
    val statsHandler: StatusHandler,
    val flightController: FlightController,
    val waypointMissionOperator: WaypointMissionOperator
) {

}