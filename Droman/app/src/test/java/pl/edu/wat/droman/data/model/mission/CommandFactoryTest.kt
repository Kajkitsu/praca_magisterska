package pl.edu.wat.droman.data.model.command

import org.junit.Assert.*
import org.junit.Test

class CommandFactoryTest{
    @Test
    fun testMappingToUploadMission(){
        //given
        val commandFactory = CommandFactory()
        val missionValue = "{\n" +
                "            \"type\": \"load_waypoint_mission\",\n" +
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
        assertEquals(LoadWaypointCommand.type, command.type)
    }
}