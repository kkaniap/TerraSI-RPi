package com.terrasi.terrasirpi.utils;

import com.terrasi.terrasirpi.model.SensorsReads;
import com.terrasi.terrasirpi.model.TerrariumSettings;
import com.terrasi.terrasirpi.service.TerrariumService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class TerrariumLogic {

    private final TerrariumService terrariumService;
    private static final SensorsReads sensorsReads = new SensorsReads();
    private static TerrariumSettings terrariumSettings;

    public TerrariumLogic(TerrariumService terrariumService) {
        this.terrariumService = terrariumService;
    }

    @Scheduled(fixedDelay = 60000)
    private void sendSensorReads() {
        readSensors();
        terrariumService.sendSensorRead(sensorsReads);
    }

    public void readSensors() {
        Map<String, Double> dthMap = terrariumService.readDTH();
        sensorsReads.setHumidity(dthMap.get("humidity"));
        sensorsReads.setTemperature(dthMap.get("temp"));
        sensorsReads.setIsOpen(terrariumService.isTerrariumOpen());
        sensorsReads.setReadDate(LocalDateTime.now());
    }

    public static void setWaterLevel(Integer waterLevel) {
        sensorsReads.setWaterLevel(waterLevel);
    }

}
