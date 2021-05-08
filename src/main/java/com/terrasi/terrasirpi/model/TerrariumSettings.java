package com.terrasi.terrasirpi.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalTime;

@ToString
@Data
public class TerrariumSettings implements Serializable {

    private Long id;
    private Integer lightPower;
    private Integer humidityLevel;
    private Integer sunSpeed;
    private Boolean isBulbWorking;
    private Boolean isHumidifierWorking;
    private Boolean autoManagement;

    @JsonDeserialize(using = LocalTimeDeserializer.class)
    @JsonSerialize(using = LocalTimeSerializer.class)
    private LocalTime sunriseTime;

    @JsonDeserialize(using = LocalTimeDeserializer.class)
    @JsonSerialize(using = LocalTimeSerializer.class)
    private LocalTime sunsetTime;
}
