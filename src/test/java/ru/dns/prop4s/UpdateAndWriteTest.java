package ru.dns.prop4s;

import org.junit.Assert;
import ru.dns.example.SimpleProperty;
import ru.dns.prop4s.common.Property;
import ru.dns.prop4s.loader.FileLoader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class UpdateAndWriteTest {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(DifficultPropertyLoaderTest.class);

    @org.junit.Test
    public void onUpdate() throws IOException {
        URL resource = UpdateAndWriteTest.class.getClassLoader().getResource("test.property");
        URL resourceNew = UpdateAndWriteTest.class.getClassLoader().getResource("test.property.new");
        assert resource != null;
        File fileProp = new File(resource.getFile());
        assert resourceNew != null;
        File filePropNew = new File(resourceNew.getFile());
        File tmp = File.createTempFile("wwww", ".tmp");
        try {
            logger.debug("tmp {}", tmp.getAbsolutePath());
            Files.copy(fileProp.toPath(), tmp.toPath(), REPLACE_EXISTING);
            logger.debug("tmp length {}", tmp.length());
            Property<SimpleProperty> property = ManagerProperty.putProperty(SimpleProperty.class, new FileLoader(tmp));
            logger.debug("{}", property.toString());
            Assert.assertEquals("mast be 333!!", property.getProperty().getField2(), 333, 0);
            logger.debug("isNeedUpdated = {}", property.isNeedUpdated());
            Assert.assertEquals("mast be false!!", property.isNeedUpdated(), false);
            Files.copy(filePropNew.toPath(), tmp.toPath(), REPLACE_EXISTING);
            logger.debug("copy....");
            logger.debug("isNeedUpdated = {}", property.isNeedUpdated());
            Assert.assertEquals("mast be true!!", property.isNeedUpdated(), true);
            logger.debug("update... {}", property.update());
            logger.debug("{}", property.toString());
            Assert.assertEquals("mast be 555!!", property.getProperty().getField2(), 555, 0);
            logger.debug("isNeedUpdated = {}", property.isNeedUpdated());
            Assert.assertEquals("mast be false!!", property.isNeedUpdated(), false);
        } finally {
            if (tmp.exists()) {
                logger.debug("file tmp deleted {}", tmp.delete());
            }
        }
    }

    @org.junit.Test
    public void onWrite() throws IOException {
        URL resource = UpdateAndWriteTest.class.getClassLoader().getResource("test.property");
        File file = new File(new File(resource.getPath()).getParent(), "write.property");
        logger.debug("{} {}", file.getAbsolutePath(), file.exists());
        if (file.exists()) {
            logger.debug("file deleted {}", file.delete());
        }
        assert !file.exists();
        try {
            Property<SimpleProperty> property = ManagerProperty.putProperty(SimpleProperty.class, new FileLoader(file));
            logger.debug("{}", property.toString());
            Assert.assertEquals("mast be 2!!", property.getProperty().getField2(), 2, 0);
            ManagerProperty.writeAll();
            assert file.exists();
            assert !ManagerProperty.isNeedUpdated(SimpleProperty.class);
            assert !ManagerProperty.isNeedAllUpdated();
        } finally {
           // logger.debug("file deleted {}", file.delete());
        }
    }

}
