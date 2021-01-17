package com.unascribed.blockrenderer.fabric.mixin.accessor;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ContainerScreen.class)
public interface IHoveredSlot {

    @Accessor(value = "hoveredSlot")
    @Nullable
    Slot block_renderer$accessor$hoveredSlot();

}
