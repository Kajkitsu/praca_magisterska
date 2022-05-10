package pl.edu.wat.droman.data.model

import com.google.gson.Gson
import dji.common.mission.waypointv2.WaypointV2

class Mission {
    fun getWaypoints(): MutableList<WaypointV2> {
        TODO("Not yet implemented")
    }

    companion object {
        fun from(value: String): Mission {
            return Gson().fromJson(value, Mission::class.java)
        }
    }

}
