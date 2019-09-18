package com.example.dora.huawei;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JSONConverter {
    public static String parseFeed(JSONObject jsonObject){
        String userName = "";
        try {
            userName = jsonObject.getString("display_name");
            int exp = jsonObject.getInt("exp");
            String pictureLink = jsonObject.getString("picture");

            return userName;
        }catch (JSONException e) {
            Log.e("ERROR", e.toString());
            return null;
        }
    }

}
