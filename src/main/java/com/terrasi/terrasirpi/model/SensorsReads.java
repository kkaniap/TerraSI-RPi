package com.terrasi.terrasirpi.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensorsReads {

    private Long id;
    private Double temperature;
    private Double humidity;
    private Integer brightness;
    private Integer uvaLevel;
    private Integer uvbLevel;
    private Integer waterLevel;
    private Boolean isOpen;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime readDate;
}

