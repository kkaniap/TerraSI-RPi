package com.terrasi.terrasirpi.utils;

import com.terrasi.terrasirpi.enums.ScriptName;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Component
public class PythonUtils {

    private final Logger LOG = LoggerFactory.getLogger(PythonUtils.class);

    public String runScript(InputStream script) {
        File file = createTempFile(script);
        Process process = null;
        String result = "";

        ProcessBuilder processBuilder = new ProcessBuilder("python3", file.getAbsolutePath());
        processBuilder.redirectErrorStream(true);

        try {
            process = processBuilder.start();
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }

        if (process != null) {
            result = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));
        }

        deleteTempFile(file);

        return result;
    }

    public InputStream getScript(ScriptName scriptName) {
        return PythonUtils.class.getClassLoader()
                .getResourceAsStream(scriptName.getFileName());
    }

    private File createTempFile(InputStream script) {
        File tmpFile = null;
        try {
            tmpFile = File.createTempFile("pythonScript", ".py");
            FileOutputStream outputStream = new FileOutputStream(tmpFile);
            IOUtils.copy(script, outputStream);
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }

        return tmpFile;
    }

    private Boolean deleteTempFile(File file) {
        return file.delete();
    }
}
