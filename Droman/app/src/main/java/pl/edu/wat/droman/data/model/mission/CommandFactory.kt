package pl.edu.wat.droman.data.model.command

import com.google.gson.Gson
import com.google.gson.JsonObject
import pl.edu.wat.droman.ui.FeedbackUtils
import pl.edu.wat.droman.ui.LogLevel

class CommandFactory {
    companion object {
        const val TAG = "MissionFactory"
    }

    private val gson: Gson = Gson()

    fun from(value: String): Command {
        return try {
            val jsonObject = gson.fromJson(value, JsonObject::class.java)
            val missionType = jsonObject.get("type").asString
            createCommand(missionType, jsonObject)
        } catch (e: Exception) {
            FeedbackUtils.setResult(e.toString(), level = LogLevel.ERROR, tag = TAG)
            UnrecognizedCommand(value)
        }
    }

    @Throws(Throwable::class)
    private fun createCommand(missionType: String, jsonObject: JsonObject): Command {
        return when (missionType) {
            StartGoHomeCommand.type -> {
                StartGoHomeCommand()
            }
            LandCommand.type -> {
                LandCommand()
            }
            ShootPhotoCommand.type -> {
                ShootPhotoCommand()
            }
            StartMotorsCommand.type -> {
                StartMotorsCommand()
            }
            StopGoHomeCommand.type -> {
                StopGoHomeCommand()
            }
            StopMotorsCommand.type -> {
                StopMotorsCommand()
            }
            TakeOffCommand.type -> {
                TakeOffCommand()
            }
            LoadWaypointCommand.type -> {
                LoadWaypointCommand(jsonObject)
            }
            UploadWaypointCommand.type -> {
                UploadWaypointCommand()
            }
            SetHomeLocationCommand.type -> {
                SetHomeLocationCommand(jsonObject)
            }
            StopWaypointCommand.type -> {
                StopWaypointCommand()
            }
            ResumeWaypointCommand.type -> {
                ResumeWaypointCommand()
            }
            PauseWaypointCommand.type -> {
                PauseWaypointCommand()
            }
            else -> {
                UnrecognizedCommand(jsonObject.toString())
            }
        }

    }

}