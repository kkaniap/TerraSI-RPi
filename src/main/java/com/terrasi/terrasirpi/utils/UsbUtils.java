package com.terrasi.terrasirpi.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class UsbUtils implements SerialPortDataListener {

    private static UsbUtils INSTANCE;
    private static String RPI_PORT;
    private static SerialPort serialPort;
    private final ObjectMapper objectMapper;
    private String receivedData = "";

    private UsbUtils() {
        //serialPort = SerialPort.getCommPort("ttyACM0");
        serialPort = SerialPort.getCommPort("COM3");
        serialPort.setComPortParameters(115200, 8, 1, 0);
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);
        serialPort.addDataListener(this);
        serialPort.setFlowControl(SerialPort.FLOW_CONTROL_RTS_ENABLED);
        serialPort.openPort();
        this.objectMapper = new ObjectMapper();
    }


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

    public void sendData(Object terrariumSettings) throws InterruptedException, IOException {
        try {
            if (serialPort.isOpen()) {
//                serialPort.getOutputStream().write(objectMapper.writeValueAsString(terrariumSettings)
//                        .replaceAll("\"","\\\"")
//                        .replaceAll("false", "0")
//                        .replaceAll("true", "1")
//                        .getBytes());
                serialPort.getOutputStream().write("kania".getBytes());
                serialPort.getOutputStream().flush();
                serialPort.getOutputStream().close();
            }
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
    }

    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        if (serialPortEvent.getEventType() != SerialPort.LISTENING_EVENT_DATA_RECEIVED) {
            return;
        }

        byte[] buffer = serialPortEvent.getReceivedData();
        receivedData += new String(buffer);

        if (receivedData.contains("\n")) {
            receivedData = "";
        }

        try {
            sendData("kania");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
