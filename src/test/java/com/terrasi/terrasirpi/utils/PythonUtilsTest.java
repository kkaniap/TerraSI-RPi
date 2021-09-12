package com.terrasi.terrasirpi.utils;

import com.terrasi.terrasirpi.enums.ScriptName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PythonUtilsTest {

    PythonUtils pythonUtils = new PythonUtils();

    @Test
    void shouldRunScript() throws IOException {
        //given
        PythonUtils mockPythonUtils = mock(PythonUtils.class);
        InputStream inputStream = new ByteArrayInputStream("print(\"test_message\")".getBytes());
        when(mockPythonUtils.getScript(ScriptName.WaterLevel)).thenReturn(inputStream);

        //when
        String result = pythonUtils.runScript(mockPythonUtils.getScript(ScriptName.WaterLevel));

        //then
        assertEquals("test_message", result);
    }

    @Test
    void shouldReturnScript() {
        //when
        InputStream inputStream = pythonUtils.getScript(ScriptName.ReadDTH);

        //then
        assertNotEquals(null, inputStream);
    }

}
