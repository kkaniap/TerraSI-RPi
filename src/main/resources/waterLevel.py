import time
import smbus
import numpy as np

NO_TOUCH = 0xFE
THRESHOLD = 100
ATTINY1_HIGH_ADDR = 0x78
ATTINY2_LOW_ADDR = 0x77

i2c_ch = 1

reg_temp = 0x00
reg_config = 0x01

bus = smbus.SMBus(i2c_ch)

def getHigh12SectionValue():
    high_data = bus.read_i2c_block_data(ATTINY1_HIGH_ADDR, reg_config, 12)
    print(high_data)
    return high_data

def getLow8SectionValue():
    low_data = bus.read_i2c_block_data(ATTINY2_LOW_ADDR, reg_config, 8)
    print(low_data)
    return low_data

def check_water_level():
    touch_val = int(0)

    low_data = getLow8SectionValue()
    high_data = getHigh12SectionValue()

    for i in range(8):
        if low_data[i] > THRESHOLD:
            touch_val += 1

    for i in range(12):
        if high_data[i] > THRESHOLD:
            touch_val += 1

    value = (touch_val * 5) / 100
    return value

sensorLevel = check_water_level()
print("{\"waterLevel\":" + str(sensorLevel) + "}")
time.sleep(2)

