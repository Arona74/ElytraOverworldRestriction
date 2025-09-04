package io.arona74.elytraoverworldrestriction;

import java.lang.reflect.Method;

import io.arona74.elytraoverworldrestriction.config.ElytraConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Items;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;

public class ElytraOverworldRestriction implements ModInitializer {
    public static final String MOD_ID = "elytraoverworldrestriction";
    private static final boolean TRINKETS_LOADED = FabricLoader.getInstance().isModLoaded("trinkets");

    @Override
    public void onInitialize() {
        // Initialize and load config (this will create the file if it doesn't exist)
        ElytraConfig config = ElytraConfig.getInstance();
        System.out.println("ElytraOverworldRestriction: Config loaded - Nether: " + config.enableInNether + ", Realistic: " + config.realisticGliding);
        
        // Block firework usage while flying in restricted dimensions
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (!world.isClient && player instanceof ServerPlayerEntity serverPlayer) {
                String dimensionName = serverPlayer.getWorld().getRegistryKey().getValue().getPath();
                ElytraConfig currentConfig = ElytraConfig.getInstance();
                
                // Check if in restricted dimension and trying to use firework while elytra flying
                // Always block fireworks in restricted dimensions regardless of gliding mode
                if (currentConfig.shouldRestrictInDimension(dimensionName) &&
                    serverPlayer.isFallFlying() &&
                    serverPlayer.getStackInHand(hand).getItem() instanceof FireworkRocketItem &&
                    hasElytraEquipped(serverPlayer)) {
                    
                    // Prevent firework usage - return the same stack without using it
                    return TypedActionResult.fail(serverPlayer.getStackInHand(hand));
                }
            }
            return TypedActionResult.pass(player.getStackInHand(hand));
        });

        // Register server tick event to monitor elytra usage
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                handleElytraRestriction(player);
            }
        });
    }

    /**
     * Check if player has elytra equipped in either chest slot or trinket slot
     */
    private boolean hasElytraEquipped(ServerPlayerEntity player) {
        // Check vanilla chest slot first
        if (player.getEquippedStack(net.minecraft.entity.EquipmentSlot.CHEST).getItem() == Items.ELYTRA) {
            return true;
        }
        
        // Check trinkets if available
        if (TRINKETS_LOADED) {
            return hasElytraInTrinkets(player);
        }
        
        return false;
    }

    /**
     * Check trinkets for elytra using reflection (only called if Trinkets mod is loaded)
     */
    private boolean hasElytraInTrinkets(ServerPlayerEntity player) {
        try {
            // Use reflection to avoid compile-time dependency on Trinkets
            Class<?> trinketsApiClass = Class.forName("dev.emi.trinkets.api.TrinketsApi");
            Method getTrinketComponentMethod = trinketsApiClass.getMethod("getTrinketComponent", net.minecraft.entity.LivingEntity.class);
            
            Object trinketComponentOptional = getTrinketComponentMethod.invoke(null, player);
            
            // Handle java.util.Optional
            if (trinketComponentOptional instanceof java.util.Optional<?> javaOptional && javaOptional.isPresent()) {
                Object trinketComponent = javaOptional.get();
                
                // Get all equipped trinkets
                Method getAllEquippedMethod = trinketComponent.getClass().getMethod("getAllEquipped");
                Object equippedList = getAllEquippedMethod.invoke(trinketComponent);
                
                // Check if any equipped item is an elytra
                if (equippedList instanceof Iterable<?> equipped) {
                    for (Object pair : equipped) {
                        try {
                            // Use the working obfuscated method to get ItemStack
                            Method getItemStackMethod = pair.getClass().getMethod("method_15441");
                            Object itemStack = getItemStackMethod.invoke(pair);
                            
                            if (itemStack instanceof ItemStack stack && stack.getItem() == Items.ELYTRA) {
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
            // Trinkets API call failed, fall back to chest slot only
            // Uncomment for debugging: System.err.println("ElytraOverworldRestriction: Failed to check trinkets: " + e.getMessage());
        }
        return false;
    }

    private void handleElytraRestriction(ServerPlayerEntity player) {
        String dimensionName = player.getWorld().getRegistryKey().getValue().getPath();
        ElytraConfig config = ElytraConfig.getInstance();
        
        // Only restrict in configured dimensions
        if (!config.shouldRestrictInDimension(dimensionName)) {
            return;
        }

        // Check if player has elytra equipped (chest slot or trinket slot)
        if (hasElytraEquipped(player)) {
            
            // Prevent takeoff from ground - stop elytra immediately if on ground
            if (player.isOnGround() && player.isFallFlying()) {
                player.stopFallFlying();
                return;
            }
            
            // Apply velocity restrictions based on realistic gliding setting
            if (player.isFallFlying()) {
                if (config.realisticGliding) {
                    Vec3d velocity = player.getVelocity();
                    boolean modified = false;
                    
                    // Strongly prevent upward momentum
                    if (velocity.y > 0.01) {
                        velocity = new Vec3d(velocity.x, Math.max(velocity.y * 0.1, -0.1), velocity.z);
                        modified = true;
                    }
                    
                    // Limit horizontal speed for realistic gliding
                    double horizontalSpeed = Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);
                    if (horizontalSpeed > 0.6) {
                        double factor = 0.6 / horizontalSpeed;
                        velocity = new Vec3d(velocity.x * factor, velocity.y, velocity.z * factor);
                        modified = true;
                    }
                    
                    // Apply velocity changes and force sync
                    if (modified) {
                        player.setVelocity(velocity);
                        player.velocityModified = true;
                        player.networkHandler.sendPacket(new net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket(player));
                    }
                }
                // In classic mode - no velocity restrictions applied (fireworks still blocked)
            }
        }
    }
}