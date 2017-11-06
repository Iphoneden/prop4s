package ru.dns.prop4s.loader;

import ru.dns.prop4s.common.Loader;
import ru.dns.prop4s.common.Property;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import static ru.dns.prop4s.common.Loader.*;

/**
 * реализация файл лоадера
 */
public class FileLoader extends Loader {

    private File file;

    public FileLoader(File file) {
        super();
        this.file = file;
    }

    @Override
    public boolean isChanged(Property property) throws IOException {
        if (isExistsSource()) {
            try (FileReader in = new FileReader(file); BufferedReader reader = new BufferedReader(in)) {
                return check(property, reader);
            }
        }
        return false;
    }

    @Override
    public boolean update(Property property) throws IOException {
        if (isExistsSource()) {
            try (FileReader in = new FileReader(file); BufferedReader reader = new BufferedReader(in)) {
                return read(property, reader);
            }
        }
        return false;
    }

    @Override
    public boolean isExistsSource() {
        return file != null && file.exists();
    }

    @Override
    public void writeSource(StringWriter writer) throws IOException {
        if (file != null) {
            try (FileWriter out = new FileWriter(file)) {
                out.write(getHeader().toString());
                out.write(writer.getBuffer().toString());
            }
        }
    }

}
