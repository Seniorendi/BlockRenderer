package com.unascribed.blockrenderer.fabric.mixin;

import com.unascribed.blockrenderer.fabric.client.proxy.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.profiler.IProfiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Minecraft.class)
public class MixinMinecraftClient {

    @Redirect(method = "runGameLoop(Z)V", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/profiler/IProfiler;endStartSection(Ljava/lang/String;)V", args = "ldc=gameRenderer"), allow = 1)
    public void hookGameRenderer(IProfiler profiler, String arg) {
        profiler.endStartSection(arg);

        //--------------------------------------------------------------------------------------------------------------

        ClientProxy.onFrameStart();
    }

}
