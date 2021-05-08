package com.terrasi.terrasirpi.controller;

import com.terrasi.terrasirpi.model.TerrariumSettings;
import com.terrasi.terrasirpi.utils.UsbUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/terrarium", produces = {MediaType.APPLICATION_JSON_VALUE})
public class TerrariumController {

    private final UsbUtils usbUtils;

    public TerrariumController(UsbUtils usbUtils) {
        this.usbUtils = usbUtils;
    }

    @PostMapping("/settings")
    public ResponseEntity<String> setTerrariumSettings(@RequestBody TerrariumSettings settings) throws IOException, InterruptedException {
        //send to arduino
        System.out.println(settings);
        //usbUtils.sendData("bulbPower=" + settings.getLightPower());
        return new ResponseEntity<>("sd", HttpStatus.OK);
    }

    @PostMapping("/bulbOnOf")
    public ResponseEntity<String> turnBulbOnOf(@RequestBody Map<String, String> data) throws IOException, InterruptedException {
        return null;
    }

    @PostMapping("/humidifierOnOff")
    public ResponseEntity<String> turnHumidifierOnOff(@RequestBody Map<String, String> data) throws IOException, InterruptedException {
        System.out.println(data);
        //usbUtils.sendData("humidifier=" + data.get("humidifier"));
        return null;
    }
}
