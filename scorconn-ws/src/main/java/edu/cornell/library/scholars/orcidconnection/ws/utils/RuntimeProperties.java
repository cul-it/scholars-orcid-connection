/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.ws.utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Reads a properties file from a default location unless overridden by a system
 * property.
 * 
 * The default location is ~/.scorconn.properties
 * 
 * The system property is scorconn.runtime.properties.
 */
public class RuntimeProperties {
    private static final Log log = LogFactory.getLog(RuntimeProperties.class);

    public static final String SYSTEM_PROPERTY = "scorconn.runtime.properties";
    public static final String DEFAULT_PATH = "/.scorconn.properties";

    // ----------------------------------------------------------------------
    // The factory
    // ----------------------------------------------------------------------

    private static volatile RuntimeProperties instance = null;

    public static synchronized void initialize()
            throws RuntimePropertiesException {
        if (instance == null) {
            instance = new RuntimeProperties();
            instance.load();
            log.debug("initialized: " + instance);
        } else {
            throw new IllegalStateException("Already initialized: " + instance);
        }
    }

    public static RuntimeProperties instance() {
        if (instance == null) {
            throw new IllegalStateException(
                    "RuntimeProperties has not been initialized.");
        } else {
            return instance;
        }
    }

    // ----------------------------------------------------------------------
    // The static interface
    // ----------------------------------------------------------------------

    public static String getValue(String key) {
        return instance().map.get(key);
    }

    public static String getValue(String key, String defaultValue) {
        String value = getValue(key);
        return (value == null) ? defaultValue : value;
    }

    public static Map<String, String> getMap() {
        return new HashMap<String, String>(instance().map);
    }

    // ----------------------------------------------------------------------
    // The instance
    // ----------------------------------------------------------------------

    private Map<String, String> map = new HashMap<>();

    private RuntimeProperties load() throws RuntimePropertiesException {
        String path = locatePropertiesFile();
        readProperties(path);
        return this;
    }

    private String locatePropertiesFile() {
        String path = System.getProperty(SYSTEM_PROPERTY);
        if (path != null) {
            return path;
        } else {
            return System.getProperty("user.home") + DEFAULT_PATH;
        }
    }

    private void readProperties(String path) throws RuntimePropertiesException {
        File file = new File(path);
        if (!file.exists()) {
            throw complain("File '%s' doesn't exist.", path);
        }
        if (!file.isFile()) {
            throw complain("File '%s' is not a normal file.", path);
        }
        if (!file.canRead()) {
            throw complain("File '%s' cannot be read.", path);
        }

        Properties props = new Properties();
        try {
            props.load(new FileReader(file));
        } catch (IOException e) {
            throw new RuntimePropertiesException(e);
        }

        for (Object key : props.keySet()) {
            String s = (String) key;
            map.put(s, props.getProperty(s));
        }
    }

    private RuntimePropertiesException complain(String format, Object... args) {
        return new RuntimePropertiesException(String.format(format, args));
    }

    // ----------------------------------------------------------------------
    // Helper classes
    // ----------------------------------------------------------------------

    public static class RuntimePropertiesException extends Exception {
        public RuntimePropertiesException(String message) {
            super(message);
        }

        public RuntimePropertiesException(Throwable cause) {
            super(cause);
        }

        public RuntimePropertiesException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
