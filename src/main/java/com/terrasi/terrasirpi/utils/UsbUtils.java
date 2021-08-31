package com.terrasi.terrasirpi.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.terrasi.terrasirpi.model.TerrariumSettings;
import com.terrasi.terrasirpi.service.TerrariumLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

public class UsbUtils implements SerialPortDataListener {

    private String receivedData = "";
    private static UsbUtils INSTANCE;
    private static SerialPort serialPort;
    private final ObjectMapper objectMapper;
    private static final Logger LOG = LoggerFactory.getLogger(UsbUtils.class);

    private UsbUtils() {
        setSerialPort();
        serialPort.setComPortParameters(115200, 8, 1, 0);
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);
        serialPort.addDataListener(this);
        serialPort.setFlowControl(SerialPort.FLOW_CONTROL_RTS_ENABLED);
        serialPort.openPort();
        this.objectMapper = new ObjectMapper();
    }

    private void setSerialPort() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("application.properties");
        Properties prop = new Properties();
        try {
            prop.load(inputStream);
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
        }
        String port = prop.getProperty("rpi.usb.port");
        serialPort = SerialPort.getCommPort(port);
    }

    public static UsbUtils getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UsbUtils();
        }
        return INSTANCE;
    }

    public void sendData(TerrariumSettings terrariumSettings) {
        try {
            if (serialPort.isOpen()) {
                serialPort.getOutputStream()
                        .write(objectMapper.writeValueAsString(terrariumSettings)
                                .getBytes());
                serialPort.getOutputStream().flush();
                serialPort.getOutputStream().close();
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
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
            try {
                HashMap<String, Integer> resultMap = this.objectMapper.readValue(receivedData, new TypeReference<>() {
                });
                TerrariumLogic.setWaterLevel(resultMap.get("waterLevel"));
            } catch (JsonProcessingException e) {
                LOG.error(e.getMessage());
            }

            receivedData = "";
        }
    }
}
