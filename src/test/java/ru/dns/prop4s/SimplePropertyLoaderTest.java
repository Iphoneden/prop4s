package ru.dns.prop4s;

import org.junit.Assert;
import org.junit.Test;
import ru.dns.example.SimpleProperty;
import ru.dns.prop4s.common.Property;
import ru.dns.prop4s.loader.FileLoader;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class SimplePropertyLoaderTest {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(SimplePropertyLoaderTest.class);

    @Test
    public void getPropertyTest() throws IOException {
        logger.info("Start test");
        URL resource = SimplePropertyLoaderTest.class.getClassLoader().getResource("test.property");
        assert resource != null;
        File fileProp = new File(resource.getFile());

        FileLoader fileLoader = new FileLoader(fileProp);
        ManagerProperty.putProperty(SimpleProperty.class, fileLoader);
        Property<SimpleProperty> property = ManagerProperty.getProperty(SimpleProperty.class);
        SimpleProperty prop = property.getProperty();
        logger.debug("{}",property.toString());
        prop.setField3("update1");
        property.setCheckSum(null);
        logger.debug("{}",property.toString());

        Property<SimpleProperty> prop2 = ManagerProperty.putProperty(SimpleProperty.class, null);
        logger.debug("{}",prop2.toString());
        Assert.assertEquals("значение не должно измениться", prop2.getProperty().getField3(), "update1");
        logger.debug("{}",prop.toString());
        Assert.assertNotEquals("значение должно измениться", prop.getField3(), "test");

        ManagerProperty.update(SimpleProperty.class);
        ManagerProperty.updateAll();
    }

    @Test
    public void getPropertyUpdateTest() throws IOException {
        logger.info("Start test by test.property");
        URL resource = SimplePropertyLoaderTest.class.getClassLoader().getResource("test.property");
        assert resource != null;
        File fileProp = new File(resource.getFile());

        FileLoader fileLoader = new FileLoader(fileProp);
        ManagerProperty.putProperty(SimpleProperty.class, fileLoader);
        Property<SimpleProperty> property = ManagerProperty.getProperty(SimpleProperty.class);
        SimpleProperty prop = property.getProperty();
        logger.debug(prop.toString());
        prop.setField3("update2");
        logger.debug("{}",prop.toString());
        prop = ManagerProperty.property(SimpleProperty.class);
        assert prop != null;
        logger.debug("{}",prop.toString());
        ManagerProperty.updateAll();
        prop = ManagerProperty.property(SimpleProperty.class);
        assert prop != null;
        logger.debug("{}",prop.toString());
    }

}