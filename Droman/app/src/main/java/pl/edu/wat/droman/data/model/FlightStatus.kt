package pl.edu.wat.droman.data.model

import com.google.gson.Gson
import dji.common.flightcontroller.FlightControllerState

data class FlightStatus(
    val activeBrakeEngaged: Boolean,
    val aircraftHeadDirection: Long,
    val aircraftLocationAltitude: Double?,
    val aircraftLocationLatitude: Double?,
    val aircraftLocationLongitude: Double?,
    val attitudePitch: Double?,
    val attitudeRoll: Double?,
    val attitudeYaw: Double?,
    val batteryThresholdBehavior: String,
    val doesUltrasonicHaveError: Boolean,
    val flightCount: Long,
    val flightLogIndex: Long,
    val flightMode: String,
    val flightModeString: String?,
    val flightTimeInSeconds: Long,
    val flightWindWarning: String,
    val goHomeAssessmentBatteryPercentageNeededToGoHome: Long,
    val goHomeAssessmentBatteryPercentageNeededToLandFromCurrentHeight: Long,
    val goHomeAssessmentMaxRadiusAircraftCanFlyAndGoHome: Double?,
    val goHomeAssessmentRemainingFlightTime: Long,
    val goHomeAssessmentSmartRTHCountdown: Long,
    val goHomeAssessmentSmartRTHState: String,
    val goHomeAssessmentTimeNeededToGoHome: Long,
    val goHomeAssessmentTimeNeededToLandFromCurrentHeight: Long,
    val goHomeExecutionState: String,
    val goHomeHeight: Long,
    val gpsSignalLevel: String,
    val hasReachedMaxFlightHeight: Boolean,
    val hasReachedMaxFlightRadius: Boolean,
    val homeLocationLatitude: Double?,
    val homeLocationLongitude: Double?,
    val isFailsafeEnabled: Boolean,
    val isFlying: Boolean,
    val isGoingHome: Boolean,
    val isHomeLocationSet: Boolean,
    val isIMUPreheating: Boolean,
    val isLowerThanBatteryWarningThreshold: Boolean,
    val isLowerThanSeriousBatteryWarningThreshold: Boolean,
    val isMultipModeOpen: Boolean,
    val isUltrasonicBeingUsed: Boolean,
    val isVisionPositioningSensorBeingUsed: Boolean,
    val islandingConfirmationNeeded: Boolean,
    val motorsOn: Boolean,
    val orientationMode: String,
    val satelliteCount: Long,
    val takeoffLocationAltitude: Double?,
    val ultrasonicHeightInMeters: Double?,
    val velocityX: Double?,
    val velocityY: Double?,
    val velocityZ: Double?
) {

    companion object {
        fun gen(flightState: FlightControllerState): FlightStatus {
            return FlightStatus(
                activeBrakeEngaged = flightState.isActiveBrakeEngaged,
                aircraftHeadDirection = flightState.aircraftHeadDirection.toLong(),
                aircraftLocationAltitude = flightState.aircraftLocation.altitude.toDoubleOrNull(),
                aircraftLocationLatitude = flightState.aircraftLocation.latitude.toPrimitiveDouble(),
                aircraftLocationLongitude = flightState.aircraftLocation.longitude.toPrimitiveDouble(),
                attitudePitch = flightState.attitude.pitch.toPrimitiveDouble(),
                attitudeRoll = flightState.attitude.roll.toPrimitiveDouble(),
                attitudeYaw = flightState.attitude.yaw.toPrimitiveDouble(),
                batteryThresholdBehavior = flightState.batteryThresholdBehavior.name,
                doesUltrasonicHaveError = flightState.doesUltrasonicHaveError(),
                flightCount = flightState.flightCount.toLong(),
                flightLogIndex = flightState.flightLogIndex.toLong(),
                flightMode = flightState.flightMode.name,
                flightModeString = flightState.flightModeString,
                flightTimeInSeconds = flightState.flightTimeInSeconds.toLong(),
                flightWindWarning = flightState.flightWindWarning.name,
                goHomeAssessmentBatteryPercentageNeededToGoHome = flightState.goHomeAssessment.batteryPercentageNeededToGoHome.toLong(),
                goHomeAssessmentBatteryPercentageNeededToLandFromCurrentHeight = flightState.goHomeAssessment.batteryPercentageNeededToLandFromCurrentHeight.toLong(),
                goHomeAssessmentMaxRadiusAircraftCanFlyAndGoHome = flightState.goHomeAssessment.maxRadiusAircraftCanFlyAndGoHome.toDoubleOrNull(),
                goHomeAssessmentRemainingFlightTime = flightState.goHomeAssessment.remainingFlightTime.toLong(),
                goHomeAssessmentSmartRTHCountdown = flightState.goHomeAssessment.smartRTHCountdown.toLong(),
                goHomeAssessmentSmartRTHState = flightState.goHomeAssessment.smartRTHState.name,
                goHomeAssessmentTimeNeededToGoHome = flightState.goHomeAssessment.timeNeededToGoHome.toLong(),
                goHomeAssessmentTimeNeededToLandFromCurrentHeight = flightState.goHomeAssessment.timeNeededToLandFromCurrentHeight.toLong(),
                goHomeExecutionState = flightState.goHomeExecutionState.name,
                goHomeHeight = flightState.goHomeHeight.toLong(),
                gpsSignalLevel = flightState.gpsSignalLevel.name,
                hasReachedMaxFlightHeight = flightState.hasReachedMaxFlightHeight(),
                hasReachedMaxFlightRadius = flightState.hasReachedMaxFlightRadius(),
                homeLocationLatitude = flightState.homeLocation.latitude.toPrimitiveDouble(),
                homeLocationLongitude = flightState.homeLocation.longitude.toPrimitiveDouble(),
                isFailsafeEnabled = flightState.isFailsafeEnabled,
                isFlying = flightState.isFlying,
                isGoingHome = flightState.isGoingHome,
                isHomeLocationSet = flightState.isHomeLocationSet,
                isIMUPreheating = flightState.isIMUPreheating,
                isLowerThanBatteryWarningThreshold = flightState.isLowerThanBatteryWarningThreshold,
                isLowerThanSeriousBatteryWarningThreshold = flightState.isLowerThanSeriousBatteryWarningThreshold,
                isMultipModeOpen = flightState.isMultipleModeOpen,
                isUltrasonicBeingUsed = flightState.isUltrasonicBeingUsed,
                isVisionPositioningSensorBeingUsed = flightState.isVisionPositioningSensorBeingUsed,
                islandingConfirmationNeeded = flightState.isLandingConfirmationNeeded,
                motorsOn = flightState.areMotorsOn(),
                orientationMode = flightState.orientationMode.name,
                satelliteCount = flightState.satelliteCount.toLong(),
                takeoffLocationAltitude = flightState.takeoffLocationAltitude.toDoubleOrNull(),
                ultrasonicHeightInMeters = flightState.ultrasonicHeightInMeters.toDoubleOrNull(),
                velocityX = flightState.velocityX.toDoubleOrNull(),
                velocityY = flightState.velocityY.toDoubleOrNull(),
                velocityZ = flightState.velocityZ.toDoubleOrNull(),
            )

        }
    }

    fun toJson(): String {
        return Gson().toJson(this)
    }

}

private fun Double.toPrimitiveDouble(): Double? {
    if (this.isNaN()) {
        return null
    }
    return this
}

private fun Float.toDoubleOrNull(): Double? {
    return this.toDouble().toPrimitiveDouble()

}
