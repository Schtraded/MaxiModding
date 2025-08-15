package com.example.examplemod.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;

import java.io.FileReader;
import java.io.Reader;
import java.util.*;

public class BlockedUserManager {

    // Store flagged users by UUID
    @Getter
    private static final Set<UUID> flaggedUUIDs = new HashSet<>();
    private static final Map<UUID, String> uuidToName = new HashMap<>();

    public static void loadJson(String pathToJson) {
        flaggedUUIDs.clear();
        uuidToName.clear();

        try (Reader reader = new FileReader(pathToJson)) {
            JsonArray array = JsonParser.parseReader(reader).getAsJsonArray();
            for (JsonElement element : array) {
                JsonObject obj = element.getAsJsonObject();
                UUID uuid = UUID.fromString(obj.get("uuid").getAsString());
                String username = obj.get("username").getAsString();

                flaggedUUIDs.add(uuid);
                uuidToName.put(uuid, username);
            }
            System.out.println("[FlaggedUserManager] Loaded " + flaggedUUIDs.size() + " flagged users.");
        } catch (Exception e) {
            System.err.println("[FlaggedUserManager] Failed to load JSON:");
            e.printStackTrace();
        }
    }

    public static boolean isFlagged(UUID uuid) {
        return flaggedUUIDs.contains(uuid);
    }

    public static boolean nameIsFlagged(String username) {
        return uuidToName.containsValue(username);
    }

    public static Collection<String> getFlaggedNames() {
        return uuidToName.values();
    }

    public static String getName(UUID uuid) {
        return uuidToName.getOrDefault(uuid, null);
    }
}
