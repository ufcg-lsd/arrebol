package org.fogbowcloud.arrebol.utils;

import java.util.UUID;
import org.json.JSONObject;

import java.util.Collection;
import java.util.Map;

public class AppUtil {

    public static void makeBodyField(JSONObject json, String key, Boolean bool) {
        if (bool != null) {
            json.put(key, bool);
        }
    }

    public static void makeBodyField(JSONObject json, String key, Map map) {
        if (map != null || !map.isEmpty()) {
            json.put(key, map);
        }
    }

    public static void makeBodyField(JSONObject json, String key, Collection collection) {
        if (collection != null && !collection.isEmpty()) {
            json.put(key, collection);
        }
    }

    public static void makeBodyField(JSONObject json, String key, String value) {
        if (value != null && !value.isEmpty()) {
            json.put(key, value);
        }
    }

    public static void makeBodyField(JSONObject json, String key, JSONObject object) {
        if (object != null) {
            json.put(key, object);
        }
    }

    public static String getValueFromJsonStr(String key, String jsonStr) {
        JSONObject json = new JSONObject(jsonStr);
        String value = json.getString(key);
        return value;
    }

    public static String generateUniqueStringId() {
        String uniqueID = UUID.randomUUID().toString();
        return uniqueID;
    }

}
