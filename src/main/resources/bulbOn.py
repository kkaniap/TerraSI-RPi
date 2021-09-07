import RPi.GPIO as GPIO

humidifierRelay = 12

GPIO.setwarnings(False)
GPIO.setmode(GPIO.BCM)
GPIO.setup(humidifierRelay, GPIO.OUT)
GPIO.output(humidifierRelay, GPIO.HIGH)
