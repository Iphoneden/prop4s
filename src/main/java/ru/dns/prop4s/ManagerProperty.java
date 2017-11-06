package ru.dns.prop4s;

import ru.dns.prop4s.common.ILoader;
import ru.dns.prop4s.common.Property;

import java.io.*;
import java.util.*;

public class ManagerProperty {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ManagerProperty.class);

    private static HashMap<Class<?>, Property> managerProperty = new HashMap<>();

    public static <PROPERTY> PROPERTY property(final Class<PROPERTY> pClass) {
        Property property = managerProperty.get(pClass);
        return property != null ? (PROPERTY) property.getProperty() : null;
    }

    public static <PROPERTY> Property<PROPERTY> getProperty(final Class<PROPERTY> pClass) {
        Property property = managerProperty.get(pClass);
        if (property == null) {
            property = putProperty(pClass, null);
        }
        return property;
    }

    public static <PROPERTY> Property<PROPERTY> putProperty(final Class<PROPERTY> pClass, ILoader loader) {
        Property instance = new Property(pClass, loader);
        managerProperty.put(pClass, instance);
        return instance;
    }

    public static <PROPERTY> Property<PROPERTY> putProperty(final Class<PROPERTY> pClass) throws IOException {
        Property<PROPERTY> property = managerProperty.get(pClass);
        ILoader loader = property != null ? property.getLoader() : null;
        return putProperty(pClass, loader);
    }

    public static boolean isNeedAllUpdated() {
        Collection<Property> values = managerProperty.values();
        for (Property property : managerProperty.values()) {
            try {
                if (property.isNeedUpdated()) {
                    return true;
                }
            } catch (Exception e) {
                logger.error("Error check update {}", property.getProperty().getClass(), e);
            }
        }
        return false;
    }

    public static void updateAll() {
        for (Property property : managerProperty.values()) {
            try {
                property.update();
            } catch (Exception e) {
                logger.error("Error update {}", property.getProperty().getClass(), e);
            }
        }
    }

    public static void writeAll() {
        Set<ILoader> loaders = new HashSet<>();
        for (Property property : managerProperty.values()) {
            ILoader loader = property.getLoader();
            if (loader != null) {
                loaders.add(loader);
            }
        }

        for (ILoader iLoader : loaders) {
            if (!iLoader.isExistsSource()) {
                try {
                    StringWriter sw = new StringWriter();
                    BufferedWriter bw = new BufferedWriter(sw);
                    Map<Property, String> checkSums = new HashMap<>();
                    for (Property property : managerProperty.values()) {
                        ILoader loader = property.getLoader();
                        if (loader != null && loader.equals(iLoader)) {
                            String checkSum = iLoader.write(property, bw);
                            checkSums.put(property, checkSum);
                        }
                    }
                    bw.flush();
                    iLoader.writeSource(sw);
                    for(Map.Entry<Property, String> entry : checkSums.entrySet()){
                        Property property = entry.getKey();
                        property.setCheckSum(entry.getValue());
                        property.setNeedUpdated(false);
                    }
                } catch (Exception e) {
                    logger.error("Error writeAll {}", iLoader.getClass(), e);
                }
            }
        }
    }

    public static boolean isNeedUpdated(Class<?> pClass) throws IOException {
        Property property = managerProperty.get(pClass);
        return property != null && property.isNeedUpdated();
    }


    public static boolean update(Class<?> pClass) throws IOException {
        Property<?> property = managerProperty.get(pClass);
        return property != null && property.isNeedUpdated() && putProperty(pClass, property.getLoader()).update();
    }

}
