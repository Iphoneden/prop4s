package ru.dns.prop4s;

import ru.dns.prop4s.common.ILoader;
import ru.dns.prop4s.common.Property;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ManagerProperty {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ManagerProperty.class);

    private static HashMap<Class<?>, Property> managerProperty = new HashMap<>();

    public static <PROPERTY> PROPERTY property(final Class<PROPERTY> pClass) {
        Property<PROPERTY> property = managerProperty.get(pClass);
        return property != null ? property.getProperty() : null;
    }

    public static <PROPERTY> Property<PROPERTY> getProperty(final Class<PROPERTY> pClass) {
        Property<PROPERTY> property = managerProperty.get(pClass);
        if (property == null) {
            property = putProperty(pClass, null);
        }
        return property;
    }

    public static <PROPERTY> Property<PROPERTY> putProperty(final Class<PROPERTY> pClass, ILoader loader) {
        Property<PROPERTY> instance;
        try {
            instance = clone(managerProperty.get(pClass), pClass, loader);
        } catch (IntrospectionException | InvocationTargetException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
        managerProperty.put(pClass, instance);
        return instance;
    }

    private static <PROPERTY> Property<PROPERTY> clone(Property<PROPERTY> property, final Class<PROPERTY> pClass, ILoader loader)
            throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        Property<PROPERTY> instance = new Property<>(pClass, null);
        if (property != null) {
            instance.setNeedUpdated(property.getNeedUpdated());
            instance.setCheckSum(property.getCheckSum());
            Object obj = instance.getProperty();
            Object objRead = property.getProperty();
            final BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass(), Object.class);
            for (PropertyDescriptor field : beanInfo.getPropertyDescriptors()) {
                if (field.getReadMethod() != null && field.getWriteMethod() != null) {
                    field.getWriteMethod().invoke(obj, field.getReadMethod().invoke(objRead));
                }
            }
            instance.setLoader(loader);
        }
        return instance;
    }

    public static boolean isNeedAllUpdated() {
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
                    for (Map.Entry<Property, String> entry : checkSums.entrySet()) {
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
