package com.example.cookenchant;

import java.util.List;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public class CookEnchantMod implements ModInitializer {
    public static final String MOD_ID = "cookenchant";

    /**
     * Minecraft's enchantment codec only allows levels 1..255, so 255 is the
     * highest level that can actually be stored/saved on an item.
     * (1,000,000 would fail to save and could corrupt or delete the item.)
     */
    public static final int ENCHANT_LEVEL = 255;

    @Override
    public void onInitialize() {
        // All logic lives in AbstractFurnaceBlockEntityMixin.
    }

    /** Anything that can normally take enchantments: tools, weapons, armor, bows, rods, books... */
    public static boolean isCookable(ItemStack stack) {
        return !stack.isEmpty() && stack.has(DataComponents.ENCHANTABLE);
    }

    /** Adds one random enchantment (any enchant in the registry) at max level. */
    public static void applyRandomEnchant(ServerLevel level, ItemStack stack) {
        List<Holder.Reference<Enchantment>> all = level.registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .listElements()
                .toList();
        if (all.isEmpty()) {
            return;
        }
        Holder<Enchantment> pick = all.get(level.getRandom().nextInt(all.size()));
        stack.enchant(pick, ENCHANT_LEVEL);
    }
}
