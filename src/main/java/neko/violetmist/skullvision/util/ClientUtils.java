package neko.violetmist.skullvision.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PlayerHeadItem;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

public class ClientUtils {
    public static String[] lastPostProcessors = {"null", "null"};

    public static String getSkullNameFrom(Entity entity) {
        if (!(entity instanceof LivingEntity)) return "null";
        ItemStack skull = ((LivingEntity) entity).getItemBySlot(EquipmentSlot.HEAD);
        if (!skull.is(ItemTags.NOTE_BLOCK_TOP_INSTRUMENTS)) return "null";
        if (!(skull.getItem() instanceof BlockItem blockItem)) return "null";
        NoteBlockInstrument skullInstrument = blockItem.getBlock().defaultBlockState().instrument();
        String soundName = "";
        if (skullInstrument.hasCustomSound()) {
            CompoundTag skullNBT = PlayerHeadItem.getBlockEntityData(skull);
            if (skullNBT != null)
                soundName = skullNBT.getString("note_block_sound");
        } else {
            soundName = skullInstrument.getSoundEvent().value().getLocation().getPath();
        }
        for (String element : soundName.split("\\.")) {
            if (EntityType.byString(element).isPresent()) {
                return element;
            }
        }
        return "null";
    }
}
