package com.terrasi.terrasirpi.service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.terrasi.terrasirpi.model.TerrariumSettings;
import com.terrasi.terrasirpi.utils.PythonUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.time.LocalTime;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TerrariumServiceTest {

    @Mock
    PythonUtils pythonUtils;

    @Mock
    ObjectMapper objectMapper;

    @Spy
    RabbitTemplate rabbitTemplate = new RabbitTemplate(new CachingConnectionFactory("TEST_FACTORY"));

    @InjectMocks
    TerrariumService terrariumService;

    @AfterEach
    void afterEach() {
        terrariumService = null;
    }

    @Test
    void shouldReturnIsTerrariumOpen() {
        //given
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("isOpen", true);
        given(this.terrariumService.deserializeJSON(anyString(), any())).willReturn(resultMap);
        given(this.pythonUtils.runScript(any())).willReturn("{\"isOpen\":\"true\"}");

        //then
        assertEquals(true, this.terrariumService.isTerrariumOpen());
    }

    @Test
    void shouldReturnReadDTH() {
        //given
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("humidity", 20.5);
        resultMap.put("temp", 30.5);
        given(this.terrariumService.deserializeJSON(any(), any())).willReturn(resultMap);

        //then
        assertEquals(20.5, this.terrariumService.readDTH().get("humidity"));
        assertEquals(30.5, this.terrariumService.readDTH().get("temp"));
    }

    @Test
    void shouldReturnUV(){
        //given
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("uva", 50.0);
        resultMap.put("uvb", 40.0);
        given(this.terrariumService.deserializeJSON(any(), any())).willReturn(resultMap);

        //then
        assertEquals(50.0, this.terrariumService.readUV().get("uva"));
        assertEquals(40.0, this.terrariumService.readUV().get("uvb"));
    }

    @Test
    void shouldReturnWaterLevel(){
        //given
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("waterLevel", 50.0);
        given(this.terrariumService.deserializeJSON(any(), any())).willReturn(resultMap);

        //then
        assertEquals(50.0, this.terrariumService.readWaterLevel());
    }

    @Test
    void shouldTurnOffHumidifier() {
        assertDoesNotThrow(() -> this.terrariumService.turnOnOffHumidifier(false));
    }

    @Test
    void shouldTurnOnHumidifier() {
        assertDoesNotThrow(() -> this.terrariumService.turnOnOffHumidifier(true));
    }

    @Test
    void shouldSendDataToArduino() {
        assertDoesNotThrow(() -> this.terrariumService.sendDataToArduino(prepareTerrariumSetting()));
    }

    @Test
    void shouldExecuteFirstRun() throws IOException {
        //given
        TerrariumSettings terrariumSettings = prepareTerrariumSetting();
        ReflectionTestUtils.setField(terrariumService, "terrariumSettings", null);
        when(this.objectMapper.readValue("".getBytes(), TerrariumSettings.class)).thenReturn(terrariumSettings);

        //when
        this.terrariumService.terrariumSettingsListener(new Message("".getBytes(), new MessageProperties()));

        //then
        assertEquals(terrariumSettings, ReflectionTestUtils.getField(this.terrariumService, "terrariumSettings"));
    }

    @Test
    void shouldExecuteSetSettings() throws IOException {
        //given
        TerrariumSettings terrariumSettings = prepareTerrariumSetting();
        TerrariumSettings newTerrariumSettings = prepareTerrariumSetting();
        newTerrariumSettings.setLightPower(100);
        newTerrariumSettings.setIsHumidifierWorking(true);
        ReflectionTestUtils.setField(terrariumService, "terrariumSettings", terrariumSettings);
        when(this.objectMapper.readValue("".getBytes(), TerrariumSettings.class)).thenReturn(newTerrariumSettings);

        //when
        this.terrariumService.terrariumSettingsListener(new Message("".getBytes(), new MessageProperties()));

        //then
        assertEquals(newTerrariumSettings, ReflectionTestUtils.getField(this.terrariumService, "terrariumSettings"));
    }


    @Test
    void shouldThrowExceptionOnListener() throws IOException {
        //given
        when(this.objectMapper.readValue("".getBytes(), TerrariumSettings.class)).thenThrow(JsonParseException.class);

        //then
        assertAll(() -> terrariumService.terrariumSettingsListener(new Message("".getBytes(), new MessageProperties())));
    }

    @Test
    void shouldThrowExceptionDeserializeJSON() throws IOException {
        //given
        when(this.objectMapper.readValue("".getBytes(), new TypeReference<HashMap<String, Object>>() {
        })).thenThrow(JsonProcessingException.class);

        //when
        assertAll(() -> this.terrariumService.deserializeJSON("", new TypeReference<>() {
        }));
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
