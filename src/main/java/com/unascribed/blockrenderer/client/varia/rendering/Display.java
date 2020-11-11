package com.unascribed.blockrenderer.client.varia.rendering;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.util.List;

public interface Display {

    Minecraft client = Minecraft.getInstance();
    FontRenderer font = client.fontRenderer;

    static void drawRect(MatrixStack stack, int x1, int y1, int x2, int y2, int color) {
        AbstractGui.fill(stack, x1, y1, x2, y2, color);
    }

    static void drawCenteredString(MatrixStack stack, ITextComponent component, int x, int y, int color) {
        drawCenteredString(stack, component.getString(), x, y, color);
    }

    static void drawCenteredString(MatrixStack stack, String str, int x, int y, int color) {
        font.drawStringWithShadow(stack, str, x - font.getStringWidth(str) / 2F, y, color);
    }

    static void drawHoveringText(MatrixStack stack, List<ITextComponent> tooltip, int x, int y, int width, int height) {
        GuiUtils.drawHoveringText(stack, tooltip, x, y, width, height, -1, font);
    }

    static void renderTooltip(Screen owner, MatrixStack stack, List<ITextComponent> tooltip, int x, int y) {
        GuiUtils.drawHoveringText(stack, tooltip, x, y, owner.width, owner.height, -1, font);
    }

    static void drawDirtBackground(int scaledWidth, int scaledHeight) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();

        client.getTextureManager().bindTexture(AbstractGui.BACKGROUND_LOCATION);

        GL.color(1.0F, 1.0F, 1.0F, 1.0F);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);

        // 0 h
        bufferbuilder.pos(0.0D, scaledHeight, 0.0D)
                .color(64, 64, 64, 255)
                .tex(0.0F, scaledHeight / 32.0F + 0.0F)
                .endVertex();
        // w h
        bufferbuilder.pos(scaledWidth, scaledHeight, 0.0D)
                .color(64, 64, 64, 255)
                .tex(scaledWidth / 32.0F, scaledHeight / 32.0F + 0.0F)
                .endVertex();
        // w 0
        bufferbuilder.pos(scaledWidth, 0.0D, 0.0D)
                .color(64, 64, 64, 255)
                .tex(scaledWidth / 32.0F, 0.0F)
                .endVertex();
        // 0 0
        bufferbuilder.pos(0.0D, 0.0D, 0.0D)
                .color(64, 64, 64, 255)
                .tex(0.0F, 0.0F)
                .endVertex();

        tessellator.draw();
    }

}