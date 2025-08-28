package com.example.examplemod.features.esp;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.*;

public class EntityHighlighter {

    private static EntityHighlighter INSTANCE;

    // Entity tracking
    private final Map<EntityLivingBase, EntityData> trackedEntities = new HashMap<>();

    // Configuration
    private final Set<String> exactMatchNames = new HashSet<>();
    private final Set<String> containsNames = new HashSet<>();

    // Rendering settings
    private float[] highlightColor = {1.0F, 0.0F, 0.0F, 0.5F}; // Red with 50% transparency
    private boolean renderThroughWalls = false;
    private double maxRenderDistance = 50.0;

    private static class EntityData {
        String nametag;
        String cleanNametag;
        boolean isHighlighted;
        long lastSeen;

        EntityData(String nametag, boolean highlighted) {
            this.nametag = nametag;
            this.cleanNametag = cleanNametag(nametag);
            this.isHighlighted = highlighted;
            this.lastSeen = System.currentTimeMillis();
        }

        void update(String nametag, boolean highlighted) {
            this.nametag = nametag;
            this.cleanNametag = cleanNametag(nametag);
            this.isHighlighted = highlighted;
            this.lastSeen = System.currentTimeMillis();
        }
    }

    private EntityHighlighter() {
        // Register for events
        MinecraftForge.EVENT_BUS.register(this);

        // Add some default target names
        addContainsName("boss");
        addContainsName("target");
        addContainsName("special");
        addContainsName("rare");
        addContainsName("elite");
        addContainsName("champion");
        addContainsName("legendary");
        addContainsName("unique");
        addContainsName("mini");
        addContainsName("event");
    }

