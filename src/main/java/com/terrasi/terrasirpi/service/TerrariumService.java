package com.terrasi.terrasirpi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terrasi.terrasirpi.model.TerrariumSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;


@Service
public class TerrariumService {

    private static final Logger LOG = LoggerFactory.getLogger(TerrariumService.class);
    private final ObjectMapper objectMapper;

    public TerrariumService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "Raspberry_kkaniap_Terrarium_test")
    public void getTerrariumSettings(Message message) {
        try {
            TerrariumSettings terrariumSettings = objectMapper.readValue(message.getBody(), TerrariumSettings.class);
            System.out.println(terrariumSettings);
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }
}
