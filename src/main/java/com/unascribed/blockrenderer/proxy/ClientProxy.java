package com.unascribed.blockrenderer.proxy;

import com.unascribed.blockrenderer.init.Keybindings;
import com.unascribed.blockrenderer.render.SingleRenderer;
import com.unascribed.blockrenderer.render.impl.ItemStackRenderer;
import com.unascribed.blockrenderer.render.request.IRequest;
import com.unascribed.blockrenderer.screens.EnterNamespaceScreen;
import com.unascribed.blockrenderer.screens.EnterSizeScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.lwjgl.glfw.GLFW;

import java.util.Deque;
import java.util.LinkedList;

import static com.unascribed.blockrenderer.utils.StringUtils.addMessage;

public class ClientProxy extends CommonProxy {

    private boolean down = false;
    private final Deque<IRequest> pendingRequests = new LinkedList<>();

    @Override
    public void init() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void render(IRequest request) {
        pendingRequests.add(request);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onFrameStart(TickEvent.RenderTickEvent e) {
        if (e.phase != TickEvent.Phase.START) return;

        if (pendingRequests.size() > 0) pendingRequests.poll().render();

        if (!isKeyDown()) {
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
            addMessage(new TranslationTextComponent("msg.block_renderer.notContainer"));
            return;
        }

        if (hovered == null) {
            addMessage(new TranslationTextComponent("msg.block_renderer.slot.absent"));
            return;
        }

        ItemStack stack = hovered.getStack();

        if (stack.isEmpty()) {
            addMessage(new TranslationTextComponent("msg.block_renderer.slot.empty"));
            return;
        }

        renderStack(stack);
    }

    private static void renderStack(ItemStack stack) {
        Minecraft client = Minecraft.getInstance();

        if (Screen.hasShiftDown()) {
            client.displayGuiScreen(new EnterSizeScreen(client.currentScreen, stack));
            return;
        }

        SingleRenderer.render(new ItemStackRenderer(), stack, 512, false, false);
    }

    private static boolean isKeyDown() {
        Minecraft client = Minecraft.getInstance();
        Screen currentScreen = client.currentScreen;

        /* Unbound key */
        if (Keybindings.render.isInvalid()) return false;

        /* Has the Keybinding been triggered? */
        if (Keybindings.render.isPressed()) return true;

        /* Not in Screen so we should be ok */
        if (currentScreen == null) return false;

        /* Non Containers seem to behave ok */
        boolean hasSlots = currentScreen instanceof ContainerScreen;
        if (!hasSlots) return false;

        /* TextFieldWidgets */
        if (currentScreen.getFocused() instanceof TextFieldWidget) return false;

        /* Recipe Books */
        if (currentScreen instanceof IRecipeShownListener) {
            RecipeBookGui recipeBook = ((IRecipeShownListener) currentScreen).func_194310_f();
            if (recipeBook.isVisible()) return false;
        }

        /* Actually Check to see if the key is down */
        InputMappings.Input key = Keybindings.render.getKey();

        if (key.getType() == InputMappings.Type.MOUSE) {
            return GLFW.glfwGetMouseButton(client.mainWindow.getHandle(), key.getKeyCode()) == GLFW.GLFW_PRESS;
        }

        return InputMappings.isKeyDown(client.mainWindow.getHandle(), key.getKeyCode());
    }

}