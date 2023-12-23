package org.demkiv.domain.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

@Slf4j
public final class PropertyLoader {
    public static void loadProperties(File fromFile, Object loadInto) {
        try {
            InputStream stream = new FileInputStream(fromFile);
            loadProperties(fromFile.toString(), stream, loadInto);
            stream.close();
        } catch (IOException x) {
            log.error("Error reading property file " + fromFile, x);
        }
    }

    public static void loadProperties(String filename, InputStream fromStream, Object loadInto) {
        Properties props = new Properties();
        try {
            props.load(fromStream);
        } catch (IOException x) {
            log.error("Error reading property file " + filename, x);
            return;
        }
        for (Object o : props.keySet()) {
            String key = o.toString();
            String value = props.getProperty(key).trim();
            setNamedProperty(loadInto, key, value);
        }
    }

    /**
     * Store a given property into an object.  If there is a setter, it will be used.  If not,
     * and if there is a field of the given name, it will be used.
     */
    public static void setNamedProperty(Object loadInto, String key, String value) {
        String methodName = "set" + Character.toUpperCase(key.charAt(0)) + key.substring(1);
        try {
            // see if there is a setXXX(String) method
            try {
                Method setter = loadInto.getClass().getMethod(methodName, String.class);
                setter.invoke(loadInto, value);
                return;
            } catch (NoSuchMethodException ignored) {}
            // see if there is a setXXX(int) method
            try {
                Method setter = loadInto.getClass().getMethod(methodName, Integer.TYPE);
                setter.invoke(loadInto, Integer.valueOf(value));
                return;
            } catch (NoSuchMethodException | NumberFormatException ignored) {}
            // see if there is a setXXX(boolean) method
            try {
                Method setter = loadInto.getClass().getMethod(methodName, Boolean.TYPE);
                boolean bValue = "true".equals(value) || "1".equals(value);
                setter.invoke(loadInto, bValue);
                return;
            } catch (NoSuchMethodException | NumberFormatException ignored) {}
            // see if there is a setParameter(String,String) method
            try {
                Method setter = loadInto.getClass().getMethod("setParameter", String.class, String.class);
                setter.invoke(loadInto, key, value);
                return;
            } catch (NoSuchMethodException ignored) {}
            // see if there is a matching field
            try {
                Field field = loadInto.getClass().getField(key);
                if (field.getType() == String.class)
                    field.set(loadInto, value);
                else if (field.getType() == Integer.class || field.getType() == Integer.TYPE)
                    field.set(loadInto, Integer.parseInt(value));
                else if (field.getType() == Boolean.class || field.getType() == Boolean.TYPE)
                    field.set(loadInto, "true".equals(value) || "1".equals(value));
                else
                    log.error("Unsupported type for " + key + ": " + field.getType());
            } catch (NoSuchFieldException y) {
                log.error("Cannot set property " + key + " on object of type " + loadInto.getClass().getName() + " " + y.getMessage());
            } catch (Exception y) {
                log.error("Error setting property " + key + " on object of type " + loadInto.getClass().getName() + " " + y.getMessage());
            }
        } catch (InvocationTargetException x) {
            log.error("Error in call to " + methodName + ": " + x.getCause(), x);
        } catch (Exception x) {
            log.error("Error in call to " + methodName + ": " + x, x);
        }
    }
}
