package ru.tbank.clients;

import okhttp3.*;
import org.json.JSONObject;
import ru.tbank.entities.Location;

import java.io.IOException;

public class LocationApiClient {
    private static final String BASE_URL = "http://localhost:8080/api/v1";

    private final OkHttpClient client;

    public LocationApiClient() {
        this.client = new OkHttpClient();
    }

    public String getAllLocations() throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + "/locations")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response.body().string();
        }
    }

    public String getLocationById(int id) throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + "/locations/" + id)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response.body().string();
        }
    }

    public String createLocation(Location location) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, new JSONObject(location).toString());

        Request request = new Request.Builder()
                .url(BASE_URL + "/locations")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response.body().string();
        }
    }

    public String updateLocation(int id, Location location) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, new JSONObject(location).toString());

        Request request = new Request.Builder()
                .url(BASE_URL + "/locations/" + id)
                .put(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response.body().string();
        }
    }

    public void deleteLocation(int id) throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + "/locations/" + id)
                .delete()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
        }
    }
}
