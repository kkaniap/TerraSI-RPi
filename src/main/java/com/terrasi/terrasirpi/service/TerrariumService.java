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
    private final UsbUtils usbUtils;

    public TerrariumService(ObjectMapper objectMapper, UsbUtils usbUtils) {
        this.objectMapper = objectMapper;
        this.usbUtils = usbUtils;
    }

    @RabbitListener(queues = "Raspberry_kkaniap_Terrarium_test")
    public void getTerrariumSettings(Message message) {
        try {
            TerrariumSettings terrariumSettings = objectMapper.readValue(message.getBody(), TerrariumSettings.class);
            usbUtils.sendData(terrariumSettings);
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }
}
