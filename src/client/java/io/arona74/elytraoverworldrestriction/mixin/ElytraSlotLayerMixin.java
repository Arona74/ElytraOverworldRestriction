package io.arona74.elytraoverworldrestriction.mixin;

import io.arona74.elytraoverworldrestriction.config.ElytraConfig;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Environment(EnvType.CLIENT)
@Mixin(targets = {
    "com.illusivesoulworks.elytraslot.client.ElytraSlotLayer",
    "com.illusivesoulworks.elytraslot.ElytraSlotLayer"
}, remap = false)
public class ElytraSlotLayerMixin {
    
    @Inject(method = "render", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void onElytraSlotRender(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, LivingEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch, CallbackInfo ci) {
        // Only apply to players
        if (!(entity instanceof PlayerEntity)) {
            return;
        }
        PlayerEntity player = (PlayerEntity) entity;
        
        // Check config setting
        ElytraConfig config = ElytraConfig.getInstance();
        if (!config.invisibleOnGround) {
            return;
        }
        
        // Check if player should have invisible elytra
        // Hide when: not actively fall flying (covers ground, jumping, falling, swimming, etc.)
        if (!player.isFallFlying()) {
            ci.cancel();
        }
    }
}