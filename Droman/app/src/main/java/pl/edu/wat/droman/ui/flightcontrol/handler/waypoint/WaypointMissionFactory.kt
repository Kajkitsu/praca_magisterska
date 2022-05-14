package pl.edu.wat.droman.ui.flightcontrol.handler.waypoint

import dji.common.mission.waypoint.*
import pl.edu.wat.droman.data.model.mission.LoadWaypointCommand
import pl.edu.wat.droman.ui.FeedbackUtils
import pl.edu.wat.droman.ui.LogLevel


class WaypointMissionFactory {
    companion object {
        const val TAG = "WaypointMissionFactory"
    }

    fun createWaypointMission(missionDto: LoadWaypointCommand): WaypointMission? {

        val mission = WaypointMission.Builder()
            .finishedAction(getFinishAction(missionDto))
            .setMissionID(missionDto.id)
            .maxFlightSpeed(missionDto.maxFlightSpeed)
            .autoFlightSpeed(missionDto.autoFlightSpeed)
            .gotoFirstWaypointMode(WaypointMissionGotoWaypointMode.SAFELY)
            .setExitMissionOnRCSignalLostEnabled(true)
            .repeatTimes(1)
            .headingMode(getHeading(missionDto))


        missionDto.waypoints
            .map {
                return@map createWaypoint(it) }.
            forEach { way ->
                mission.addWaypoint(way)
            }
        return mission.build()
    }

    private fun getFinishAction(missionDto: LoadWaypointCommand): WaypointMissionFinishedAction {
        when {
            missionDto.finishedAction == "NO_ACTION" -> {
                return WaypointMissionFinishedAction.NO_ACTION
            }
            missionDto.finishedAction == "GO_HOME" -> {
                return WaypointMissionFinishedAction.GO_HOME
            }
            missionDto.finishedAction == "AUTO_LAND" -> {
                return WaypointMissionFinishedAction.AUTO_LAND
            }
            missionDto.finishedAction == "CONTINUE_UNTIL_END" -> {
                return WaypointMissionFinishedAction.CONTINUE_UNTIL_END
            }
            missionDto.finishedAction == "GO_FIRST_WAYPOINT" -> {
                return WaypointMissionFinishedAction.GO_FIRST_WAYPOINT
            }
            else -> {
                FeedbackUtils.setResult(
                    "Wrong finish action value in missionDto, setting to NO_ACTION",
                    level = LogLevel.ERROR,
                    tag = TAG
                )
                return WaypointMissionFinishedAction.NO_ACTION
            }
        }
    }

    private fun getHeading(missionDto: LoadWaypointCommand): WaypointMissionHeadingMode {
        return if (missionDto.headingMode == "WAYPOINT_CUSTOM" && missionDto.heading != null) {
            WaypointMissionHeadingMode.USING_WAYPOINT_HEADING
        } else if (missionDto.headingMode == "TOWARD_POINT_OF_INTEREST" && missionDto.pointOfInterestLatitude != null && missionDto.pointOfInterestLongitude != null) {
            WaypointMissionHeadingMode.TOWARD_POINT_OF_INTEREST
        } else {
            if (missionDto.headingMode != "AUTO") {
                FeedbackUtils.setResult(
                    "Wrong heading mode action value in missionDto, setting to AUTO",
                    level = LogLevel.ERROR,
                    tag = TAG
                )
            }
            WaypointMissionHeadingMode.AUTO
        }

    }

    private fun createWaypoint(it: LoadWaypointCommand.Waypoint): Waypoint {
        return Waypoint(it.latitude, it.longitude, it.attitude.toFloat())

    }
}