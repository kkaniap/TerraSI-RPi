package com.terrasi.terrasirpi.utils;

import com.terrasi.terrasirpi.model.SensorsReads;
import com.terrasi.terrasirpi.service.TerrariumService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class TerrariumLogic {

    private final TerrariumService terrariumService;
    private static final SensorsReads sensorsReads = new SensorsReads();

    public TerrariumLogic(TerrariumService terrariumService) {
        this.terrariumService = terrariumService;
    }

    @Scheduled(fixedDelay = 60000)
    private void sendSensorReads() {
        readSensors();
        terrariumService.sendSensorRead(sensorsReads);
    }

    public void readSensors() {
        sensorsReads.setHumidity(terrariumService.readDTH().get("humidity"));
        sensorsReads.setTemperature(terrariumService.readDTH().get("temp"));
        sensorsReads.setIsOpen(terrariumService.isTerrariumOpen());
    }

    public static void setWaterLevel(Integer waterLevel) {
        sensorsReads.setWaterLevel(waterLevel);
    }
}
