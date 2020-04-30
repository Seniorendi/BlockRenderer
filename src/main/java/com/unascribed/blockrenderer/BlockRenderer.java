package com.unascribed.blockrenderer;

import com.unascribed.blockrenderer.init.Keybindings;
import com.unascribed.blockrenderer.render.ItemStackRenderer;
import com.unascribed.blockrenderer.render.request.IRequest;
import com.unascribed.blockrenderer.screens.EnterNamespaceScreen;
import com.unascribed.blockrenderer.screens.EnterSizeScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.unascribed.blockrenderer.utils.StringUtils.addMessage;

@Mod(Reference.MOD_ID)
public class BlockRenderer {

	public static final Logger LOGGER = LogManager.getLogger("BlockRenderer");

	private boolean down = false;

	public static IRequest pendingRequest;

	public BlockRenderer() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent(priority= EventPriority.HIGHEST)
	public void onFrameStart(TickEvent.RenderTickEvent e) {
		if (e.phase != TickEvent.Phase.START) return;

		/*
		 * Quick primer: OpenGL is double-buffered. This means, where we draw to is
		 * /not/ on the screen. As such, we are free to do whatever we like before
		 * Minecraft renders, as long as we put everything back the way it was.
		 */

		if (pendingRequest != null) {
			// We *must* call render code in pre-render. If we don't, it won't work right.
			pendingRequest.render();
			pendingRequest = null;
		}

		if (!Keybindings.render.isKeyDown()) {
			down = false;
			return;
		}

		if (down) return;
		down = true;

		Minecraft client = Minecraft.getInstance();
		Slot hovered = null;
		Screen currentScreen = client.currentScreen;
		boolean isContainerScreen = currentScreen instanceof ContainerScreen;

		if (isContainerScreen) hovered = ((ContainerScreen<?>) currentScreen).getSlotUnderMouse();

		if (Screen.hasControlDown()) {
			String namespace = "";
			if (hovered != null && hovered.getHasStack()) {
				ResourceLocation identifier = ForgeRegistries.ITEMS.getKey(hovered.getStack().getItem());
				if (identifier != null) namespace = identifier.getNamespace();
			}

			PlayerEntity player = client.player;
			if (!isContainerScreen && player != null && !player.getHeldItemMainhand().isEmpty()) {
				ResourceLocation identifier = ForgeRegistries.ITEMS.getKey(player.getHeldItemMainhand().getItem());
				if (identifier != null) namespace = identifier.getNamespace();
			}

			client.displayGuiScreen(new EnterNamespaceScreen(client.currentScreen, namespace.trim()));
			return;
		}

		if (!isContainerScreen) {
			PlayerEntity player = client.player;

			if (player != null && !player.getHeldItemMainhand().isEmpty()) {
				renderStack(player.getHeldItemMainhand());
				return;
			}
			addMessage(new TranslationTextComponent("msg.blockrenderer.notContainer"));
			return;
		}

		if (hovered == null) {
			addMessage(new TranslationTextComponent("msg.blockrenderer.slot.absent"));
			return;
		}

		ItemStack stack = hovered.getStack();

		if (stack.isEmpty()) {
			addMessage(new TranslationTextComponent("msg.blockrenderer.slot.empty"));
			return;
		}

		renderStack(stack);
	}

	private static void renderStack(ItemStack stack) {
		Minecraft client = Minecraft.getInstance();

		if (Screen.hasAltDown()) {
			client.displayGuiScreen(new EnterSizeScreen(client.currentScreen, stack));
			return;
		}

		int size = Screen.hasShiftDown() ? (int) Minecraft.getInstance().mainWindow.getGuiScaleFactor() * 16 : 512;

		ItemStackRenderer.renderItem(size, stack);
	}


}
