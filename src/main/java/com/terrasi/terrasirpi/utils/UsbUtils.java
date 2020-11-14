package com.terrasi.terrasirpi.utils;

import com.fazecast.jSerialComm.SerialPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class UsbUtils {

    private static String RPI_PORT;

    @Value("${rpi.usb.port}")
    private void setRpiPort(String port) {
        UsbUtils.RPI_PORT = port;
    }

    public void sendData(Integer data) throws InterruptedException, IOException {
        SerialPort serialPort = SerialPort.getCommPort(RPI_PORT);
        serialPort.setComPortParameters(9600, 8, 1, 0);
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);
        if(serialPort.openPort()){
            Thread.sleep(3000);
            serialPort.getOutputStream().write(data);
            serialPort.getOutputStream().flush();

        }
        serialPort.closePort();
    }
}
