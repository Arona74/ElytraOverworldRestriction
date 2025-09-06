package io.arona74.elytraoverworldrestriction.trinkets;

import io.arona74.elytraoverworldrestriction.config.ElytraConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ElytraOverworldTrinketRenderer {
    
    // This will implement TrinketRenderer interface methods via reflection
    // Based on https://github.com/emilyploszaj/trinkets/blob/1.20.1/src/main/java/dev/emi/trinkets/api/client/TrinketRenderer.java
    
    public void render(ItemStack stack, Object slotReference, Object contextModel, Object matrices, Object vertexConsumers, int light, Object entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        System.out.println("=== TRINKET RENDERER CALLED ===");
        System.out.println("Item: " + stack.getItem());
        
        // Only handle elytra items
        if (stack.getItem() != Items.ELYTRA) {
            // Call default rendering for non-elytra items
            renderDefault(stack, slotReference, contextModel, matrices, vertexConsumers, light, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
            return;
        }
        
        // Only apply to players
        if (!(entity instanceof PlayerEntity)) {
            renderDefault(stack, slotReference, contextModel, matrices, vertexConsumers, light, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
            return;
        }
        
        PlayerEntity player = (PlayerEntity) entity;
        
        System.out.println("Trinket elytra render for player: " + player.getName().getString());
        System.out.println("On ground: " + player.isOnGround() + ", Fall flying: " + player.isFallFlying());
        
        // Check config setting
        ElytraConfig config = ElytraConfig.getInstance();
        if (!config.invisibleOnGround) {
            System.out.println("Config disabled, rendering trinket elytra normally");
            renderDefault(stack, slotReference, contextModel, matrices, vertexConsumers, light, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
            return;
        }
        
        // Check if player should have invisible elytra
        if (player.isOnGround() && !player.isFallFlying()) {
            System.out.println("HIDING TRINKET ELYTRA (player on ground)");
            // Don't call renderDefault - this hides the elytra
            return;
        }
        
        System.out.println("Rendering trinket elytra normally (player flying/in air)");
        // Render normally when flying or in air
        renderDefault(stack, slotReference, contextModel, matrices, vertexConsumers, light, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
    }
    
    private void renderDefault(ItemStack stack, Object slotReference, Object contextModel, Object matrices, Object vertexConsumers, int light, Object entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        try {
            // Call the static renderElytra method from TrinketRenderer interface
            // This is the default implementation that Trinkets provides
            Class<?> trinketRendererClass = Class.forName("dev.emi.trinkets.api.client.TrinketRenderer");
            
            java.lang.reflect.Method renderElytraMethod = trinketRendererClass.getDeclaredMethod("renderElytra", 
                ItemStack.class, 
                Class.forName("dev.emi.trinkets.api.SlotReference"),
                Class.forName("net.minecraft.client.render.entity.model.EntityModel"),
                Class.forName("net.minecraft.client.util.math.MatrixStack"),
                Class.forName("net.minecraft.client.render.VertexConsumerProvider"),
                int.class,
                Class.forName("net.minecraft.entity.LivingEntity"),
                float.class, float.class, float.class, float.class, float.class, float.class
            );
            
            renderElytraMethod.invoke(null, stack, slotReference, contextModel, matrices, vertexConsumers, light, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
            System.out.println("Successfully called default trinket elytra renderer");
            
        } catch (NoSuchMethodException e) {
            System.out.println("renderElytra method not found, trying alternative approach");
            // Try alternative approach - directly call ElytraFeatureRenderer
            try {
                // Get the client player's renderer
                net.minecraft.client.MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();
                if (client != null && client.getEntityRenderDispatcher() != null) {
                    Object playerRenderer = client.getEntityRenderDispatcher().getRenderer((net.minecraft.entity.Entity) entity);
                    
                    // This is complex - for now, let's just log that we tried
                    System.out.println("Could not render default trinket elytra - method not found");
                }
            } catch (Exception e2) {
                System.out.println("Alternative rendering approach failed: " + e2.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Failed to call default trinket renderer: " + e.getMessage());
        }
    }
}