package pl.edu.wat.droman.data.model

import com.google.gson.GsonBuilder
import dji.common.flightcontroller.FlightControllerState

data class FlightStatus(
    val isFlying: Boolean,
    val longitude: Float,
    val latitude: Float,
    val altitude: Float,
    val velocityX: Float,
    val velocityY: Float,
    val velocityZ: Float,
    val orientationMode: Int,
    val flightModeString: String,
    val flightTimeInSeconds: Int,
    val gpsSignalLevel: Int,
    val satelliteCount: Int,
    val isLandingConfirmationNeeded: Boolean,
    val isFailsafeEnabled: Boolean,
    val batteryThresholdBehavior: Int,
    val isLowerThanBatteryWarningThreshold: Boolean,
    val isLowerThanSeriousBatteryWarningThreshold: Boolean) {

    companion object {
        fun gen(flightState: FlightControllerState): FlightStatus {
            return FlightStatus(
            isFlying = flightState.isFlying,
            longitude = flightState.aircraftLocation.longitude.toFloat(),
            latitude = flightState.aircraftLocation.latitude.toFloat(),
            altitude = flightState.aircraftLocation.altitude,
            velocityX = flightState.velocityX,
            velocityY = flightState.velocityY,
            velocityZ = flightState.velocityZ,
            orientationMode = flightState.orientationMode.value(),
            flightModeString = flightState.flightModeString,
            flightTimeInSeconds = flightState.flightTimeInSeconds,
            gpsSignalLevel = flightState.gpsSignalLevel.value(),
            satelliteCount = flightState.satelliteCount,
            isLandingConfirmationNeeded = flightState.isLandingConfirmationNeeded,
            isFailsafeEnabled = flightState.isFailsafeEnabled,
            batteryThresholdBehavior = flightState.batteryThresholdBehavior.value(),
            isLowerThanBatteryWarningThreshold = flightState.isLowerThanBatteryWarningThreshold,
            isLowerThanSeriousBatteryWarningThreshold = flightState.isLowerThanSeriousBatteryWarningThreshold
            )

        }
    }

    fun toJson():String {
        return GsonBuilder().serializeSpecialFloatingPointValues().create().toJson(this)
    }

}
