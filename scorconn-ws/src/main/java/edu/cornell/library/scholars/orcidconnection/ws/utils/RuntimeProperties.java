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
        m.put("CLIENT_ID", "APP-2I42YGFI3H4RC1YZ");
        m.put("CLIENT_SECRET", "eca5e9f7-527f-4898-a30d-996b6a635b0e");
        m.put("API_PLATFORM", "sandbox");
        m.put("WEBAPP_BASE_URL", "http://localhost:8888/scorconn-ws");
        m.put("CALLBACK_PATH", "orcidCallback");
        return m;
    }

    /**
     * @return
     */
    public static Map<String, String> getMap() {
        return new HashMap<String, String>(getInstance());
    }
}
