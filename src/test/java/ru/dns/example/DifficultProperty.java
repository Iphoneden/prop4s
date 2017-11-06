package ru.dns.example;

import javax.sound.sampled.AudioFileFormat;

import org.apache.log4j.Level;

public class DifficultProperty extends SimpleProperty {

    public static final AudioFileFormat.Type[] TYPES = new AudioFileFormat.Type[]{
            AudioFileFormat.Type.WAVE, AudioFileFormat.Type.AIFC
    };

    public static final java.util.logging.Level OFF = java.util.logging.Level.OFF;
    public static final java.util.logging.Level SEVERE = java.util.logging.Level.SEVERE;
    public static final java.util.logging.Level WARNING = java.util.logging.Level.WARNING;
    public static final java.util.logging.Level INFO = java.util.logging.Level.INFO;

    public enum Example {Null, Zero, True, False}

    private int field11 = 1;
    private float field12 = 2;
    private String field113 = "test";
    private Example exampleEnum = Example.Null;
    private AudioFileFormat.Type type = AudioFileFormat.Type.WAVE;
    private Level level;

    public int getField11() {
        return field11;
    }

    public void setField11(int field11) {
        this.field11 = field11;
    }

    public float getField12() {
        return field12;
    }

    public void setField12(float field12) {
        this.field12 = field12;
    }

    public String getField113() {
        return field113;
    }

    public void setField113(String field113) {
        this.field113 = field113;
    }

    public Example getExampleEnum() {
        return exampleEnum;
    }

    public void setExampleEnum(Example exampleEnum) {
        this.exampleEnum = exampleEnum;
    }

    public AudioFileFormat.Type getType() {
        return type;
    }

    public void setType(AudioFileFormat.Type type) {
        this.type = type;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "DifficultProperty{" +
                "field11=" + field11 +
                ", field12=" + field12 +
                ", field113='" + field113 + '\'' +
                ", exampleEnum=" + exampleEnum +
                ", type=" + type +
                ", level=" + level +
                '}';
    }
}
