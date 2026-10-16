package com.example.cookenchant.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.example.cookenchant.CookEnchantMod;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Takes over the furnace / blast furnace / smoker tick whenever the input slot
 * holds an enchantable item (and the output slot is empty). The item "cooks"
 * for 200 ticks and comes out with a random enchantment. Everything else
 * (ores, food, ...) is left to vanilla.
 */
@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin {

    private static final int COOK_TIME = 200;

    @Inject(method = "serverTick", at = @At("HEAD"), cancellable = true)
    private static void cookenchant$tick(ServerLevel level, BlockPos pos, BlockState state,
                                         AbstractFurnaceBlockEntity furnace, CallbackInfo ci) {
        ItemStack input = furnace.getItem(0);
        ItemStack fuel = furnace.getItem(1);

        if (!CookEnchantMod.isCookable(input) || !furnace.getItem(2).isEmpty()) {
            return; // vanilla behaviour
        }

        FurnaceAccessor acc = (FurnaceAccessor) (Object) furnace;
        boolean wasLit = acc.cookenchant$getLitTimeRemaining() > 0;

        if (wasLit) {
            acc.cookenchant$setLitTimeRemaining(acc.cookenchant$getLitTimeRemaining() - 1);
        }

        // Light a new piece of fuel if needed
        if (acc.cookenchant$getLitTimeRemaining() <= 0 && !fuel.isEmpty()) {
            int burn = level.fuelValues().burnDuration(fuel);
            if (burn > 0) {
                acc.cookenchant$setLitTimeRemaining(burn);
                acc.cookenchant$setLitTotalTime(burn);
                fuel.shrink(1);
            }
        }

        acc.cookenchant$setCookingTotalTime(COOK_TIME);

        if (acc.cookenchant$getLitTimeRemaining() > 0) {
            int progress = acc.cookenchant$getCookingTimer() + 1;
            if (progress >= COOK_TIME) {
                ItemStack result = input.copyWithCount(1);
                CookEnchantMod.applyRandomEnchant(level, result);
                furnace.setItem(2, result);
                input.shrink(1);
                progress = 0;
            }
            acc.cookenchant$setCookingTimer(progress);
        } else {
            // cool down like vanilla
            acc.cookenchant$setCookingTimer(Mth.clamp(acc.cookenchant$getCookingTimer() - 2, 0, COOK_TIME));
        }

        boolean isLit = acc.cookenchant$getLitTimeRemaining() > 0;
        if (wasLit != isLit) {
            BlockState newState = state.setValue(AbstractFurnaceBlock.LIT, isLit);
            level.setBlock(pos, newState, 3);
        }
        furnace.setChanged();
        ci.cancel();
    }
}
