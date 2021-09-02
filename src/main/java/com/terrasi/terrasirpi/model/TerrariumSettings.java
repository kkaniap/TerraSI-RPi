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
import java.util.Objects;

@ToString
@Data
public class TerrariumSettings implements Serializable {

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long id;
    private Integer lightPower;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer humidityLevel;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TerrariumSettings that = (TerrariumSettings) o;
        return lightPower.equals(that.lightPower) && humidityLevel.equals(that.humidityLevel) && sunSpeed.equals(that.sunSpeed) && isBulbWorking.equals(that.isBulbWorking) && isHumidifierWorking.equals(that.isHumidifierWorking) && autoManagement.equals(that.autoManagement) && sunriseTime.equals(that.sunriseTime) && sunsetTime.equals(that.sunsetTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lightPower, humidityLevel, sunSpeed, isBulbWorking, isHumidifierWorking, autoManagement, sunriseTime, sunsetTime);
    }
}
