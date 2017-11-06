package ru.dns.prop4s.common;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;

public interface ILoader {

    boolean isChanged(Property property) throws IOException;

    boolean update(Property property) throws IOException;

    String write(Property property, BufferedWriter writer) throws IOException;

    boolean isExistsSource();

    void writeSource(StringWriter writer) throws IOException;

    StringBuilder getHeader();
}
