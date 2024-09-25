package ru.tbank.clients;

import okhttp3.*;
import org.json.JSONObject;
import ru.tbank.entities.Category;

import java.io.IOException;

public class CategoryApiClient {
    private static final String BASE_URL = "http://localhost:8080/api/v1/places";

    private final OkHttpClient client;

    public CategoryApiClient() {
        this.client = new OkHttpClient();
    }

    public String getAllCategories() throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + "/categories")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response.body().string();
        }
    }

    public String getCategoryById(int id) throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + "/categories/" + id)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response.body().string();
        }
    }

    public String createCategory(Category category) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, new JSONObject(category).toString());

        Request request = new Request.Builder()
                .url(BASE_URL + "/categories")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response.body().string();
        }
    }

    public String updateCategory(int id, Category category) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, new JSONObject(category).toString());

        Request request = new Request.Builder()
                .url(BASE_URL + "/categories/" + id)
                .put(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response.body().string();
        }
    }

    public void deleteCategory(int id) throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + "/categories/" + id)
                .delete()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
        }
    }
}
