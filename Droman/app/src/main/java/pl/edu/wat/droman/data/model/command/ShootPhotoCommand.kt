package pl.edu.wat.droman.data.model.command

import pl.edu.wat.droman.ui.flightcontrol.handler.AircraftControllers

class ShootPhotoCommand : Command(type) {
    companion object {
        const val type = "shoot_photo"
    }

    override fun exec(commandHandler: AircraftControllers) {
        commandHandler.cameraHandler.shootPhoto()
    }

}