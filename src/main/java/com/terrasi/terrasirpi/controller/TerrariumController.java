package com.terrasi.terrasirpi.controller;

import com.terrasi.terrasirpi.model.TerrariumSettings;
import com.terrasi.terrasirpi.utils.UsbUtils;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/terrarium", produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE})
public class TerrariumController {

    private final UsbUtils usbUtils;

    public TerrariumController(UsbUtils usbUtils) {
        this.usbUtils = usbUtils;
    }

    @PostMapping
    public ResponseEntity<Object> setTerrariumSettings(@RequestBody TerrariumSettings settings){
        return null;
    }
}
