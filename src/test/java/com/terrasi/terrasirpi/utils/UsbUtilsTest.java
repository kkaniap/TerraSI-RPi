package com.terrasi.terrasirpi.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

@ExtendWith(MockitoExtension.class)
class UsbUtilsTest {

    @Test
    void shouldReturnInstance() {
        //when
        UsbUtils usbUtils = UsbUtils.getInstance();

        //then
        assertNotEquals(null, usbUtils);
    }

}
