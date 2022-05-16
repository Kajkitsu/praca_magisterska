package pl.edu.wat.droman.data.model.command

import pl.edu.wat.droman.ui.flightcontrol.handler.AircraftControllers

abstract class Command(val type: String) {
    abstract fun exec(aircraftControllers: AircraftControllers)
}