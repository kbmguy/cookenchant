package com.example.cookenchant.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

@Mixin(AbstractFurnaceBlockEntity.class)
public interface FurnaceAccessor {
    @Accessor("litTimeRemaining")
    int cookenchant$getLitTimeRemaining();

    @Accessor("litTimeRemaining")
    void cookenchant$setLitTimeRemaining(int value);

    @Accessor("litTotalTime")
    void cookenchant$setLitTotalTime(int value);

    @Accessor("cookingTimer")
    int cookenchant$getCookingTimer();

    @Accessor("cookingTimer")
    void cookenchant$setCookingTimer(int value);

    @Accessor("cookingTotalTime")
    void cookenchant$setCookingTotalTime(int value);
}
