import sys

import paho.mqtt.client as mqtt
import time
import json

MQTT_SERVER = "10.8.47.193"
MQTT_PATH = "droman/command/"
CLIENT_ID = "5FSCJB7001165D"


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
    if sys.argv[1] == "test":
        client.publish(topic=MQTT_PATH + CLIENT_ID, payload='{"type":"shoot_photo"}')
        time.sleep(1)
        client.publish(topic=MQTT_PATH + CLIENT_ID, payload='{"type":"start_motors"}')
        time.sleep(3)
        client.publish(topic=MQTT_PATH + CLIENT_ID, payload='{"type":"stop_motors"}')

    elif sys.argv[1] == "take_off":
        client.publish(topic=MQTT_PATH + CLIENT_ID, payload='{"type":"take_off"}')
        time.sleep(5)

    elif sys.argv[1] == "take_off_and_land":
        client.publish(topic=MQTT_PATH + CLIENT_ID, payload='{"type":"take_off"}')
        time.sleep(10)
        client.publish(topic=MQTT_PATH + CLIENT_ID, payload='{"type":"land"}')

    elif sys.argv[1] == "set_home":
        client.publish(topic=MQTT_PATH + CLIENT_ID, payload='{"type":"set_home"}')

    elif sys.argv[1] == "load_waypoint_mission":
        mission_command = {
            "type": "load_waypoint_mission",
            "finished_action": "NO_ACTION",
            "auto_flight_speed": 0.01,
            "max_flight_speed": 5.0,
            "heading_mode": "AUTO",
            "waypoints": [
                {
                    "attitude": 1.0,
                    "longitude": 1.0,
                    "latitude": 1.0
                },
                {
                    "attitude": 1.1,
                    "longitude": 1.1,
                    "latitude": 1.1
                },
                {
                    "attitude": 1.2,
                    "longitude": 1.2,
                    "latitude": 1.2
                }
            ]
        }
        client.publish(topic=MQTT_PATH + CLIENT_ID, payload=json.dumps(mission_command))

    elif sys.argv[1] == "land":
        client.publish(topic=MQTT_PATH + CLIENT_ID, payload='{"type":"land"}')
        print("mission send")

    elif sys.argv[1] == "upload_waypoint_mission":
        client.publish(topic=MQTT_PATH + CLIENT_ID, payload='{"type":"upload_waypoint_mission"}')
        print("mission send")

    # Blocking call that processes network traffic, dispatches callbacks and
    # handles reconnecting.
    # Other loop*() functions are available that give a threaded interface and a
    # manual interface.
