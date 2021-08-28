package com.terrasi.terrasirpi.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.terrasi.terrasirpi.model.TerrariumSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.util.Objects;
import java.util.Properties;

public class UsbUtils implements SerialPortDataListener {

    private static UsbUtils INSTANCE;
    private static SerialPort serialPort;
    private final ObjectMapper objectMapper;
    private String receivedData = "";
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
        String rootPath = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("")).getPath();
        String appConfigPath = rootPath + "application.properties";
        Properties appProps = new Properties();
        try {
            appProps.load(new FileInputStream(appConfigPath));
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
        }
        String port = appProps.getProperty("rpi.usb.port");
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
                serialPort.getOutputStream().write(objectMapper.writeValueAsString(terrariumSettings)
                        .getBytes());
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
            System.out.print(receivedData);
            receivedData = "";
        }
    }
}
