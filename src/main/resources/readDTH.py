import adafruit_dht
import board
import RPi.GPIO as GPIO

DHT_SENSOR_1 = adafruit_dht.DHT11(board.D4, use_pulseio=False)
DHT_SENSOR_2 = adafruit_dht.DHT11(board.D17, use_pulseio=False)

temp1 = None
temp2 = None
humidity1 = None
humidity2 = None
error = None

try:
    temp1 = DHT_SENSOR_1.temperature
    humidity1 = DHT_SENSOR_1.humidity
except Exception as error_1:
    error = error_1.args[0]

try:
    temp2 = DHT_SENSOR_2.temperature
    humidity2 = DHT_SENSOR_2.humidity
except Exception as error_2:
    error = error_2.args[0]

avgTemp = 0.0
avgHumidity = 0.0

if temp1 != None and temp2 != None: avgTemp = (temp1 + temp2) / 2
elif temp1 == None and temp2 != None: avgTemp = temp2
elif temp1 != None and temp2 == None: avgTemp = temp1

if humidity1 != None and humidity2 != None: avgHumidity = (humidity1 + humidity2) / 2
elif humidity1 == None and humidity2 != None: avgHumidity = humidity2
elif humidity1 != None and humidity2 == None: avgHumidity = humidity1

print("{\"temp\":" + str(avgTemp) + ",\"humidity\":" + str(avgHumidity) + "}")

DHT_SENSOR_1.exit()
DHT_SENSOR_2.exit()
GPIO.cleanup()

