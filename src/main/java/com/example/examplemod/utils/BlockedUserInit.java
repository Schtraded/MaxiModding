package com.example.examplemod.utils;

import com.example.examplemod.ExampleMod;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;

import java.io.*;
import java.util.*;

public class BlockedUserInit {

    private static final File CONFIG_DIR = new File(Minecraft.getMinecraft().mcDataDir, "config/" + ExampleMod.MODID);
    private static final File FILE_PATH = new File(CONFIG_DIR, "blockedList.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void add(String username) {
        try {
            UUID uuid = MojangAPI.getUUIDFromUsername(username);
            if (uuid == null) {
                return;
            }
            List<Map<String, String>> blockedUsers = loadBlockedUsers();

            Map<String, String> entry = new HashMap<>();
            entry.put("username",  MojangAPI.getUsernameFromUUID(uuid));
            entry.put("uuid", uuid.toString());

            blockedUsers.add(entry);
            saveBlockedUsers(blockedUsers);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void remove(String username) {
        try {
            List<Map<String, String>> blockedUsers = loadBlockedUsers();

            blockedUsers.removeIf(user -> username.equalsIgnoreCase(user.get("username")));
            saveBlockedUsers(blockedUsers);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void syncBlockedUsers(List<String> inputUsernames) {
        try {
            List<Map<String, String>> currentBlockedUsers = loadBlockedUsers();
            Map<String, String> currentUserMap = new HashMap<>();

            // Build map of current users (username -> uuid)
            for (Map<String, String> user : currentBlockedUsers) {
                currentUserMap.put(user.get("username"), user.get("uuid"));
            }

            // Create set of input usernames for quick lookup
            Set<String> inputSet = new HashSet<>();
            for (String name : inputUsernames) {
                inputSet.add(name);
            }

            // Step 1: Add new users not already in the file
            for (String inputName : inputUsernames) {
                if (!currentUserMap.containsKey(inputName)) {
                    UUID uuid = MojangAPI.getUUIDFromUsername(inputName);
                    currentUserMap.put(inputName, uuid.toString());
                }
            }

            // Step 2: Remove users not in the input list
            currentUserMap.entrySet().removeIf(entry -> !inputSet.contains(entry.getKey()));

            // Step 3: Save final list
            List<Map<String, String>> updatedList = new ArrayList<>();
            for (Map.Entry<String, String> entry : currentUserMap.entrySet()) {
                Map<String, String> userEntry = new HashMap<>();
                userEntry.put("username", entry.getKey());
                userEntry.put("uuid", entry.getValue());
                updatedList.add(userEntry);
            }

            saveBlockedUsers(updatedList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<Map<String, String>> loadBlockedUsers() throws IOException {
        List<Map<String, String>> blockedUsers = new ArrayList<Map<String, String>>();

        if (FILE_PATH.exists()) {
            BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH));
            JsonElement element = new JsonParser().parse(reader);
            reader.close();

            if (element.isJsonArray()) {
                for (JsonElement el : element.getAsJsonArray()) {
                    Map<String, String> user = GSON.fromJson(el, Map.class);
                    blockedUsers.add(user);
                }
            }
        }

        return blockedUsers;
    }

    private static void saveBlockedUsers(List<Map<String, String>> blockedUsers) throws IOException {
        if (!CONFIG_DIR.exists()) {
            CONFIG_DIR.mkdirs();
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH));
        GSON.toJson(blockedUsers, writer);
        writer.close();
    }
}
