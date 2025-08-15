package com.example.examplemod.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jline.internal.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MojangAPI {

    public static UUID getUUIDFromUsername(String username) {
        try {
            return getUUID(username);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getUsernameFromUUID(UUID uuid) {
        try {
            return getUsername(uuid);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static @Nullable UUID getUUID(String username) throws Exception {
        String url = "https://api.mojang.com/users/profiles/minecraft/" + username;

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");

        if (connection.getResponseCode() == 204) {
            // User not found
            return null;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
        reader.close();

        String id = json.get("id").getAsString();

        return UUID.fromString(id.replaceFirst(
                "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
                "$1-$2-$3-$4-$5"
        ));
    }

    private static @Nullable String getUsername(UUID uuid) throws IOException {
        String cleanUUID = uuid.toString().replace("-", "");
        URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + cleanUUID);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            String json = response.toString();
            Pattern pattern = Pattern.compile("\"name\"\\s*:\\s*\"([^\"]+)\"");
            Matcher matcher = pattern.matcher(json);
            if (matcher.find()) {
                return matcher.group(1);
            } else {
                throw new IllegalStateException("Name field not found in JSON: " + json);
            }
        }
    }
}