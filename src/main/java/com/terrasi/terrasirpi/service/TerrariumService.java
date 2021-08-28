package com.terrasi.terrasirpi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terrasi.terrasirpi.model.TerrariumSettings;
import com.terrasi.terrasirpi.utils.UsbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class TerrariumService {

    private static final Logger LOG = LoggerFactory.getLogger(TerrariumService.class);
    private final ObjectMapper objectMapper;
    private static TerrariumSettings terrariumSettings;

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
            terrariumSettings = receivedTerrariumSettings;
            System.out.println(terrariumSettings);
            sendData(terrariumSettings);
        }
    }

    private void sendData(TerrariumSettings terrariumSettings) {
        UsbUtils utils = UsbUtils.getInstance();
        utils.sendData(terrariumSettings);
    }
}
