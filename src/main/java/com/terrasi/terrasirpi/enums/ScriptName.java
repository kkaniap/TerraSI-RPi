package com.terrasi.terrasirpi.enums;

public enum ScriptName {
    HumidifierOff("humidifierOff.py"),
    HumidifierOn("humidifierOn.py"),
    IsOpen("isOpen.py"),
    ReadDTH("readDTH.py"),
    BulbON("bulbOn.py"),
    BulbOff("bulbOff.py"),
    ReadUV("readUV.py");

    private final String fileName;

    ScriptName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return this.fileName;
    }
}
