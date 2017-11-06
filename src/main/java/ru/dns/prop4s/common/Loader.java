package ru.dns.prop4s.common;

import org.apache.commons.lang3.StringUtils;
import ru.dns.prop4s.common.helpers.Utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Loader implements ILoader {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(Loader.class);

    public static final String DELIMITER_ROW = "\n";

    private Map<Class<?>, Map<String, ?>> values = new HashMap<>();

    @Override
    public StringBuilder getHeader() {
        return new StringBuilder("// Auto generate"+ DELIMITER_ROW+"// UTF8"+ DELIMITER_ROW+"// " +
                new SimpleDateFormat("dd.MM.yy HH.mm.ss").format(new Date()) + DELIMITER_ROW+ DELIMITER_ROW);
    }

    protected boolean read(Property property, BufferedReader reader) throws IOException {
        Object obj = property.getProperty();
        if (reader == null) {
            return false;
        }
        Map<String, String> allProp = getMapProperty(reader);
        Map<String, String> validProp = new HashMap<>();
        try {
            final BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass(), Object.class);
            for (PropertyDescriptor field : beanInfo.getPropertyDescriptors()) {
                if (field.getReadMethod() != null && field.getWriteMethod() != null) {
                    final String key = field.getName().toLowerCase();
                    if (StringUtils.isNotEmpty(key) && allProp.containsKey(key)) {
                        final String val = allProp.get(key);
                        Class<?> propertyType = field.getPropertyType();
                        try {
                            Object value = getValue(propertyType, val);
                            Type genericSuperclass = propertyType.getGenericSuperclass();
                            if (value != null || genericSuperclass != null) {
                                field.getWriteMethod().invoke(obj, value);
                                validProp.put(key, val);
                            }
                        } catch (IllegalAccessException | InvalidObjectException | NumberFormatException | InvocationTargetException e) {
                            logger.error("Ошибка загрузки атрибута {}({}) <= {}", key, propertyType, val, e);
                        }
                    }
                }
            }
            property.setCheckSum(getCheckSum(validProp));
            return true;
        } catch (IntrospectionException e) {
            logger.error("Ошибка загрузки атрибутов", e);
        }
        return false;
    }

    protected boolean check(Property property, BufferedReader reader) throws IOException {
        Object obj = property.getProperty();
        if (reader == null) {
            return false;
        }
        if (property.getCheckSum() == null) {
            return true;
        }
        Map<String, String> allProp = getMapProperty(reader);
        Map<String, String> validProp = new HashMap<>();
        try {
            final BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass(), Object.class);
            for (PropertyDescriptor field : beanInfo.getPropertyDescriptors()) {
                if (field.getReadMethod() != null && field.getWriteMethod() != null) {
                    final String key = field.getName().toLowerCase();
                    if (StringUtils.isNotEmpty(key) && allProp.containsKey(key)) {
                        final String val = allProp.get(key);
                        validProp.put(key, val);
                    }
                }
            }
            String checkSum = getCheckSum(validProp);
            logger.trace("check {} {} {} validProp {}", !Objects.equals(property.getCheckSum(), checkSum), property.getCheckSum(), checkSum, validProp.toString());
            return !Objects.equals(property.getCheckSum(), checkSum);
        } catch (IntrospectionException e) {
            logger.error("Ошибка проверки изменения атрибутов", e);
        }
        return false;
    }

    private Map<String, String> getMapProperty(BufferedReader reader) throws IOException {
        Map<String, String> stringMap = new ConcurrentHashMap<>();
        try (BufferedReader bufferedReader = new BufferedReader(reader)) {
            for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                final String[] split = line.trim().split("=");
                if (split.length >= 1) {
                    final String key = split[0].trim().toLowerCase();
                    if (StringUtils.isNotEmpty(key) && !stringMap.containsKey(key)) {
                        String value = split.length > 1 ? split[1].trim() : "";
                        stringMap.put(key, value);
                    }
                }
            }
        }
        return stringMap;
    }

    private String getCheckSum(Map<String, String> validProp) {
        try {
            return Utils.getMD5(validProp.toString());
        } catch (NoSuchAlgorithmException | IOException e) {
            logger.error("MD5 checksum error", e);
            return null;
        }
    }

    protected Object getValue(Class<?> aClass, String val) throws InvalidObjectException {
        if (StringUtils.isEmpty(val)) {
            return null;
        } else if (String.class.equals(aClass)) {
            return val;
        } else if (StringBuffer.class.equals(aClass)) {
            return new StringBuffer(val);
        } else if (boolean.class.equals(aClass)) {
            String trim = val.toLowerCase().replace("да", "true").replace("нет", "false").replace("д", "true").replace("н", "false").replace(" ", "");
            if ("!false".equals(trim) || "true".equals(trim) || "1".equals(trim) || "y".equals(trim) || "yes".equals(trim)) {
                return true;
            } else if ("!true".equals(trim) || "false".equals(trim) || "0".equals(trim) || "n".equals(trim) || "no".equals(trim) || "not".equals(trim)) {
                return false;
            } else {
                throw new NumberFormatException("For input string: \"" + val + "\"");
            }
        } else if (int.class.equals(aClass) || Integer.class.equals(aClass)) {
            return Integer.parseInt(val);
        } else if (long.class.equals(aClass) || Long.class.equals(aClass)) {
            return Long.parseLong(val);
        } else if (float.class.equals(aClass)) {
            return Float.parseFloat(val);
        } else if (double.class.equals(aClass)) {
            return Double.parseDouble(val);
        } else if (values.containsKey(aClass)) {
            return Utils.valueOf(values.get(aClass), val);
        } else if (aClass.isEnum()) {
            Object[] values = aClass.getEnumConstants();
            return Utils.valueOf(values, val);
        } else {
            try {
                Object[] classValues = Utils.getClassValues(aClass);
                if (classValues.length != 0) {
                    return Utils.valueOf(classValues, val);
                }
            } catch (IllegalAccessException e) {
                logger.error("Error find final static public fields in class {}", aClass, e);
            }
        }
        throw new InvalidObjectException("Not supported " + aClass + " with value \"" + val + "\"");
    }

    public String write(Property property, BufferedWriter writer) throws IOException {
        Object obj = property.getProperty();
        final BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(obj.getClass(), Object.class);
        } catch (IntrospectionException e) {
            logger.error("Error export from {}", obj.getClass().getSimpleName(), e);
            return null;
        }
        Map<String, String> validProp = new HashMap<>();
        String lastChars = null;
        for (PropertyDescriptor field : beanInfo.getPropertyDescriptors()) {
            if (field.getReadMethod() != null && field.getWriteMethod() != null) {
                try {
                    final Object invoke = field.getReadMethod().invoke(obj);
                    Object o = invoke != null ? invoke : "";
                    final String str = field.getName() + "=" + o;
                    validProp.put(field.getName().toLowerCase(), o.toString());
                    if (lastChars == null || !str.startsWith(lastChars)) {
                        lastChars = str.substring(0, 2);
                        writer.append(DELIMITER_ROW);
                    }
                    writer.append(str).append(DELIMITER_ROW);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        String checkSum = getCheckSum(validProp);
        return checkSum;
    }

    public <E> void putValues(Class<E> typeClass, Map<String, E> types) {
        values.put(typeClass, types);
    }

    public <E> void putValues(Class<E> typeClass, E[] types) {
        Map<String, E> classValuesByName = new LinkedHashMap<>();
        for (E type : types) {
            classValuesByName.put(type.toString(), type);
        }
        this.values.put(typeClass, classValuesByName);
    }

    public <E> void putValues(Class<E> typeClass, Class<?> container) throws IllegalAccessException, InvalidObjectException {
        Map<String, E> values = Utils.getClassValuesByName(typeClass, container);
        if (values.isEmpty()) {
            throw new InvalidObjectException("The container " + container + " does not contain any enumerations " + typeClass);
        }
        this.values.put(typeClass, values);
    }

}
