package com.terrasi.terrasirpi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.terrasi.terrasirpi.model.SensorsReads;
import com.terrasi.terrasirpi.model.TerrariumSettings;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;

@Service
public class TerrariumLogic {

    private final TerrariumService terrariumService;
    private static TerrariumSettings terrariumSettings;
    private static TerrariumSettings currentSettings;
    private static final SensorsReads sensorsReads = new SensorsReads();

    public TerrariumLogic(TerrariumService terrariumService) {
        this.terrariumService = terrariumService;
    }

    @Scheduled(fixedDelay = 60000)
    public void executeLogic() {
        readSensors();
        if (terrariumSettings != null && terrariumSettings.getAutoManagement()) {
            runAutoManagement();
        }
        try {
            terrariumService.sendSensorRead(sensorsReads);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private void readSensors() {
        HashMap<String, Double> dthMap = terrariumService.readDTH();
        sensorsReads.setHumidity(dthMap.get("humidity"));
        sensorsReads.setTemperature(dthMap.get("temp"));
        sensorsReads.setIsOpen(terrariumService.isTerrariumOpen());
        sensorsReads.setReadDate(LocalDateTime.now());
    }

    private void runAutoManagement() {
        handleHumidity();
        handleLight();
    }

    private void handleHumidity() {
        if (sensorsReads.getHumidity() < terrariumSettings.getHumidityLevel()) {
            terrariumService.turnOnOffHumidifier(true);
            terrariumSettings.setIsHumidifierWorking(true);
        }
        else if (sensorsReads.getHumidity() > terrariumSettings.getHumidityLevel()) {
            terrariumService.turnOnOffHumidifier(false);
            terrariumSettings.setIsHumidifierWorking(false);
        }
    }

    private void handleLight() {
        if (terrariumSettings.getSunSpeed() == 0
                && terrariumSettings.getSunsetTime().isAfter(LocalTime.now())
                && terrariumSettings.getSunriseTime().isBefore(LocalTime.now())) {
            currentSettings.setLightPower(0);
            terrariumService.sendDataToArduino(currentSettings);
        }
        else if(terrariumSettings.getSunSpeed() == 0
                && terrariumSettings.getSunriseTime().isAfter(LocalTime.now())
                && terrariumSettings.getSunsetTime().isBefore(LocalTime.now())){
            currentSettings.setLightPower(terrariumSettings.getLightPower());
            terrariumService.sendDataToArduino(currentSettings);
        }
        else if (terrariumSettings.getSunSpeed() != 0
                && terrariumSettings.getSunsetTime().isAfter(LocalTime.now())
                && terrariumSettings.getSunriseTime().isBefore(LocalTime.now())){
            currentSettings.setLightPower(Math.max((currentSettings.getLightPower() - currentSettings.getSunSpeed()), 0));
            terrariumService.sendDataToArduino(currentSettings);
        }
        else if(terrariumSettings.getSunSpeed() != 0
                && terrariumSettings.getSunriseTime().isAfter(LocalTime.now())
                && terrariumSettings.getSunsetTime().isBefore(LocalTime.now())){
            currentSettings.setLightPower(Math.min((currentSettings.getLightPower() + currentSettings.getSunSpeed()), terrariumSettings.getLightPower()));
            terrariumService.sendDataToArduino(currentSettings);
        }
    }

    public static void setWaterLevel(Integer waterLevel) {
        sensorsReads.setWaterLevel(waterLevel);
    }

    public static void setSettings(TerrariumSettings settings) {
        terrariumSettings = settings;
        currentSettings = settings;
    }

    public static SensorsReads getSensorsReads(){
        return sensorsReads;
    }
}
