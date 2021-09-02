package com.terrasi.terrasirpi.service;

import com.terrasi.terrasirpi.model.SensorsReads;
import com.terrasi.terrasirpi.model.TerrariumSettings;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

@Service
public class TerrariumLogic {

    private final TerrariumService terrariumService;
    private static TerrariumSettings terrariumSettings;
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
        terrariumService.sendSensorRead(sensorsReads);
    }

    private void readSensors() {
        Map<String, Double> dthMap = terrariumService.readDTH();
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
            terrariumSettings.setLightPower(0);
            terrariumService.sendDataToArduino(terrariumSettings);
        }
        else if(terrariumSettings.getSunSpeed() == 0
                && terrariumSettings.getSunriseTime().isAfter(LocalTime.now())
                && terrariumSettings.getSunsetTime().isBefore(LocalTime.now())){
            terrariumSettings.setLightPower(100);
            terrariumService.sendDataToArduino(terrariumSettings);
        }
        else if (terrariumSettings.getSunSpeed() != 0
                && terrariumSettings.getSunsetTime().isAfter(LocalTime.now())
                && terrariumSettings.getSunriseTime().isBefore(LocalTime.now())){
            terrariumSettings.setLightPower(Math.max((terrariumSettings.getLightPower() - terrariumSettings.getSunSpeed()), 0));
            terrariumService.sendDataToArduino(terrariumSettings);
        }
        else if(terrariumSettings.getSunSpeed() != 0
                && terrariumSettings.getSunriseTime().isAfter(LocalTime.now())
                && terrariumSettings.getSunsetTime().isBefore(LocalTime.now())){
            terrariumSettings.setLightPower(Math.min((terrariumSettings.getLightPower() + terrariumSettings.getSunSpeed()), 100));
            terrariumService.sendDataToArduino(terrariumSettings);
        }
         LocalTime test = LocalTime.now();
        terrariumSettings.setIsBulbWorking(terrariumSettings.getLightPower() != 0);
    }

    public static void setWaterLevel(Integer waterLevel) {
        sensorsReads.setWaterLevel(waterLevel);
    }

    public static void setSettings(TerrariumSettings settings) {
        terrariumSettings = settings;
    }

    public static SensorsReads getSensorsReads(){
        return sensorsReads;
    }
}
