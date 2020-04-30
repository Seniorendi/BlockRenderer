package com.unascribed.blockrenderer.screens;

import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.OptionSlider;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.SliderPercentageOption;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;

public abstract class BaseScreen extends Screen {

    private static final int MIN_SIZE = 16;
    private static final int MAX_SIZE = 2048;

    protected final Screen old;

    protected double size = 512;

    protected OptionSlider slider;

    public BaseScreen(ITextComponent title, @Nullable Screen old) {
        super(title);
        this.old = old;
    }

    @Override
    public void init() {
        assert minecraft != null;
        boolean enabled = minecraft.world != null;

        addButton(new Button(width/2-100, height/6+120, 98, 20, I18n.format("gui.cancel"), button -> minecraft.displayGuiScreen(old)));

        addButton(new Button(width/2+2, height/6+120, 98, 20, I18n.format("blockrenderer.gui.render"), this::onRender), enabled);

        size = MathHelper.clamp(size, MIN_SIZE, MAX_SIZE);

        SliderPercentageOption option = new SliderPercentageOption(I18n.format("blockrenderer.gui.renderSize"), MIN_SIZE, MAX_SIZE, 1, (settings) -> size, (settings, value) -> size = round(value), this::getSliderDisplay);
        slider = addButton(new OptionSlider(minecraft.gameSettings, width/2-100, height/6+80, 200, 20, option), enabled);
    }

    protected int round(double value) {
        assert minecraft != null;

        int val = (int)value;

        // There's a more efficient method in MathHelper, but it rounds up. We want the nearest.
        int nearestPowerOfTwo = (int)Math.pow(2, Math.ceil(Math.log(val)/Math.log(2)));

        if (nearestPowerOfTwo < MAX_SIZE && Math.abs(val-nearestPowerOfTwo) < 32) val = nearestPowerOfTwo;

        return MathHelper.clamp(val, MIN_SIZE, MAX_SIZE);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        assert minecraft != null;

        renderBackground();

        super.render(mouseX, mouseY, partialTicks);

        drawCenteredString(minecraft.fontRenderer, title.getFormattedText(), width/2, height/6, -1);

        if (minecraft.world != null) return;

        drawCenteredString(minecraft.fontRenderer, I18n.format("blockrenderer.gui.noWorld"), width/2, height/6+30, 0xFF5555);
    }

    abstract void onRender(Button button);

    public String getSliderDisplay(GameSettings settings, SliderPercentageOption option) {
        int px = round(size);
        return option.getDisplayString() + px + "x" + px;
    }

    protected <T extends Widget> T addButton(T button, boolean active) {
        addButton(button);

        button.active = active;
        button.visible = active;

        return button;
    }

}