package com.terrasi.terrasirpi.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fazecast.jSerialComm.SerialPort;
import com.terrasi.terrasirpi.model.TerrariumSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;


@Component
public class UsbUtils {

    private static UsbUtils INSTANCE;
    private static SerialPort serialPort;
    private final ObjectMapper objectMapper;

    private UsbUtils() {
        serialPort = SerialPort.getCommPort("ttyACM0");
        //serialPort = SerialPort.getCommPort("COM2");
        serialPort.setComPortParameters(9600, 8, 1, 0);
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);
        serialPort.openPort();
        this.objectMapper = new ObjectMapper();
    }

    private static String RPI_PORT;

    @Value("${rpi.usb.port}")
    private void setRpiPort(String port) {
        UsbUtils.RPI_PORT = port;
    }

    public static UsbUtils getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UsbUtils();
        }
        return INSTANCE;
    }

    public void sendData(TerrariumSettings terrariumSettings) throws InterruptedException, IOException {
        System.out.println(terrariumSettings);
        try{
            if (serialPort.isOpen()) {
                serialPort.getOutputStream().write(objectMapper.writeValueAsString(terrariumSettings)
                        .replaceAll("\"","\\\"")
                        .replaceAll("false", "0")
                        .replaceAll("true", "1")
                        .getBytes());

                serialPort.getOutputStream().flush();
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }


    }
}
