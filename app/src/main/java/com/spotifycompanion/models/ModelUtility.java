package com.spotifycompanion.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ModelUtility {
    /**
     * Double check for existence and not null
     * @param obj the object containing the key-value pair
     * @param key string key for checking key-value pair
     * @return key-value exists and is not null?
     */
    public static boolean exists(JSONObject obj, String key) {
        return obj.has(key) && !obj.isNull(key);
    }

    /**
     * Checks key-value pair for existence and returns the <strong>string</strong> value
     * @param obj the object containing the key-value pair
     * @param key requested string key
     * @return the value corresponding to the key
     */
    public static String getString(JSONObject obj, String key) {
        try {
            return exists(obj,key) ? obj.getString(key):null;
        } catch (JSONException e) {
            return null;
        }
    }

    /**
     * Checks key-value pair for existence and returns the <strong>boolean</strong> value
     * @param obj the object containing the key-value pair
     * @param key requested string key
     * @return the value corresponding to the key
     */
    public static Boolean getBoolean(JSONObject obj, String key) {
        try {
            return exists(obj, key) ? obj.getBoolean(key) : null;
        } catch (JSONException e) {
            return null;
        }
    }

    /**
     * Checks key-value pair for existence and returns the <strong>integer</strong> value
     * @param obj the object containing the key-value pair
     * @param key requested string key
     * @return the value corresponding to the key
     */
    public static Integer getInteger(JSONObject obj, String key) {
        try {
            return exists(obj,key) ? obj.getInt(key):null;
        } catch (JSONException e) {
            return null;
        }
    }

    /**
     * Checks key-value pair for existence and returns the <strong>JSONArray</strong> object
     * @param obj the object containing the key-value pair
     * @param key requested string key
     * @return the value corresponding to the key
     */
    public static JSONArray getJSONArray(JSONObject obj, String key) {
        try {
            return exists(obj,key) ? obj.getJSONArray(key):null;
        } catch (JSONException e) {
            return null;
        }
    }

    /**
     * Checks key-value pair for existence and returns the <strong>JSONObject</strong> object
     * @param obj the object containing the key-value pair
     * @param key requested string key
     * @return the value corresponding to the key
     */
    public static JSONObject getJSONObject(JSONObject obj, String key) {
        try {
            return exists(obj,key) ? obj.getJSONObject(key):null;
        } catch (JSONException e) {
            return null;
        }
    }
}
