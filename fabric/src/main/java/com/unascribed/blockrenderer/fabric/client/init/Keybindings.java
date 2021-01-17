package com.unascribed.blockrenderer.fabric.client.init;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.options.KeyBinding;
import org.lwjgl.glfw.GLFW;

public interface Keybindings {

    KeyBinding render = new KeyBinding("key.block_renderer.render", GLFW.GLFW_KEY_GRAVE_ACCENT, "key.categories.block_renderer");

    static void register() {
        KeyBindingHelper.registerKeyBinding(render);
    }

}