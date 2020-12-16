package com.terrasi.terrasirpi.controller;

import com.terrasi.terrasirpi.model.TerrariumSettings;
import com.terrasi.terrasirpi.utils.UsbUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<String> setTerrariumSettings(@RequestBody TerrariumSettings settings){
        //send to arduino
        System.out.println(settings);
        return new ResponseEntity<>("sd", HttpStatus.OK);
    }

    @PostMapping("/bulbOnOf")
    public ResponseEntity<String> turnBulbOnOf(@RequestBody Map<String, Boolean> param){
        //send to arduino
        System.out.println(param);
        return null;
    }
}
