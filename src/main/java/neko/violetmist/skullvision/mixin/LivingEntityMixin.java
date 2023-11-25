package neko.violetmist.skullvision.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Inject(method = "onEquipItem", at = @At(value = "RETURN"))
    private void skullVision$checkEquippingSkull(EquipmentSlot slot, ItemStack oldStack, ItemStack newStack, CallbackInfo info) {
        Minecraft client = Minecraft.getInstance();
        if (!slot.equals(EquipmentSlot.HEAD)) return;
        if (!(this == client.getCameraEntity() && client.options.getCameraType().isFirstPerson())) return;
        Minecraft.getInstance().gameRenderer.checkEntityPostEffect(this);
    }
}