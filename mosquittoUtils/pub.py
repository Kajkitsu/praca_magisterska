import sys

import paho.mqtt.client as mqtt
import time
import json

MQTT_SERVER = "172.16.4.73"
MQTT_PATH = "droman/command/"
CLIENT_ID = "5FSCJB7001165D"
START_POINT_LATITUDE = 20
START_POINT_LONGITUDE = 113
ONE_METER_OFFSET = 0.00000899322


# The callback for when the client receives a CONNACK response from the server.
def on_connect(client, userdata, flags, rc):
    print("Connected with result code " + str(rc))

    # Subscribing in on_connect() means that if we lose the connection and
    # reconnect then subscriptions will be renewed.
    client.subscribe(MQTT_PATH)
    # The callback for when a PUBLISH message is received from the server.


if __name__ == '__main__':
    client = mqtt.Client()
    client.username_pw_set(username="admin", password="letmein")
    client.on_connect = on_connect
    client.connect(MQTT_SERVER, 1883, 60)

    print(sys.argv)
    allowed_args = ["test", "set_home_location", "land", "upload_waypoint_mission", "take_off", "start_motors",
                   "stop_motors", "shoot_photo", "load_waypoint_mission", "take_off_and_land", "stop_waypoint_mission",
                   "start_waypoint_mission", "go_home"]
    print(allowed_args)

    if sys.argv[1] == "test":
        client.publish(topic=MQTT_PATH + CLIENT_ID, payload='{"type":"shoot_photo"}')
        time.sleep(1)
        client.publish(topic=MQTT_PATH + CLIENT_ID, payload='{"type":"start_motors"}')
        time.sleep(3)
        client.publish(topic=MQTT_PATH + CLIENT_ID, payload='{"type":"stop_motors"}')
    elif sys.argv[1] == "set_home_location":
        payload = {
            "type": "set_home_location",
            "longitude": 20.0,
            "latitude": 113.0,
        }
        client.publish(topic=MQTT_PATH + CLIENT_ID, payload=json.dumps(payload))

    elif sys.argv[1] == "load_waypoint_mission":
        payload = {
            "type": "load_waypoint_mission",
            "finished_action": "NO_ACTION",
            "auto_flight_speed": 1.0,
            "max_flight_speed": 5.0,
            "heading_mode": "AUTO",
            "waypoints": [
                {
                    "attitude": 10,
                    "longitude": START_POINT_LONGITUDE,
                    "latitude": START_POINT_LATITUDE + ONE_METER_OFFSET * 0.5
                },
                {
                    "attitude": 11,
                    "longitude": START_POINT_LONGITUDE + ONE_METER_OFFSET * 1,
                    "latitude": START_POINT_LATITUDE - ONE_METER_OFFSET * 1
                },
                {
                    "attitude": 12,
                    "longitude": START_POINT_LONGITUDE + ONE_METER_OFFSET * 2,
                    "latitude": START_POINT_LATITUDE + ONE_METER_OFFSET * 1
                }
            ]
        }
        print(json.dumps(payload))
        client.publish(topic=MQTT_PATH + CLIENT_ID, payload=json.dumps(payload))

    elif sys.argv[1] == "take_off_and_land":
        client.publish(topic=MQTT_PATH + CLIENT_ID, payload='{"type":"take_off"}')
        time.sleep(6)
        client.publish(topic=MQTT_PATH + CLIENT_ID, payload='{"type":"land"}')

    elif sys.argv[1] in allowed_args:
        client.publish(topic=MQTT_PATH + CLIENT_ID, payload='{"type":"'+sys.argv[1]+'"}')
    else:
        print("forbidden command "+sys.argv[1])


    # Blocking call that processes network traffic, dispatches callbacks and
    # handles reconnecting.
    # Other loop*() functions are available that give a threaded interface and a
    # manual interface.
