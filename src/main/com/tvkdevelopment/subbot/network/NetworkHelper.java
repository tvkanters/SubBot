package com.tvkdevelopment.subbot.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class NetworkHelper {

    /** The (connect and read) timeout in ms for the HTTP request */
    private static final int TIMEOUT = 10 * 1000;

    public static String get(final String url) {
        // Prepare the HTTP connection
        final URL requestUrl;
        try {
            requestUrl = new URL(url);
        } catch (final MalformedURLException ex) {
            throw new RuntimeException("Invalid URL provided: " + url, ex);
        }

        final HttpURLConnection connection;
        try {
            connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setConnectTimeout(TIMEOUT);
            connection.setReadTimeout(TIMEOUT);
            connection.setUseCaches(false);

        } catch (final IOException ex) {
            System.err.println("IOException while opening HTTP request: " + url);
            return null;
        }

        // Read and parse the HTTP request
        try {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            final StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            reader.close();

            return stringBuilder.toString();

        } catch (final IOException ex) {
            System.err.println("IOException while performing HTTP request: " + url);
            return null;

        } finally {
            connection.disconnect();
        }
    }

}
