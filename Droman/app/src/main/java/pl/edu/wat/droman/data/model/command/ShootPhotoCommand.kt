package pl.edu.wat.droman.data.model.command

import dji.common.error.DJIError
import dji.common.util.CommonCallbacks
import pl.edu.wat.droman.ui.flightcontrol.handler.AircraftControllers

class ShootPhotoCommand(completionCallback : CommonCallbacks.CompletionCallback<DJIError>) : Command(type, completionCallback) {
    companion object {
        const val type = "shoot_photo"
    }

    override fun exec(aircraftControllers: AircraftControllers) {
        aircraftControllers.cameraHandler.shootPhoto()
    }

}