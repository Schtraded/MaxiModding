package com.example.examplemod.shader.newTry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ResourceUtil {

    public static String loadResourceAsString(ResourceLocation location) {
        BufferedReader reader = null;
        try {
            IResource resource = Minecraft.getMinecraft()
                    .getResourceManager()
                    .getResource(location);

            reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), UTF_8));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

            return sb.toString();

        } catch (IOException e) {
            throw new RuntimeException("Failed to load resource: " + location, e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {}
            }
        }
    }
}
