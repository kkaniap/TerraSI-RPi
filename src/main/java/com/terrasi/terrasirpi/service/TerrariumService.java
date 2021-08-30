package com.terrasi.terrasirpi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terrasi.terrasirpi.enums.ScriptName;
import com.terrasi.terrasirpi.model.TerrariumSettings;
import com.terrasi.terrasirpi.utils.PythonUtils;
import com.terrasi.terrasirpi.utils.UsbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class TerrariumService {

    private final ObjectMapper objectMapper;
    private static TerrariumSettings terrariumSettings;
    private static final Logger LOG = LoggerFactory.getLogger(TerrariumService.class);

    public TerrariumService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "Raspberry_kkaniap_Terrarium_test")
    public void getTerrariumSettings(Message message) {
        TerrariumSettings receivedTerrariumSettings = null;

        try {
            receivedTerrariumSettings = this.objectMapper.readValue(message.getBody(), TerrariumSettings.class);
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }

        if (receivedTerrariumSettings != null && !receivedTerrariumSettings.equals(terrariumSettings)) {
            setNewSettings(receivedTerrariumSettings);
        }
    }

    private void setNewSettings(TerrariumSettings newTerrariumSettings) {
        if (terrariumSettings == null) {
            firstRun(newTerrariumSettings);
        } else if (!terrariumSettings.getLightPower().equals(newTerrariumSettings.getLightPower())) {
            sendDataToArduino(newTerrariumSettings);
        } else if (!terrariumSettings.getIsHumidifierWorking().equals(newTerrariumSettings.getIsHumidifierWorking())) {
            turnOnOffHumidifier(newTerrariumSettings);
        }

        terrariumSettings = newTerrariumSettings;
    }

    private void firstRun(TerrariumSettings newTerrariumSettings) {
        terrariumSettings = newTerrariumSettings;
        sendDataToArduino(terrariumSettings);
        turnOnOffHumidifier(terrariumSettings);
    }

    private void turnOnOffHumidifier(TerrariumSettings terrariumSettings) {
        if (terrariumSettings.getIsHumidifierWorking()) {
            PythonUtils.runScript(PythonUtils.getScript(ScriptName.HumidifierOn));
        } else {
            PythonUtils.runScript(PythonUtils.getScript(ScriptName.HumidifierOff));
        }
    }

    private void sendDataToArduino(TerrariumSettings terrariumSettings) {
        UsbUtils utils = UsbUtils.getInstance();
        utils.sendData(terrariumSettings);
    }
}
