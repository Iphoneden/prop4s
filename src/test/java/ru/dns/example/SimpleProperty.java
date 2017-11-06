package ru.dns.example;

import org.apache.log4j.Level;

public class SimpleProperty {

    private int field1 = 1;
    private float field2 = 2;
    private String field3 = "test";
    private StringBuffer stringBuffer = new StringBuffer("test StringBuffer");
    private Integer anInt = 1;
    private Long aLong = 1L;
    private Level level = Level.ALL;

    public int getField1() {
        return field1;
    }

    public void setField1(int field1) {
        this.field1 = field1;
    }

    public float getField2() {
        return field2;
    }

    public void setField2(float field2) {
        this.field2 = field2;
    }

    public String getField3() {
        return field3;
    }

    public void setField3(String field3) {
        this.field3 = field3;
    }

    public StringBuffer getStringBuffer() {
        return stringBuffer;
    }

    public void setStringBuffer(StringBuffer stringBuffer) {
        this.stringBuffer = stringBuffer;
    }

    public Integer getAnInt() {
        return anInt;
    }

    public void setAnInt(Integer anInt) {
        this.anInt = anInt;
    }

    public Long getaLong() {
        return aLong;
    }

    public void setaLong(Long aLong) {
        this.aLong = aLong;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "SimpleProperty{" +
                "field1=" + field1 +
                ", field2=" + field2 +
                ", field3='" + field3 + '\'' +
                ", stringBuffer=" + stringBuffer +
                ", anInt=" + anInt +
                ", aLong=" + aLong +
                ", level=" + level +
                '}';
    }
}
