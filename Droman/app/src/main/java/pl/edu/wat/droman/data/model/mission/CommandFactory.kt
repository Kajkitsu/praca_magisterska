package pl.edu.wat.droman.data.model.mission

import com.google.gson.Gson
import com.google.gson.JsonObject
import pl.edu.wat.droman.ui.FeedbackUtils
import pl.edu.wat.droman.ui.LogLevel

class CommandFactory {

    private val gson: Gson = Gson()

    fun from(value: String): Command? {
        val jsonObject = gson.fromJson(value, JsonObject::class.java)
        val missionType = jsonObject.get("type").asString
        var command: Command? = null
        try {
            command = createCommand(missionType, jsonObject)
        } catch (e: Exception) {
            FeedbackUtils.setResult(e.toString(), level = LogLevel.ERROR, tag = TAG)
        }
        return command
    }

    @Throws(Throwable::class)
    private fun createCommand(missionType: String, jsonObject: JsonObject): Command? {
        return when (missionType) {
            StartGoHomeCommand.type -> {
                StartGoHomeCommand(jsonObject)
            }
            LandCommand.type -> {
                LandCommand(jsonObject)
            }
            ShootPhotoCommand.type -> {
                ShootPhotoCommand(jsonObject)
            }
            StartMotorsCommand.type -> {
                StartMotorsCommand(jsonObject)
            }
            StopGoHomeCommand.type -> {
                StopGoHomeCommand(jsonObject)
            }
            StopMotorsCommand.type -> {
                StopMotorsCommand(jsonObject)
            }
            TakeOffCommand.type -> {
                TakeOffCommand(jsonObject)
            }
            LoadWaypointCommand.type -> {
                LoadWaypointCommand(jsonObject)
            }
            UploadWaypointCommand.type -> {
                UploadWaypointCommand(jsonObject)
            }
            SetHomeLocationCommand.type -> {
                SetHomeLocationCommand(jsonObject)
            }
            StopWaypointCommand.type -> {
                StopWaypointCommand(jsonObject)
            }
            ResumeWaypointCommand.type -> {
                ResumeWaypointCommand(jsonObject)
            }
            PauseWaypointCommand.type -> {
                PauseWaypointCommand(jsonObject)
            }
            else -> {
                null
            }
        }

    }

    companion object {
        const val TAG = "MissionFactory"
    }

}