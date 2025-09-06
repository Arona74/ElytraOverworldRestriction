package io.arona74.elytraoverworldrestriction.client;

import io.arona74.elytraoverworldrestriction.config.ElytraConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;

@Environment(EnvType.CLIENT)
public class ElytraRenderer {
    
    public static void initialize() {
        // We'll use a mixin instead of events since ALLOW_RENDER doesn't exist in 1.20.1
        System.out.println("ElytraOverworldRestriction: Elytra renderer initialized (using mixin approach)");
    }
    
    /**
     * Called from mixin to determine if elytra should be rendered
     */
    public static boolean shouldRenderElytra(PlayerEntity player) {
        // Check config setting
        ElytraConfig config = ElytraConfig.getInstance();
        if (!config.invisibleOnGround) {
            return true; // Feature disabled, render normally
        }
        
        // Check if player has elytra equipped (either chest or trinket slot)
        if (!hasElytraEquipped(player)) {
            return true; // No elytra equipped, render normally
        }
        
        // Check if player should have invisible elytra
        if (shouldHideElytra(player)) {
            return false; // Hide elytra rendering
        }
        
        return true; // Allow normal rendering
    }
    
    private static boolean hasElytraEquipped(PlayerEntity player) {
        // Check vanilla chest slot
        if (player.getEquippedStack(net.minecraft.entity.EquipmentSlot.CHEST).getItem() == Items.ELYTRA) {
            return true;
        }
        
        // Check trinkets if available (using reflection to avoid compile-time dependency)
        try {
            // Use reflection to avoid compile-time dependency on Trinkets
            Class<?> trinketsApiClass = Class.forName("dev.emi.trinkets.api.TrinketsApi");
            java.lang.reflect.Method getTrinketComponentMethod = trinketsApiClass.getMethod("getTrinketComponent", net.minecraft.entity.LivingEntity.class);
            
            Object trinketComponentOptional = getTrinketComponentMethod.invoke(null, player);
            
            // Handle java.util.Optional
            if (trinketComponentOptional instanceof java.util.Optional<?> javaOptional && javaOptional.isPresent()) {
                Object trinketComponent = javaOptional.get();
                
                // Get all equipped trinkets
                java.lang.reflect.Method getAllEquippedMethod = trinketComponent.getClass().getMethod("getAllEquipped");
                Object equippedList = getAllEquippedMethod.invoke(trinketComponent);
                
                // Check if any equipped item is an elytra
                if (equippedList instanceof Iterable<?> equipped) {
                    for (Object pair : equipped) {
                        try {
                            // Use the working obfuscated method to get ItemStack
                            java.lang.reflect.Method getItemStackMethod = pair.getClass().getMethod("method_15441");
                            Object itemStack = getItemStackMethod.invoke(pair);
                            
                            if (itemStack instanceof net.minecraft.item.ItemStack stack && stack.getItem() == Items.ELYTRA) {
                                return true;
                            }
                        } catch (Exception e) {
                            // Skip this pair if there's an error
                            continue;
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Trinkets not available or failed, ignore
        }
        
        return false;
    }
    
    private static boolean shouldHideElytra(PlayerEntity player) {
        // Hide elytra when player is on ground and not fall flying
        return player.isOnGround() && !player.isFallFlying();
    }
}