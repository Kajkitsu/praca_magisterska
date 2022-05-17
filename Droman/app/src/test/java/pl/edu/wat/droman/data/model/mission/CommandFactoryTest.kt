package pl.edu.wat.droman.data.model.command

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class CommandFactoryTest {
    @Test
    fun testMappingToUploadMissionCommand() {
        //given
        val commandFactory = CommandFactory()
        val missionValue = "{\n" +
                "            \"type\": \"upload_waypoint_mission\",\n" +
                "            \"finished_action\": \"NO_ACTION\",\n" +
                "            \"auto_flight_speed\": 0.01,\n" +
                "            \"max_flight_speed\": 0.5,\n" +
                "            \"heading_mode\": \"AUTO\",\n" +
                "            \"waypoints\": [\n" +
                "                {\n" +
                "                    \"attitude\": 1.0,\n" +
                "                    \"longitude\": 1.0,\n" +
                "                    \"latitude\": 1.0\n" +
                "                },\n" +
                "                {\n" +
                "                    \"attitude\": 1.0,\n" +
                "                    \"longitude\": 1.0,\n" +
                "                    \"latitude\": 1.0\n" +
                "                },\n" +
                "                {\n" +
                "                    \"attitude\": 1.0,\n" +
                "                    \"longitude\": 1.0,\n" +
                "                    \"latitude\": 1.0\n" +
                "                }\n" +
                "            ]\n" +
                "        }"
        //then
        val command = commandFactory.from(missionValue)

        //expect
        assertNotNull(command)
        assertEquals(UploadWaypointCommand.type, command.type)
        assertEquals(UploadWaypointCommand::class.java,command.javaClass)
    }

    @Test
    fun testMappingToFailUploadMissionCommand() {
        //given
        val commandFactory = CommandFactory()
        val missionValue = "{\"type\": \"dsadas\"}"
        //then
        val command = commandFactory.from(missionValue)

        //expect
        assertNotNull(command)
        assertEquals(UnrecognizedCommand::class.java,command.javaClass)
        assertEquals(UnrecognizedCommand.type, command.type)
    }

    @Test
    fun testMappingToTakeOffCommand() {
        //given
        val commandFactory = CommandFactory()
        val missionValue = "{\"type\": \"take_off\"}"
        //then
        val command = commandFactory.from(missionValue)

        //expect
        assertNotNull(command)
        assertEquals(TakeOffCommand::class.java,command.javaClass)
        assertEquals(TakeOffCommand.type, command.type)
    }

    @Test
    fun testMappingToGoHomeCommand() {
        //given
        val commandFactory = CommandFactory()
        val missionValue = "{\"type\": \"go_home\"}"
        //then
        val command = commandFactory.from(missionValue)

        //expect
        assertNotNull(command)
        assertEquals(StartGoHomeCommand::class.java,command.javaClass)
        assertEquals(StartGoHomeCommand.type, command.type)
    }
}