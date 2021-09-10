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
    private final PythonUtils pythonUtils;
    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;
    private static TerrariumSettings terrariumSettings;
    private static final Logger LOG = LoggerFactory.getLogger(TerrariumService.class);

    public TerrariumService(ObjectMapper objectMapper, RabbitTemplate rabbitTemplate, PythonUtils pythonUtils) {
        this.objectMapper = objectMapper;
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitAdmin = new RabbitAdmin(rabbitTemplate);
        this.pythonUtils = pythonUtils;
    }

    @RabbitListener(queues = "${terrasirpi.rabbitmq.settingQueue.name}")
    public void terrariumSettingsListener(Message message) {
        TerrariumSettings receivedTerrariumSettings = null;

        try {
            receivedTerrariumSettings = this.objectMapper.readValue(message.getBody(), TerrariumSettings.class);
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }

        if (receivedTerrariumSettings != null && !receivedTerrariumSettings.equals(terrariumSettings)) {
            handleNewSettings(receivedTerrariumSettings);
            TerrariumLogic.setSettings(receivedTerrariumSettings);
            terrariumSettings = receivedTerrariumSettings;
        }
    }

    private void handleNewSettings(TerrariumSettings newTerrariumSettings) {
        if (terrariumSettings == null && !newTerrariumSettings.getAutoManagement()) {
            firstRun(newTerrariumSettings);
        } else if (!newTerrariumSettings.getAutoManagement()) {
            setSettings(newTerrariumSettings);
        }
    }

    private void firstRun(TerrariumSettings newTerrariumSettings) {
        if(!newTerrariumSettings.getIsBulbWorking()){
            newTerrariumSettings.setLightPower(0);
            this.pythonUtils.runScript(this.pythonUtils.getScript(ScriptName.BulbOff));
        }
        else {
            this.pythonUtils.runScript(this.pythonUtils.getScript(ScriptName.BulbON));
        }
        sendDataToArduino(newTerrariumSettings);
        turnOnOffHumidifier(newTerrariumSettings.getIsHumidifierWorking());

    }

    private void setSettings(TerrariumSettings newTerrariumSettings) {
        if(!newTerrariumSettings.getIsBulbWorking()){
            newTerrariumSettings.setLightPower(0);
            sendDataToArduino(newTerrariumSettings);
            this.pythonUtils.runScript(this.pythonUtils.getScript(ScriptName.BulbOff));
        }else if (!terrariumSettings.getLightPower().equals(newTerrariumSettings.getLightPower())) {
            sendDataToArduino(newTerrariumSettings);
            this.pythonUtils.runScript(this.pythonUtils.getScript(ScriptName.BulbON));
        }

        if (!terrariumSettings.getIsHumidifierWorking().equals(newTerrariumSettings.getIsHumidifierWorking())) {
            turnOnOffHumidifier(newTerrariumSettings.getIsHumidifierWorking());
        }
    }

    public void sendDataToArduino(TerrariumSettings terrariumSettings) {
        UsbUtils utils = UsbUtils.getInstance();
        utils.sendData(terrariumSettings);
    }

    public void turnOnOffHumidifier(Boolean turnOn) {
        if (turnOn) {
            this.pythonUtils.runScript(this.pythonUtils.getScript(ScriptName.HumidifierOn));
        } else {
            this.pythonUtils.runScript(this.pythonUtils.getScript(ScriptName.HumidifierOff));
        }
    }

    public Boolean isTerrariumOpen() {
        String result = this.pythonUtils.runScript(this.pythonUtils.getScript(ScriptName.IsOpen));
        HashMap<String, Boolean> resultMap = deserializeJSON(result, new TypeReference<>() {
        });
        return resultMap.get("isOpen");
    }

    public HashMap<String, Double> readDTH() {
        String result = this.pythonUtils.runScript(this.pythonUtils.getScript(ScriptName.ReadDTH));
        return deserializeJSON(result, new TypeReference<>() {
        });
    }

    public HashMap<String, Double> readUV(){
        String result = this.pythonUtils.runScript(this.pythonUtils.getScript(ScriptName.ReadUV));
        return deserializeJSON(result, new TypeReference<>() {
        });
    }

    public void sendSensorRead(SensorsReads sensorsReads) throws JsonProcessingException{
        rabbitAdmin.purgeQueue(sensorsQueueName);
        rabbitTemplate.convertAndSend(sensorsQueueName, objectMapper.writeValueAsString(sensorsReads));
    }

    public <T> HashMap<String, T> deserializeJSON(String json, TypeReference<HashMap<String, T>> type) {
        HashMap<String, T> resultMap = new HashMap<>();
        try {
            resultMap = this.objectMapper.readValue(json, type);
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return resultMap;
    }
}
