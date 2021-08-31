package com.terrasi.terrasirpi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.terrasi.terrasirpi.enums.ScriptName;
import com.terrasi.terrasirpi.model.SensorsReads;
import com.terrasi.terrasirpi.model.TerrariumSettings;
import com.terrasi.terrasirpi.utils.PythonUtils;
import com.terrasi.terrasirpi.utils.UsbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class TerrariumService {

    @Value("${terrasirpi.rabbitmq.sensorsQueue.name}")
    private String sensorsQueueName;
    private final RabbitAdmin rabbitAdmin;
    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;
    private static TerrariumSettings terrariumSettings;
    private static final Logger LOG = LoggerFactory.getLogger(TerrariumService.class);

    public TerrariumService(ObjectMapper objectMapper, RabbitTemplate rabbitTemplate) {
        this.objectMapper = objectMapper;
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitAdmin = new RabbitAdmin(rabbitTemplate);
    }

    @RabbitListener(queues = "${terrasirpi.rabbitmq.settingQueue.name}")
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

    public void sendSensorRead(SensorsReads sensorsReads) {
        rabbitAdmin.purgeQueue(sensorsQueueName);
        try {
            rabbitTemplate.convertAndSend(sensorsQueueName, objectMapper.writeValueAsString(sensorsReads));
        } catch (JsonProcessingException e) {
            LOG.error(e.getMessage());
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

    public void sendDataToArduino(TerrariumSettings terrariumSettings) {
        UsbUtils utils = UsbUtils.getInstance();
        utils.sendData(terrariumSettings);
    }

    public void turnOnOffHumidifier(TerrariumSettings terrariumSettings) {
        if (terrariumSettings.getIsHumidifierWorking()) {
            PythonUtils.runScript(PythonUtils.getScript(ScriptName.HumidifierOn));
        } else {
            PythonUtils.runScript(PythonUtils.getScript(ScriptName.HumidifierOff));
        }
    }

    public Boolean isTerrariumOpen() {
        String result = PythonUtils.runScript(PythonUtils.getScript(ScriptName.IsOpen));
        HashMap<String, Boolean> resultMap = deserializeJSON(result, new TypeReference<>() {
        });
        return resultMap.get("isOpen");
    }

    public HashMap<String, Double> readDTH() {
        String result = PythonUtils.runScript(PythonUtils.getScript(ScriptName.ReadDTH));
        return deserializeJSON(result, new TypeReference<>() {
        });
    }

    private <T> HashMap<String, T> deserializeJSON(String json, TypeReference<HashMap<String, T>> type) {
        HashMap<String, T> resultMap = new HashMap<>();
        try {
            resultMap = this.objectMapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            LOG.error(e.getMessage());
        }
        return resultMap;
    }
}
