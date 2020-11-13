package com.unascribed.blockrenderer.forge.client.init;

import com.unascribed.blockrenderer.Reference;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = Reference.ID, bus = Bus.MOD, value = Dist.CLIENT)
public interface Keybindings {

    KeyBinding render = new KeyBinding("key.block_renderer.render", GLFW.GLFW_KEY_GRAVE_ACCENT, "key.categories.block_renderer");

    @SubscribeEvent
    static void register(FMLClientSetupEvent event) {
        ClientRegistry.registerKeyBinding(render);
    }

}