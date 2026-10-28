/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.example.credentialcheck;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

final class HttpSupport {

    private HttpSupport() {}

    static HttpResponse get(String url, Map<String, String> headers) throws IOException {
        HttpURLConnection connection = openConnection(url, "GET");
        applyHeaders(connection, headers);
        return readResponse(connection);
    }

    static HttpResponse postForm(String url, String formBody) throws IOException {
        HttpURLConnection connection = openConnection(url, "POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        byte[] body = formBody.getBytes(StandardCharsets.UTF_8);
        connection.setRequestProperty("Content-Length", String.valueOf(body.length));
        try (OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(body);
        }
        return readResponse(connection);
    }

    private static HttpURLConnection openConnection(String url, String method) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod(method);
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        return connection;
    }

    private static void applyHeaders(HttpURLConnection connection, Map<String, String> headers) {
        for (Map.Entry<String, String> header : headers.entrySet()) {
            connection.setRequestProperty(header.getKey(), header.getValue());
        }
    }

    private static HttpResponse readResponse(HttpURLConnection connection) throws IOException {
        int statusCode = connection.getResponseCode();
        InputStream stream = statusCode >= 400 ? connection.getErrorStream() : connection.getInputStream();
        String body = stream == null ? "" : readStream(stream);
        return new HttpResponse(statusCode, body);
    }

    private static String readStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int read;
        while ((read = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, read);
        }
        return new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
    }

    static final class HttpResponse {

        private final int statusCode;
        private final String body;

        HttpResponse(int statusCode, String body) {
            this.statusCode = statusCode;
            this.body = body;
        }

        int getStatusCode() {
            return statusCode;
        }

        String getBody() {
            return body;
        }
    }
}
