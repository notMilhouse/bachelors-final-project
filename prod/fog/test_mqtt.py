# /// script
# requires-python = ">=3.9"
# dependencies = [
#     "paho-mqtt>=2.0.0",
# ]
# ///
import paho.mqtt.client as mqtt
from paho.mqtt.properties import Properties
from paho.mqtt.packettypes import PacketTypes
import time
import json
import random

# --- Configuration ---
BROKER_ADDRESS = "localhost"
BROKER_PORT = 1883
TOPIC_RAW = "raw_measurements"
TOPIC_PROFILED = "profiled_measurements"
TOPIC_UNMATCHED = "unmatched_measurements"
CLIENT_ID = f"python-client-{random.randint(0, 1000)}"

# List of topics to subscribe to (topic, qos)
TOPICS_TO_SUBSCRIBE = [
    (TOPIC_RAW, 1),
    (TOPIC_PROFILED, 1),
    (TOPIC_UNMATCHED, 1)
]

# --- Callback Functions ---

def on_connect(client, userdata, flags, rc, properties=None):
    """
    Callback for when the client connects to the broker.
    """
    if rc == 0:
        print(f"Successfully connected to MQTT Broker at {BROKER_ADDRESS}:{BROKER_PORT}")
        client.subscribe(TOPICS_TO_SUBSCRIBE)
        print(f"Subscribed to topics: {[topic[0] for topic in TOPICS_TO_SUBSCRIBE]}")
    else:
        print(f"Failed to connect, return code {rc}\n")

def on_disconnect(client, userdata, flags, rc, properties=None):
    """
    Callback for when the client disconnects.
    Updated for Paho v2 signature: includes 'flags' argument.
    """
    print(f"Disconnected from MQTT Broker with result code {rc}")

def on_message(client, userdata, msg):
    """
    Callback for when a PUBLISH message is received from the broker.
    """
    try:
        print(f"\n--- Message Received ---")
        print(f"Topic:   {msg.topic}")
        payload = msg.payload.decode()
        print(f"Payload: {payload}")
        print(f"QoS:     {msg.qos}")
        
        # Check for message properties (like expiry) if they exist
        if hasattr(msg, 'properties') and msg.properties:
            if hasattr(msg.properties, 'MessageExpiryInterval'):
                print(f"Expiry:  {msg.properties.MessageExpiryInterval}s")

        print("------------------------")
    except Exception as e:
        print(f"Error processing message: {e}")

def on_publish(client, userdata, mid, reason_code, properties=None):
    """
    Callback for when a message is successfully published.
    Updated for Paho v2 signature: includes 'reason_code'.
    """
    pass

# --- Main Script ---

def run_client():
    """
    Initializes and runs the MQTT client.
    """
    # Create Client with MQTTv5 and Callback API V2
    client = mqtt.Client(mqtt.CallbackAPIVersion.VERSION2, client_id=CLIENT_ID, protocol=mqtt.MQTTv5)
    
    client.on_connect = on_connect
    client.on_message = on_message
    client.on_disconnect = on_disconnect
    client.on_publish = on_publish

    try:
        client.connect(BROKER_ADDRESS, BROKER_PORT, keepalive=60)
    except ConnectionRefusedError:
        print(f"Error: Connection refused. Is the MQTT broker running at {BROKER_ADDRESS}:{BROKER_PORT}?")
        return
    except Exception as e:
        print(f"Error connecting to broker: {e}")
        return

    client.loop_start()

    print("MQTT Client started. Publishing messages every 5 seconds...")
    print("Press Ctrl+C to stop.")

    try:
        msg_count = 0
        while True:
            msg_count += 1
            print(f"\nPublishing message set {msg_count}...")
            
            # 1. Publish to raw_measurements (with Expiry)
            raw_payload = {
                "timestamp": int(time.time()),
                "sensor_id": f"sensor-{random.randint(1, 3)}",
                "value": round(random.uniform(15.0, 35.0), 2)
            }
            
            # CORRECT WAY to set Expiry in Paho v2:
            # Create a Properties object for a PUBLISH packet
            publish_props = Properties(PacketTypes.PUBLISH)
            # Set the MessageExpiryInterval property (in seconds)
            publish_props.MessageExpiryInterval = 60
            
            client.publish(TOPIC_RAW, json.dumps(raw_payload), qos=1, properties=publish_props)

            # 2. Publish to profiled_measurements (Retained)
            profiled_payload = {
                "original_timestamp": int(time.time()) - 1,
                "sensor_id": "sensor-1",
                "value_celsius": round(random.uniform(20.0, 25.0), 2),
                "profile_id": "profile_room_A"
            }
            # Retain=True is a standard argument, no properties object needed for just that
            client.publish(TOPIC_PROFILED, json.dumps(profiled_payload), qos=1, retain=True)

            # 3. Publish to unmatched_measurements
            unmatched_payload = {
                "timestamp": int(time.time()),
                "sensor_id": "sensor-999",
                "raw_data": "0xDEADBEEF",
                "error": "No matching profile found"
            }
            client.publish(TOPIC_UNMATCHED, json.dumps(unmatched_payload), qos=1)

            time.sleep(5)

    except KeyboardInterrupt:
        print("\nDisconnecting from broker...")
    finally:
        client.loop_stop()
        client.disconnect()
        print("MQTT Client stopped.")

if __name__ == "__main__":
    run_client()
