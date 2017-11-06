package ru.dns.prop4s.common.helpers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.lang.reflect.Field;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static ru.dns.prop4s.common.Loader.DELIMITER_ROW;

public final class Utils {

    private Utils() {
    }

    /**
     * considers the amount md5
     *
     * @param string buffer
     * @return string md5
     */
    public static String getMD5(String string) throws NoSuchAlgorithmException, IOException {
        MessageDigest md5;
        md5 = MessageDigest.getInstance("MD5");

        try (DigestInputStream stream = new DigestInputStream(new ByteArrayInputStream(string.getBytes()), md5)) {
            byte[] buffer = new byte[8192];
            while (true) {
                if ((stream.read(buffer) == -1)) break;
            }
            StringBuilder sb = new StringBuilder();
            for (byte b : md5.digest()) {
                sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        }
    }

    /**
     * @param types select by value from the array, allowed by index in array
     * @param val   string
     * @param <T>   class generic
     * @return instance
     * @throws InvalidObjectException if no find
     */
    public static <T> T valueOf(T[] types, String val) throws InvalidObjectException {
        int i = 0;
        for (T type : types) {
            if (String.valueOf(i).equals(val) || type.toString().equalsIgnoreCase(val)) {
                return type;
            }
            i++;
        }
        throw new InvalidObjectException("Value " + val + " not registered in " + Arrays.toString(types));
    }

    public static <T> T valueOf(Map<String, T> types, String val) throws InvalidObjectException {
        int i = 0;
        for (Map.Entry<String, T> type : types.entrySet()) {
            if (String.valueOf(i).equals(val) || type.getKey().equalsIgnoreCase(val)) {
                return type.getValue();
            }
            i++;
        }
        throw new InvalidObjectException("Value " + val + " not registered in " + types.keySet().toString());
    }

    public static <T> T[] getClassValues(Class<T> aClass) throws IllegalAccessException {
        return getClassValues(aClass, aClass);
    }

    /**
     * @param aClass         class by which we look for constants
     * @param classContainer class container where search
     * @param <T>            class generic
     * @return array constants
     * @throws IllegalAccessException system error
     */
    private static <T> T[] getClassValues(Class<T> aClass, Class<?> classContainer) throws IllegalAccessException {
        Map<String, T> staticFields = new LinkedHashMap<>();

        for (Field field : getDeclaredFields(classContainer)) {
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) &&
                    java.lang.reflect.Modifier.isFinal(field.getModifiers()) &&
                    java.lang.reflect.Modifier.isPublic(field.getModifiers()) &&
                    aClass.equals(field.getType())) {
                staticFields.put(field.getName(), (T) field.get(null));
            }
        }
        return staticFields.values().toArray((T[]) new Object[staticFields.size()]);
    }

    public static <T> Map<String, T> getClassValuesByName(Class<T> aClass, Class<?> classContainer) throws IllegalAccessException {
        Map<String, T> map = new LinkedHashMap<>();
        for (Field field : getDeclaredFields(classContainer)) {
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) &&
                    java.lang.reflect.Modifier.isFinal(field.getModifiers()) &&
                    java.lang.reflect.Modifier.isPublic(field.getModifiers()) &&
                    aClass.equals(field.getType())) {
                map.put(field.getName(), (T) field.get(null));
            }
        }
        return map;
    }

    private static List<Class<?>> getSuperClasses(Class<?> classContainer) {
        List<Class<?>> classList = new LinkedList<>();
        classList.add(classContainer);
        Class<?> parentClass = classContainer.getSuperclass();
        while (parentClass != null && !(parentClass.equals(Object.class))) {
            classList.add(parentClass);
            parentClass = parentClass.getSuperclass();
        }
        return classList;
    }

    private static Field[] getDeclaredFields(Class<?> classContainer) {
        List<Class<?>> classList = getSuperClasses(classContainer);
        ListIterator<Class<?>> revers = classList.listIterator(classList.size());

        List<Field> fields = new ArrayList<>();
        while (revers.hasPrevious()) {
            Class<?> aClass = revers.previous();
            Collections.addAll(fields, aClass.getDeclaredFields());
        }
        return fields.toArray(new Field[fields.size()]);
    }

    @SuppressWarnings("unused")
    public static <T> void addComment(StringBuilder builder, String description, T[] types) {
        builder.append("// ").append(description).append(": [");
        int i = 0;
        for (T type : types) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(i).append("-").append(type.toString());
            i++;

        }
        builder.append("]").append(DELIMITER_ROW);
    }

    @SuppressWarnings("unused")
    public static void addComment(StringBuilder builder, String description, float[] types) {
        builder.append("// ").append(description).append(": [");
        int i = 0;
        for (float type : types) {
            if (i > 0) {
                builder.append(", ");
            }
            i++;
            builder.append(type);
        }
        builder.append("]").append(DELIMITER_ROW);
    }

}
