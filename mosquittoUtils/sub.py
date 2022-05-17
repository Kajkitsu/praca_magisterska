import paho.mqtt.client as mqtt

MQTT_SERVER = "172.16.4.73"
MQTT_PATH = "droman/picture/#"
USERNAME = "admin"
PASSWORD = "letmein"


def on_connect(_client, userdata, flags, rc):
    print("Connected with result code " + str(rc))
    _client.subscribe(MQTT_PATH)


def on_message(_client, userdata, msg):
    f = open('output.jpeg', "wb")
    f.write(msg.payload)
    print("Image Received")
    f.close()


if __name__ == '__main__':
    client = mqtt.Client()
    client.username_pw_set(username=USERNAME, password=PASSWORD)
    client.on_connect = on_connect
    client.on_message = on_message
    client.connect(MQTT_SERVER, 1883, 60)
    client.loop_forever()
