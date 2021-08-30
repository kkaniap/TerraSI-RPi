import RPi.GPIO as GPIO

switch_1 = 27
switch_2 = 22

GPIO.setmode(GPIO.BCM)
GPIO.setup(switch_1, GPIO.IN, pull_up_down=GPIO.PUD_UP)
GPIO.setup(switch_2, GPIO.IN, pull_up_down=GPIO.PUD_UP)

switch_1_data = GPIO.input(switch_1)
switch_2_data = GPIO.input(switch_2)

if switch_1_data == 1 or switch_2_data == 1:
	print("{\"isOpen\":true}")
else:
	print("{\"isOpen\":false}")

GPIO.cleanup()
