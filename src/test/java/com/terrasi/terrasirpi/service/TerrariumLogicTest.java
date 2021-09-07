package com.terrasi.terrasirpi.service;

import com.terrasi.terrasirpi.model.SensorsReads;
import com.terrasi.terrasirpi.model.TerrariumSettings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TerrariumLogicTest {

    @Mock
    TerrariumService terrariumService;

    @InjectMocks
    TerrariumLogic terrariumLogic;

    @Test
    void shouldExecuteLogic() {
        //given
        given(this.terrariumService.isTerrariumOpen()).willReturn(true);
        given(this.terrariumService.readDTH()).willReturn(prepareDTHData());

        //when
        this.terrariumLogic.executeLogic();
        SensorsReads sensorsReads = TerrariumLogic.getSensorsReads();

        //then
        assertEquals(20.0, sensorsReads.getHumidity());
        assertEquals(20.0, sensorsReads.getTemperature());
        assertEquals(true, sensorsReads.getIsOpen());
        assertEquals(LocalDateTime.now().getDayOfYear(), sensorsReads.getReadDate().getDayOfYear());
    }

    @Test
    void shouldExecuteLogicWithToLowHumidity() {
        //given
        given(this.terrariumService.isTerrariumOpen()).willReturn(true);
        given(this.terrariumService.readDTH()).willReturn(prepareDTHData());
        TerrariumSettings terrariumSettings = prepareTerrariumSetting();
        terrariumSettings.setAutoManagement(true);
        TerrariumLogic.setSettings(terrariumSettings);

        //when
        this.terrariumLogic.executeLogic();

        //then
        assertEquals(true, terrariumSettings.getIsHumidifierWorking());
    }

    @Test
    void shouldExecuteLogicWithToHighHumidity() {
        //given
        HashMap<String, Double> dthReads = prepareDTHData();
        dthReads.replace("humidity", 80.0);
        given(this.terrariumService.isTerrariumOpen()).willReturn(true);
        given(this.terrariumService.readDTH()).willReturn(dthReads);
        TerrariumSettings terrariumSettings = prepareTerrariumSetting();
        terrariumSettings.setAutoManagement(true);
        terrariumSettings.setIsHumidifierWorking(true);
        TerrariumLogic.setSettings(terrariumSettings);

        //when
        this.terrariumLogic.executeLogic();

        //then
        assertEquals(false, terrariumSettings.getIsHumidifierWorking());
    }

    @Test
    void shouldExecuteLogicSunrise() {
        //given
        given(this.terrariumService.isTerrariumOpen()).willReturn(true);
        given(this.terrariumService.readDTH()).willReturn(prepareDTHData());
        TerrariumSettings terrariumSettings = prepareTerrariumSetting();
        terrariumSettings.setAutoManagement(true);
        terrariumSettings.setSunSpeed(5);
        terrariumSettings.setLightPower(0);
        terrariumSettings.setSunriseTime(LocalTime.now().plusHours(1));
        terrariumSettings.setSunsetTime(LocalTime.now().minusHours(1));
        TerrariumLogic.setSettings(terrariumSettings);

        //when
        this.terrariumLogic.executeLogic();

        //then
        assertEquals(5, terrariumSettings.getLightPower());
    }

    @Test
    void shouldExecuteLogicSunriseWithoutSunSpeed() {
        //given
        given(this.terrariumService.isTerrariumOpen()).willReturn(true);
        given(this.terrariumService.readDTH()).willReturn(prepareDTHData());
        TerrariumSettings terrariumSettings = prepareTerrariumSetting();
        terrariumSettings.setAutoManagement(true);
        terrariumSettings.setSunSpeed(0);
        terrariumSettings.setLightPower(5);
        terrariumSettings.setSunriseTime(LocalTime.now().plusHours(1));
        terrariumSettings.setSunsetTime(LocalTime.now().minusHours(1));
        TerrariumLogic.setSettings(terrariumSettings);

        //when
        this.terrariumLogic.executeLogic();

        //then
        assertEquals(100, terrariumSettings.getLightPower());
    }

    @Test
    void shouldExecuteLogicSunset() {
        //given
        given(this.terrariumService.isTerrariumOpen()).willReturn(true);
        given(this.terrariumService.readDTH()).willReturn(prepareDTHData());
        TerrariumSettings terrariumSettings = prepareTerrariumSetting();
        terrariumSettings.setAutoManagement(true);
        terrariumSettings.setSunSpeed(5);
        terrariumSettings.setLightPower(100);
        terrariumSettings.setSunriseTime(LocalTime.now().minusHours(1));
        terrariumSettings.setSunsetTime(LocalTime.now().plusHours(1));
        TerrariumLogic.setSettings(terrariumSettings);

        //when
        this.terrariumLogic.executeLogic();

        //then
        assertEquals(95, terrariumSettings.getLightPower());
    }

    @Test
    void shouldExecuteLogicSunsetWithoutSunSpeed() {
        //given
        given(this.terrariumService.isTerrariumOpen()).willReturn(true);
        given(this.terrariumService.readDTH()).willReturn(prepareDTHData());
        TerrariumSettings terrariumSettings = prepareTerrariumSetting();
        terrariumSettings.setAutoManagement(true);
        terrariumSettings.setSunSpeed(0);
        terrariumSettings.setLightPower(100);
        terrariumSettings.setSunriseTime(LocalTime.now().minusHours(1));
        terrariumSettings.setSunsetTime(LocalTime.now().plusHours(1));
        TerrariumLogic.setSettings(terrariumSettings);

        //when
        this.terrariumLogic.executeLogic();

        //then
        assertEquals(0, terrariumSettings.getLightPower());
    }

    @Test
    void shouldSetTerrariumSettings() {
        //given
        TerrariumLogic.setWaterLevel(50);

        //then
        assertEquals(50, TerrariumLogic.getSensorsReads().getWaterLevel());
    }

    private HashMap<String, Double> prepareDTHData() {
        HashMap<String, Double> dthMap = new HashMap<>();
        dthMap.put("humidity", 20.0);
        dthMap.put("temp", 20.0);
        return dthMap;
    }

    private TerrariumSettings prepareTerrariumSetting() {
        TerrariumSettings terrariumSettings = new TerrariumSettings();
        terrariumSettings.setLightPower(50);
        terrariumSettings.setHumidityLevel(50);
        terrariumSettings.setSunSpeed(5);
        terrariumSettings.setIsBulbWorking(false);
        terrariumSettings.setIsHumidifierWorking(false);
        terrariumSettings.setAutoManagement(false);
        terrariumSettings.setSunsetTime(LocalTime.now());
        terrariumSettings.setSunriseTime(LocalTime.now().plusHours(8));
        return terrariumSettings;
    }
}
