package com.example.htmlwrapper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.webkit.JavascriptInterface;

import org.json.JSONArray;

import java.io.IOException;

public class WebAppInterface {
    private Context mContext;
    private static final String PREFS_NAME = "HtmlWrapperPrefs";

    WebAppInterface(Context c) {
        mContext = c;
    }

    @JavascriptInterface
    public String getPreference(String key) {
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(key, null);
    }

    @JavascriptInterface
    public void setPreference(String key, String value) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }

    @JavascriptInterface
    public String listAssets() {
        AssetManager assetManager = mContext.getAssets();
        try {
            String[] files = assetManager.list("");
            JSONArray jsonArray = new JSONArray();
            for (String file : files) {
                if (file.endsWith(".html")) {
                    jsonArray.put(file);
                }
            }
            return jsonArray.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "[]";
        }
    }
}
