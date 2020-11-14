package com.unascribed.blockrenderer.fabric;

import com.unascribed.blockrenderer.Reference;
import com.unascribed.blockrenderer.fabric.client.init.Keybindings;
import com.unascribed.blockrenderer.varia.logging.Log;
import com.unascribed.blockrenderer.varia.logging.Markers;
import net.fabricmc.api.ClientModInitializer;

public class BlockRenderer implements ClientModInitializer {

    public BlockRenderer() {
        Log.info(Markers.ROOT, "Running Version: " + Reference.VERSION);
    }

    @Override
    public void onInitializeClient() {
        Keybindings.register();
    }

}
