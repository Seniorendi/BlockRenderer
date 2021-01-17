package com.unascribed.blockrenderer.fabric.client.screens.item;

import com.unascribed.blockrenderer.fabric.client.render.RenderManager;
import com.unascribed.blockrenderer.fabric.client.render.item.ItemRenderer;
import com.unascribed.blockrenderer.fabric.client.screens.widgets.HoverableTinyButtonWidget;
import com.unascribed.blockrenderer.fabric.client.screens.widgets.ItemButtonMultiWidget;
import com.unascribed.blockrenderer.fabric.client.varia.Registries;
import com.unascribed.blockrenderer.fabric.client.varia.StringUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.function.Supplier;

/*
 * Note: Screen's get initialized in init
 */
@SuppressWarnings("NotNullFieldNotInitialized")
public class EnterNamespaceScreen extends BaseItemScreen {

    private static final int UN_GROUPED = 0; // Empty Map
    private static final int GROUP_BY_TAB = 1; // Banner Pattern
    private static final int GROUP_BY_TYPE = 2; // Block

    private static final TranslatableText TITLE = new TranslatableText("block_renderer.gui.namespace");

    private boolean emptySpec = false;

    private final String prefill;

    private TextFieldWidget text;
    private ItemButtonMultiWidget grouped;

    private final @Nullable ItemStack stack;

    public EnterNamespaceScreen(@Nullable Screen old, String prefill) {
        super(TITLE, old);
        this.prefill = prefill;
        this.stack = null;
    }

    public EnterNamespaceScreen(@Nullable Screen old, @Nullable ItemStack stack) {
        super(TITLE, old);
        this.prefill = StringUtils.getNamespace(stack);
        this.stack = stack;
    }

    @Override
    public void init() {
        assert client != null;
        client.keyboard.setRepeatEvents(true);

        boolean enabled = enabled();

        /* Note: This is the initializer, text can be null! */
        @SuppressWarnings("ConstantConditions")
        String oldText = (text == null ? prefill : text.getText());

        text = addButton(new TextFieldWidget(client.textRenderer, width / 2 - 100, height / 6 + 50, 200, 20, new TranslatableText("block_renderer.gui.namespace")), enabled);
        text.setChangedListener((value) -> emptySpec = value.trim().isEmpty());
        text.setText(oldText);
        addChild(text);
        setInitialFocus(text);

        if (stack != null) {
            addButton(new HoverableTinyButtonWidget(
                    this,
                    width - 32,
                    height - 32,
                    new TranslatableText("block_renderer.gui.switch.single"),
                    new TranslatableText("block_renderer.gui.switch.single.tooltip"),
                    button -> client.openScreen(new EnterSizeScreen(old, stack)))
            );
        }

        final Supplier<ItemStack> EMPTY_MAP = Registries.mapLazy(Registries.EMPTY_MAP, Item::getDefaultStack);
        final Supplier<ItemStack> PATTERN = Registries.mapLazy(Registries.PATTERN, Item::getDefaultStack);
        final Supplier<ItemStack> DISPENSER = Registries.mapLazy(Registries.DISPENSER, Item::getDefaultStack);

        grouped = addButton(new ItemButtonMultiWidget(
                this,
                itemRenderer,
                (state) -> {
                    switch (state) {
                        case UN_GROUPED:
                            return EMPTY_MAP.get();
                        case GROUP_BY_TAB:
                            return PATTERN.get();
                        case GROUP_BY_TYPE:
                            return DISPENSER.get();
                        default:
                            throw new RuntimeException("Unsupported Group Type");
                    }
                },
                12,
                height - 32,
                new TranslatableText("block_renderer.gui.group"),
                (state) -> {
                    if (state < 0 || state > 2) throw new RuntimeException("Unsupported Group Type");
                    return Collections.singletonList(new TranslatableText("block_renderer.gui.group.tooltip." + state));
                },
                button -> {
                    grouped.state += 1;
                    if (grouped.state > GROUP_BY_TYPE) grouped.state = UN_GROUPED;
                }
        ), enabled);

        super.init();

        renderButton.visible = !emptySpec;
    }

    @Override
    public void tick() {
        super.tick();
        text.tick();
        renderButton.visible = !emptySpec;
    }

    @Override
    public void onClose() {
        assert client != null;
        client.keyboard.setRepeatEvents(false);
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        assert client != null;

        super.render(stack, mouseX, mouseY, partialTicks);

        if (!emptySpec) return;

        drawCenteredText(stack, client.textRenderer, new TranslatableText("block_renderer.gui.emptySpec"), width / 2, height / 6 + 30, 0xFF5555);
    }

    @Override
    public void onRender(ButtonWidget button) {
        assert client != null;

        if (!renderButton.visible) return;

        client.openScreen(old);
        if (client.world == null) return;

        RenderManager.push(ItemRenderer.bulk(text.getText(), round(size), useId.isChecked(), addSize.isChecked(), grouped.state));
    }
}