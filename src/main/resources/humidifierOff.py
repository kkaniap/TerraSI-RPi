import RPi.GPIO as GPIO

humidifierRelay = 26

GPIO.setwarnings(False)
GPIO.setmode(GPIO.BCM)
GPIO.setup(humidifierRelay, GPIO.IN)
GPIO.cleanup(humidifierRelay)


