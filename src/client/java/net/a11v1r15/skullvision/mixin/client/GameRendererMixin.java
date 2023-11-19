package net.a11v1r15.skullvision.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.enums.Instrument;
import net.a11v1r15.skullvision.SkullVision;
import net.minecraft.block.Block;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.SkullItem;
import net.minecraft.item.VerticallyAttachableBlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin implements AutoCloseable {

  @Shadow
  protected ResourceManager resourceManager;

  @Shadow
  protected MinecraftClient client;

  @Shadow
  protected abstract void loadPostProcessor(Identifier identifier);

  @Shadow
  protected abstract void onCameraEntitySet(Entity entity);


  @Inject(at = @At("RETURN"), method = "onCameraEntitySet(Lnet/minecraft/entity/Entity;)V")
  private void skullVision$testEntityWearingSkull(Entity entity, CallbackInfo info) {
    SkullVision.LOGGER.info("Function onCameraEntitySet called");
    if (entity instanceof LivingEntity) {
      ItemStack head = ((LivingEntity) entity).getEquippedStack(EquipmentSlot.HEAD);
      if (head.isIn(ItemTags.NOTEBLOCK_TOP_INSTRUMENTS)) {
        Item headItem = head.getItem();
        if (headItem instanceof VerticallyAttachableBlockItem) {
          Block headBlock = ((VerticallyAttachableBlockItem) headItem).getBlock();
          Instrument headInstrument = headBlock.getDefaultState().getInstrument();
          String soundName = "";
          if (headInstrument.hasCustomSound()) {
            soundName = SkullItem.getBlockEntityNbt(head).getString("note_block_sound");
          } else {
            soundName = headInstrument.getSound().value().getId().getPath();
          }
          String mobName = "";
          for (String element: soundName.split("\\.")) {
            if (!EntityType.get(element).isEmpty()) {
              mobName = element;
              break;
            }
          }
          SkullVision.LOGGER.info("soundName is \"" + soundName + "\" and mobName is \"" + mobName + "\"");
          Identifier shaderid = new Identifier("shaders/post/" + mobName + ".json");
          this.resourceManager.getResource(shaderid).ifPresentOrElse(r -> this.loadPostProcessor(shaderid),
            () -> { SkullVision.LOGGER.warn("No post shader found in " + shaderid); } );
        }
      }
    }
  }

  @Inject(at = @At(value = "RETURN"), method = "tick()V")
  private void skullVision$tickEquippingSkull(CallbackInfo info) {
    this.onCameraEntitySet(this.client.getCameraEntity());
  }
}