package com.tattoos.clientapp.location;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class LocationParser {

    public static JSONObject getGoogleLocationInfo(double lati, double longi) {

        HttpGet httpGet = new HttpGet("http://maps.google.com/maps/api/geocode/json?latlng=" + lati + "," + longi + "&sensor=true");
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static String getLocalityFromGoogleJSON(JSONObject json) {
        try {
            JSONObject firstObject = json.getJSONArray("results").getJSONObject(0);
            JSONObject localityObject = firstObject.getJSONArray("address_components").optJSONObject(2);
            JSONObject AdminAreaObject = firstObject.getJSONArray("address_components").optJSONObject(3);
            JSONObject CountryObject = firstObject.getJSONArray("address_components").optJSONObject(4);
            JSONObject PostalCodeObject = firstObject.getJSONArray("address_components").optJSONObject(5);

            if (localityObject != null) {
                String locality = localityObject.optString("long_name");
                if(locality != null) return locality;
            }
            if (AdminAreaObject != null) {
                String adminArea = AdminAreaObject.optString("long_name");
                if(adminArea != null) return adminArea;
            }
            if (CountryObject != null) {
                String country = CountryObject.optString("long_name");
                if(country != null) return country;
            }
            if (PostalCodeObject != null) {
                String postalCode = PostalCodeObject.optString("long_name");
                if(postalCode != null) return postalCode;
            }
            Log.d("yyy","tava tudo a null");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
