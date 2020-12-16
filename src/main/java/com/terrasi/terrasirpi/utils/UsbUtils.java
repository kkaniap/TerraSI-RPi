package com.terrasi.terrasirpi.utils;

import jssc.SerialPort;
import jssc.SerialPortException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class UsbUtils {

    private static String RPI_PORT;
    private byte endLine = 0x0D;

    @Value("${rpi.usb.port}")
    private void setRpiPort(String port) {
        UsbUtils.RPI_PORT = port;
    }

    public void sendData(String data) throws SerialPortException, InterruptedException {
        SerialPort serialPort = new SerialPort(RPI_PORT);
        serialPort.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        serialPort.openPort();
        Thread.sleep(3000);
        serialPort.writeString(data);
        serialPort.writeByte(endLine);
        serialPort.closePort();

    }
}
