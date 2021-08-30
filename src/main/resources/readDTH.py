import adafruit_dht
import board
import RPi.GPIO as GPIO

DHT_SENSOR_1 = adafruit_dht.DHT11(board.D4)
DHT_SENSOR_2 = adafruit_dht.DHT11(board.D17)

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

if temp1 == None: temp1 = 0.0
if temp2 == None: temp2 = 0.0
if humidity1 == None: humidity1 = 0.0
if humidity2 == None: humidity2 = 0.0

avgTemp = (temp1 + temp2) / 2
avgHumidity = (humidity1 + humidity2) / 2

print("{\"temp\":" + str(avgTemp) + ",\"humidity\":" + str(avgHumidity) + "}")

GPIO.cleanup()
