package neko.violetmist.skullvision.mixin;

import neko.violetmist.skullvision.util.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow
    @Final
    private ResourceManager resourceManager;

    @Shadow
    public abstract void loadEffect(ResourceLocation p_109129_);

    @Shadow
    @Final
    Minecraft minecraft;

    @Shadow
    public abstract void checkEntityPostEffect(@Nullable Entity p_109107_);

    @Inject(method = "checkEntityPostEffect", at = @At("RETURN"))
    private void skullVision$testEntityWearingSkull(Entity entity, CallbackInfo info) {
        String entityName = entity != null ? EntityType.getKey(entity.getType()).getPath() : "null";
        String skullName = ClientUtils.getSkullNameFrom(entity);

        ResourceLocation entityShaderId = new ResourceLocation("shaders/post/" + entityName + ".json");
        ResourceLocation skullShaderId = new ResourceLocation("shaders/post/" + skullName + ".json");
        ClientUtils.lastPostProcessors = new String[]{entityName, skullName};
        this.resourceManager.getResource(skullShaderId).ifPresentOrElse(
                r -> this.loadEffect(skullShaderId),
                () -> this.resourceManager.getResource(entityShaderId).ifPresent(s -> this.loadEffect(entityShaderId))
        );
    }

    @Inject(method = "tick()V", at = @At(value = "RETURN"))
    private void skullVision$tickEquippingSkull(CallbackInfo info) {
        if (this.minecraft.options.getCameraType().isFirstPerson()) {
            Entity entity = this.minecraft.getCameraEntity();
            String entityName = ((entity != null) ? EntityType.getKey(entity.getType()).getPath() : "null");
            String skullName = ClientUtils.getSkullNameFrom(entity);
            if (entity != null && (!entityName.equals(ClientUtils.lastPostProcessors[0]) || !skullName.equals(ClientUtils.lastPostProcessors[1])))
                this.checkEntityPostEffect(entity);
        }
    }
}