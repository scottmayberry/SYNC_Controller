package com.ford.kcooley8.SYNCController;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;

import ai.api.AIConfiguration;
import ai.api.GsonFactory;
import ai.api.SessionIdStorage;
import ai.api.http.HttpClient;
import ai.api.model.AIRequest;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class AIRequestSender {

    private static String BASE_URL = "https://api.api.ai/v1/";
    private static String CLIENT_ACCESS_TOKEN = "c8595aec2748432e921856969be46e66";
    private static final Gson gson = GsonFactory.getGson();
    private static String sessionID = null;

    public static String sendRequest(byte[] voiceData) {
        sessionID = SessionIdStorage.getSessionId(BaseActivity.currentActivity.getApplicationContext());
        HttpURLConnection connection = null;
        HttpClient httpClient = null;
        String response = null;

        try {
            final URL url = new URL(BASE_URL + "query");
            connection = (HttpURLConnection) url.openConnection();

            connection.addRequestProperty("Authorization", "Bearer " + CLIENT_ACCESS_TOKEN);
            connection.addRequestProperty("Accept", "application/json");
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            httpClient = new HttpClient(connection);
            httpClient.setWriteSoundLog(false); // TODO what does this mean

            httpClient.connectForMultipart();

            final AIRequest request = new AIRequest();
            request.setLanguage("en");
            request.setSessionId(sessionID); // TODO
            request.setTimezone(Calendar.getInstance().getTimeZone().getID());

            final String queryData = gson.toJson(request);

            httpClient.addFormPart("request", queryData);
            InputStream voiceDataStream = new ByteArrayInputStream(voiceData);
            httpClient.addFilePart("voiceData", "audioPassThru.raw", voiceDataStream);
            httpClient.finishMultipart();

            response = httpClient.getResponse();

        } catch (final IOException e) {
            e.printStackTrace();
            if (httpClient != null) {
                final String errorString = httpClient.getErrorString();
                Log.e("http error", errorString);
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return response;
    }
}
