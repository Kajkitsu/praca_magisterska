package pl.edu.wat.droman.data.model.command

import com.google.gson.Gson
import com.google.gson.JsonObject
import dji.common.error.DJIError
import pl.edu.wat.droman.callback.CompletionCallbackImpl
import pl.edu.wat.droman.ui.FeedbackUtils
import pl.edu.wat.droman.ui.LogLevel

class CommandFactory {
    companion object {
        const val TAG = "CommandFactory"
    }

    private val gson: Gson = Gson()

    fun from(value: String): Command {
        return try {
            val jsonObject = gson.fromJson(value, JsonObject::class.java)
            val missionType = jsonObject.get("type").asString
            createCommand(missionType, jsonObject)
        } catch (e: Exception) {
            FeedbackUtils.setResult(e.toString(), level = LogLevel.ERROR, tag = TAG)
            UnrecognizedCommand(value, getCompletionCallback(UnrecognizedCommand.type))
        }
    }

    private fun getCompletionCallback(type: String): CompletionCallbackImpl<DJIError> {
        return CompletionCallbackImpl(
            Command.TAG,
            success = {
                FeedbackUtils.setResult(
                    "Success executing command $type",
                    level = LogLevel.DEBUG,
                    tag = Command.TAG
                )
            }
        )

    }

    @Throws(Throwable::class)
    private fun createCommand(missionType: String, jsonObject: JsonObject): Command {
        return when (missionType) {
            StartGoHomeCommand.type -> {
                StartGoHomeCommand(getCompletionCallback(StartGoHomeCommand.type))
            }
            LandCommand.type -> {
                LandCommand(getCompletionCallback(LandCommand.type))
            }
            ShootPhotoCommand.type -> {
                ShootPhotoCommand(getCompletionCallback(ShootPhotoCommand.type))
            }
            StartMotorsCommand.type -> {
                StartMotorsCommand(getCompletionCallback(StartMotorsCommand.type))
            }
            StopGoHomeCommand.type -> {
                StopGoHomeCommand(getCompletionCallback(StopGoHomeCommand.type))
            }
            StopMotorsCommand.type -> {
                StopMotorsCommand(getCompletionCallback(StopMotorsCommand.type))
            }
            TakeOffCommand.type -> {
                TakeOffCommand(getCompletionCallback(TakeOffCommand.type))
            }
            LoadWaypointCommand.type -> {
                LoadWaypointCommand(jsonObject, getCompletionCallback(LoadWaypointCommand.type))
            }
            UploadWaypointCommand.type -> {
                UploadWaypointCommand(getCompletionCallback(UploadWaypointCommand.type))
            }
            SetHomeLocationCommand.type -> {
                SetHomeLocationCommand(
                    jsonObject,
                    getCompletionCallback(SetHomeLocationCommand.type)
                )
            }
            StartWaypointCommand.type -> {
                StartWaypointCommand(getCompletionCallback(StartWaypointCommand.type))
            }
            StopWaypointCommand.type -> {
                StopWaypointCommand(getCompletionCallback(StopWaypointCommand.type))
            }
            ResumeWaypointCommand.type -> {
                ResumeWaypointCommand(getCompletionCallback(ResumeWaypointCommand.type))
            }
            PauseWaypointCommand.type -> {
                PauseWaypointCommand(getCompletionCallback(PauseWaypointCommand.type))
            }
            else -> {
                UnrecognizedCommand(
                    jsonObject.toString(),
                    getCompletionCallback(UnrecognizedCommand.type)
                )
            }
        }

    }

}