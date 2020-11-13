package com.unascribed.blockrenderer.fabric.mixin;

import com.unascribed.blockrenderer.fabric.client.proxy.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Minecraft.class)
public class MixinMinecraftClient {

    @Redirect(method = "runTick(Z)V", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V", args = "ldc=gameRenderer"), allow = 1)
    public void hookGameRenderer(ProfilerFiller profiler, String arg) {
        profiler.popPush(arg);

        //--------------------------------------------------------------------------------------------------------------

        ClientProxy.onFrameStart();
    }

}
