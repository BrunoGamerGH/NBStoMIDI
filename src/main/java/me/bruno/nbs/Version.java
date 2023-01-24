package me.bruno.nbs;

import java.util.Arrays;

public enum Version {

    CLASSIC((byte) 0), V1((byte) 1), V2((byte) 2), V3((byte) 3), V4((byte) 4), V5((byte) 5);


    private byte verNumber;
    Version(byte verNumber) {
        this.verNumber = verNumber;
    }

    public byte getVersionByte() {
        return verNumber;
    }

    public static Version getFromByte(byte value) {
      return Arrays.stream(Version.values()).filter(version -> version.getVersionByte() == value).findFirst().orElse(null);
    }



}
