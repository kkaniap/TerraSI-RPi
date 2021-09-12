import RPi.GPIO as GPIO

bulbRelay = 12

GPIO.setwarnings(False)
GPIO.setmode(GPIO.BCM)
GPIO.setup(bulbRelay, GPIO.OUT)
GPIO.output(bulbRelay, GPIO.LOW)
GPIO.cleanup(bulbRelay)


