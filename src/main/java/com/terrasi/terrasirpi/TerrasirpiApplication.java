package com.terrasi.terrasirpi;

import com.terrasi.terrasirpi.utils.UsbUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TerrasirpiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TerrasirpiApplication.class, args);
        UsbUtils utils = UsbUtils.getInstance();
    }
}