    public static EntityHighlighter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new EntityHighlighter();
        }
        return INSTANCE;
    }

    /**
     * Main method called by the mixin to handle entity nametags
     */
    public void handleEntityNametag(EntityLivingBase entity, String displayName) {
        if (entity == null) return;

        // Clean up old entities periodically (every 100 calls)
        if (Math.random() < 0.01) {
            cleanupOldEntities();
        }

        boolean shouldHighlight = evaluateNametag(displayName);

        // Update or create entity data
        EntityData data = trackedEntities.get(entity);
        if (data == null) {
            data = new EntityData(displayName, shouldHighlight);
            trackedEntities.put(entity, data);
        } else {
            data.update(displayName, shouldHighlight);
        }

        if (shouldHighlight) {
            System.out.println("[EntityHighlighter] Highlighting entity: " +
                    (displayName != null ? displayName : entity.getClass().getSimpleName()));
        }
    }

    /**
     * Evaluates whether an entity should be highlighted based on its nametag
     */
    private boolean evaluateNametag(String displayName) {
        if (displayName == null || displayName.isEmpty()) return false;

        String cleanName = cleanNametag(displayName).toLowerCase();
        String originalLower = displayName.toLowerCase();

        // Check for exact matches first
        for (String exactName : exactMatchNames) {
            if (cleanName.equals(exactName.toLowerCase()) ||
                    originalLower.equals(exactName.toLowerCase())) {
                return true;
            }
        }

        // Check for contains matches
        for (String containsName : containsNames) {
            if (cleanName.contains(containsName.toLowerCase()) ||
                    originalLower.contains(containsName.toLowerCase())) {
                return true;
            }
        }

        // Check for formatted text (colored nametags often indicate special mobs)
        if (displayName.contains("§") || displayName.contains("&")) {
            return true;
        }

        // Check for level indicators
        if (cleanName.matches(".*\\[\\d+\\].*") ||
                cleanName.matches(".*lvl\\s*\\d+.*") ||
                cleanName.matches(".*level\\s*\\d+.*")) {
            return true;
        }

        // Check for health indicators
        if (cleanName.contains("❤") || cleanName.contains("♥") ||
                cleanName.contains(" hp ") || cleanName.contains("health")) {
            return true;
        }

        // Check for star or special symbols
        if (cleanName.contains("★") || cleanName.contains("☆") ||
                cleanName.contains("✦") || cleanName.contains("⚡")) {
            return true;
        }

        return false;
    }

    /**
     * Removes Minecraft color codes and formatting
     */
    private static String cleanNametag(String nametag) {
        if (nametag == null) return "";

        // Remove Minecraft color codes (§ and &)
        String cleaned = nametag.replaceAll("§[0-9a-fk-or]", "")
                .replaceAll("&[0-9a-fk-or]", "");

        return cleaned.trim();
    }

    /**
     * Checks if an entity should be highlighted
     */
    public boolean shouldHighlightEntity(EntityLivingBase entity) {
        EntityData data = trackedEntities.get(entity);
        return data != null && data.isHighlighted;
    }

    /**
     * Gets the stored nametag for an entity
     */
    public String getEntityNametag(EntityLivingBase entity) {
        EntityData data = trackedEntities.get(entity);
        return data != null ? data.nametag : null;
    }

    /**
     * Renders highlighted entity hitboxes
     */
    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;

        if (player == null || mc.theWorld == null) return;

        double playerX = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.partialTicks;
        double playerY = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.partialTicks;
        double playerZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.partialTicks;

        // Setup rendering
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();

        if (!renderThroughWalls) {
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
        } else {
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
        }

        // Set highlight color
        GlStateManager.color(highlightColor[0], highlightColor[1], highlightColor[2], highlightColor[3]);

        // Render highlighted entities
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (!(entity instanceof EntityLivingBase)) continue;

            EntityLivingBase livingEntity = (EntityLivingBase) entity;

            if (!shouldHighlightEntity(livingEntity)) continue;

            // Check distance
            double distance = player.getDistanceToEntity(entity);
            if (distance > maxRenderDistance) continue;

            // Calculate render position
            double entityX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * event.partialTicks;
            double entityY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * event.partialTicks;
            double entityZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * event.partialTicks;

            double renderX = entityX - playerX;
            double renderY = entityY - playerY;
            double renderZ = entityZ - playerZ;

            // Render hitbox
            renderEntityHitbox(entity, renderX, renderY, renderZ);
        }

        // Restore rendering state
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    /**
     * Renders the outline of an entity's hitbox
     */
    private void renderEntityHitbox(Entity entity, double x, double y, double z) {
        AxisAlignedBB boundingBox = entity.getEntityBoundingBox();

        // Adjust for render position
        double minX = boundingBox.minX - entity.posX + x;
        double minY = boundingBox.minY - entity.posY + y;
        double minZ = boundingBox.minZ - entity.posZ + z;
        double maxX = boundingBox.maxX - entity.posX + x;
        double maxY = boundingBox.maxY - entity.posY + y;
        double maxZ = boundingBox.maxZ - entity.posZ + z;

        drawOutlinedBoundingBox(new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ));
    }

    /**
     * Draws an outlined bounding box
     */
    private void drawOutlinedBoundingBox(AxisAlignedBB boundingBox) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();

        // Bottom face
        worldRenderer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
        worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
        worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();
        worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
        worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
        worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
        tessellator.draw();

        // Top face
        worldRenderer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
        worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
        worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
        worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
        tessellator.draw();

        // Vertical lines
        worldRenderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
        worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
        worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();

        worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();
        worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();

        worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
        worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();

        worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
        worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        tessellator.draw();
    }

    /**
     * Cleans up dead or old entities
     */
    private void cleanupOldEntities() {
        long currentTime = System.currentTimeMillis();
        long maxAge = 30000; // 30 seconds

        trackedEntities.entrySet().removeIf(entry -> {
            EntityLivingBase entity = entry.getKey();
            EntityData data = entry.getValue();

            return entity.isDead ||
                    !entity.isEntityAlive() ||
                    (currentTime - data.lastSeen) > maxAge;
        });
    }

    // Configuration methods

    public void addExactName(String name) {
        exactMatchNames.add(name);
    }

    public void removeExactName(String name) {
        exactMatchNames.remove(name);
    }

    public void addContainsName(String name) {
        containsNames.add(name);
    }

    public void removeContainsName(String name) {
        containsNames.remove(name);
    }

    public void setHighlightColor(float r, float g, float b, float a) {
        this.highlightColor = new float[]{r, g, b, a};
    }

    public void setRenderThroughWalls(boolean renderThroughWalls) {
        this.renderThroughWalls = renderThroughWalls;
    }

    public void setMaxRenderDistance(double distance) {
        this.maxRenderDistance = distance;
    }

    public void clearExactNames() {
        exactMatchNames.clear();
    }

    public void clearContainsNames() {
        containsNames.clear();
    }

    public void clearAllHighlights() {
        trackedEntities.clear();
    }

    // Utility methods

    public Set<String> getExactNames() {
        return new HashSet<>(exactMatchNames);
    }

    public Set<String> getContainsNames() {
        return new HashSet<>(containsNames);
    }

    public int getTrackedEntityCount() {
        return trackedEntities.size();
    }

    public void printTrackedEntities() {
        System.out.println("[EntityHighlighter] Currently tracked entities: " + trackedEntities.size());
        for (Map.Entry<EntityLivingBase, EntityData> entry : trackedEntities.entrySet()) {
            EntityLivingBase entity = entry.getKey();
            EntityData data = entry.getValue();
            System.out.println("- " + entity.getClass().getSimpleName() +
                    ": '" + data.nametag + "' (Highlighted: " + data.isHighlighted + ")");
        }
    }
}
