package org.fogbowcloud.arrebol.core.utils;

import org.json.JSONObject;

public class AppUtil {

    public static void makeBodyField(JSONObject json, String propKey, String prop) {
        if (prop != null && !prop.isEmpty()) {
            json.put(propKey, prop);
        }
    }

    public static void makeBodyField(JSONObject json, String propKey, JSONObject prop) {
        if (prop != null) {
            json.put(propKey, prop);
        }
    }
}
