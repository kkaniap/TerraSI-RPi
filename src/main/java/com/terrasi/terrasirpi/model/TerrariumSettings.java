package com.terrasi.terrasirpi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long id;
    private Integer lightPower;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer humidityLevel;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer waterLevel;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer sunSpeed;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Boolean isBulbWorking;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Boolean isHumidifierWorking;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Boolean autoManagement;

    @JsonDeserialize(using = LocalTimeDeserializer.class)
    @JsonSerialize(using = LocalTimeSerializer.class)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private LocalTime sunriseTime;

    @JsonDeserialize(using = LocalTimeDeserializer.class)
    @JsonSerialize(using = LocalTimeSerializer.class)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private LocalTime sunsetTime;
}
