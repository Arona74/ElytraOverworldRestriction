package io.arona74.elytraoverworldrestriction.mixin;

import io.arona74.elytraoverworldrestriction.config.ElytraConfig;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(value = ElytraFeatureRenderer.class, priority = 1001)
public class ElytraFeatureRendererMixin {
    
    // Try multiple possible obfuscated method names for broader compatibility
    @Inject(method = "method_4199", at = @At("HEAD"), cancellable = true, require = 0)
    private void onRenderA(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, Entity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch, CallbackInfo ci) {
        handleElytraRender(entity, ci);
    }
    
    @Inject(method = "render", at = @At("HEAD"), cancellable = true, require = 0)
    private void onRenderB(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, Entity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch, CallbackInfo ci) {
        handleElytraRender(entity, ci);
    }
    
    private void handleElytraRender(Entity entity, CallbackInfo ci) {
        // Only apply to players
        if (!(entity instanceof PlayerEntity)) {
            return; // Let non-player entities render normally
        }
        PlayerEntity player = (PlayerEntity) entity;
        
        // Check config setting
        ElytraConfig config = ElytraConfig.getInstance();
        if (!config.invisibleOnGround) {
            return; // Feature disabled, render normally
        }
        
        // Check if player has chest elytra (only handle chest slot in this mixin)
        boolean hasChestElytra = player.getEquippedStack(net.minecraft.entity.EquipmentSlot.CHEST).getItem() == Items.ELYTRA;
        if (!hasChestElytra) {
            return; // No chest elytra, let it render
        }
        
        // Check if player should have invisible elytra
        // Hide when: on ground OR (in air but not fall flying)
        if (!player.isFallFlying()) {
            ci.cancel(); // Cancel the rendering
        }
    }
    
    private boolean hasAnyElytraEquipped(PlayerEntity player) {
        // Check vanilla chest slot first
        if (player.getEquippedStack(net.minecraft.entity.EquipmentSlot.CHEST).getItem() == Items.ELYTRA) {
            return true;
        }
        
        // Check trinkets - enhanced version
        return hasElytraInTrinkets(player);
    }
    
    private boolean hasElytraInTrinkets(PlayerEntity player) {
        try {
            Class<?> trinketsApiClass = Class.forName("dev.emi.trinkets.api.TrinketsApi");
            java.lang.reflect.Method getTrinketComponentMethod = trinketsApiClass.getMethod("getTrinketComponent", net.minecraft.entity.LivingEntity.class);
            
            Object trinketComponentOptional = getTrinketComponentMethod.invoke(null, player);
            
            if (trinketComponentOptional instanceof java.util.Optional<?>) {
                java.util.Optional<?> optional = (java.util.Optional<?>) trinketComponentOptional;
                if (optional.isPresent()) {
                    Object trinketComponent = optional.get();
                    
                    // Try multiple method names for getAllEquipped
                    java.lang.reflect.Method getAllEquippedMethod = null;
                    try {
                        getAllEquippedMethod = trinketComponent.getClass().getMethod("getAllEquipped");
                    } catch (NoSuchMethodException e) {
                        try {
                            getAllEquippedMethod = trinketComponent.getClass().getMethod("getEquipped");
                        } catch (NoSuchMethodException e2) {
                            return false;
                        }
                    }
                    
                    Object equippedList = getAllEquippedMethod.invoke(trinketComponent);
                    
                    if (equippedList instanceof Iterable<?>) {
                        for (Object pair : (Iterable<?>) equippedList) {
                            if (pair == null) continue;
                            
                            try {
                                // Try multiple method names for getting ItemStack
                                net.minecraft.item.ItemStack stack = null;
                                
                                // Try obfuscated method first
                                try {
                                    java.lang.reflect.Method getStackMethod = pair.getClass().getMethod("method_15441");
                                    Object stackObj = getStackMethod.invoke(pair);
                                    if (stackObj instanceof net.minecraft.item.ItemStack) {
                                        stack = (net.minecraft.item.ItemStack) stackObj;
                                    }
                                } catch (Exception e) {
                                    // Try other possible method names
                                    try {
                                        java.lang.reflect.Method getStackMethod = pair.getClass().getMethod("getStack");
                                        Object stackObj = getStackMethod.invoke(pair);
                                        if (stackObj instanceof net.minecraft.item.ItemStack) {
                                            stack = (net.minecraft.item.ItemStack) stackObj;
                                        }
                                    } catch (Exception e2) {
                                        // Try getItem
                                        try {
                                            java.lang.reflect.Method getStackMethod = pair.getClass().getMethod("getItem");
                                            Object stackObj = getStackMethod.invoke(pair);
                                            if (stackObj instanceof net.minecraft.item.ItemStack) {
                                                stack = (net.minecraft.item.ItemStack) stackObj;
                                            }
                                        } catch (Exception e3) {
                                            continue;
                                        }
                                    }
                                }
                                
                                if (stack != null && stack.getItem() == Items.ELYTRA) {
                                    return true;
                                }
                            } catch (Exception e) {
                                continue;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Trinkets not available or method changed
        }
        
        return false;
    }
}