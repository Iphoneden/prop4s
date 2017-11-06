package ru.dns.prop4s;

import org.junit.Test;
import ru.dns.example.DifficultProperty;
import ru.dns.prop4s.common.Property;
import ru.dns.prop4s.loader.FileLoader;

import javax.sound.sampled.AudioFileFormat;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;

public class DifficultPropertyLoaderTest {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(DifficultPropertyLoaderTest.class);

    @Test
    public void getPropertyTest() throws IOException {
        logger.info("Start test DifficultProperty with putValues and limit AudioFileFormat.Type");
        URL resource = DifficultPropertyLoaderTest.class.getClassLoader().getResource("test.property");
        assert resource != null;
        File fileProp = new File(resource.getFile());
        FileLoader fileLoader = new FileLoader(fileProp);

        fileLoader.putValues(AudioFileFormat.Type.class, new AudioFileFormat.Type[]{AudioFileFormat.Type.WAVE, AudioFileFormat.Type.AIFC});

        Property property = ManagerProperty.putProperty(DifficultProperty.class, fileLoader);
        logger.debug("{}", property.toString());
    }

    @Test
    public void getProperty2Test() throws IOException, IllegalAccessException {
        logger.info("Start test DifficultProperty with putValues");
        URL resource = DifficultPropertyLoaderTest.class.getClassLoader().getResource("test.property");
        assert resource != null;
        File fileProp = new File(resource.getFile());
        FileLoader fileLoader = new FileLoader(fileProp);

        fileLoader.putValues(Level.class, DifficultProperty.class);

        Property property = ManagerProperty.putProperty(DifficultProperty.class, fileLoader);
        logger.debug("{}", property.toString());
    }

    @Test
    public void getProperty3Test() throws IOException {
        logger.info("Start test DifficultProperty with autodetect final static public fields");
        URL resource = DifficultPropertyLoaderTest.class.getClassLoader().getResource("test.property");
        assert resource != null;
        File fileProp = new File(resource.getFile());
        FileLoader fileLoader = new FileLoader(fileProp);

        Property property = ManagerProperty.putProperty(DifficultProperty.class, fileLoader);
        property.update();

        logger.debug("{}", property.toString());
    }

}