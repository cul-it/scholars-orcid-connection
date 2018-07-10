/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.ws.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 * 
 * BOGUS -- should eventually read a properties file, pointed to by a system
 * property, or a JNDI path, or in a default location.
 */
public class RuntimeProperties {
    private static Map<String, String> instance;

    public static String getValue(String key) {
        return getInstance().get(key);
    }

    public static String getValue(String key, String defaultValue) {
        String value = getValue(key);
        return (value == null) ? defaultValue : value;
    }

    private static synchronized Map<String, String> getInstance() {
        if (instance == null) {
            instance = buildInstance();
        }
        return instance;
    }

    private static Map<String, String> buildInstance() {
        // BOGUS
        Map<String, String> m = new HashMap<>();
        m.put("externalAuth.headerName", "netId");
        m.put("sitePassword", "pw");
        return m;
    }
}
