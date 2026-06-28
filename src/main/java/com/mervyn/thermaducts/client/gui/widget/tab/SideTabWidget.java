package com.mervyn.thermaducts.client.gui.widget.tab;

import com.mervyn.thermaducts.ThermaDucts;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;

public abstract class SideTabWidget extends AbstractWidget {

  protected static final Identifier TAB_TEXTURE =
      Identifier.fromNamespaceAndPath(ThermaDucts.MODID, "textures/gui/tab_right.png");
  protected static final int TAB_TEXTURE_SIZE = 256;
  protected static final int MIN_SIZE = 22;
  protected static final int EXPAND_SPEED = 8;

  private final Identifier iconTexture;
  private final int maxTabWidth;
  private final int maxTabHeight;
  private final int headerColor;
  private final int subheaderColor;
  private final int textColor;
  private final int backgroundColor;

  private int currentWidth = MIN_SIZE;
  private int currentHeight = MIN_SIZE;

  protected SideTabWidget(
      int x,
      int y,
      int maxTabWidth,
      int maxTabHeight,
      Component title,
      Identifier iconTexture,
      int headerColor,
      int subheaderColor,
      int textColor,
      int backgroundColor) {
    super(x, y, MIN_SIZE, MIN_SIZE, title);
    this.iconTexture = iconTexture;
    this.maxTabWidth = maxTabWidth;
    this.maxTabHeight = maxTabHeight;
    this.headerColor = headerColor;
    this.subheaderColor = subheaderColor;
    this.textColor = textColor;
    this.backgroundColor = backgroundColor;
  }

  @Override
  protected void extractWidgetRenderState(
      GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
    boolean shouldOpen = isMouseOver(mouseX, mouseY);
    updateSize(shouldOpen);

    drawTabBackground(graphics, getX(), getY(), currentWidth, currentHeight, backgroundColor);
    drawTabIcon(graphics, getX() + 3, getY() + 3);

    if (isFullyOpen()) {
      renderTabContents(graphics, mouseX, mouseY, partialTick);
    }
  }

  protected void drawHeader(GuiGraphicsExtractor graphics, Component title) {
    graphics
        .textRenderer()
        .accept(
            getX() + 20, getY() + 6, title.copy().withStyle(style -> style.withColor(headerColor)));
  }

  protected abstract void renderTabContents(
      GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick);

  protected final void drawBodyText(
      GuiGraphicsExtractor graphics, Component text, int x, int y, boolean subheader) {
    graphics
        .textRenderer()
        .accept(
            getX() + x,
            getY() + y,
            text.copy()
                .withStyle(style -> style.withColor(subheader ? subheaderColor : textColor)));
  }

  protected final boolean isFullyOpen() {
    return currentWidth == maxTabWidth && currentHeight == maxTabHeight;
  }

  protected final int tabRight() {
    return getX() + currentWidth;
  }

  protected final int tabBottom() {
    return getY() + currentHeight;
  }

  protected final int headerColor() {
    return headerColor;
  }

  protected final int subheaderColor() {
    return subheaderColor;
  }

  protected final int textColor() {
    return textColor;
  }

  @Override
  public boolean isMouseOver(double mouseX, double mouseY) {
    return active
        && visible
        && mouseX >= getX()
        && mouseY >= getY()
        && mouseX < getX() + currentWidth
        && mouseY < getY() + currentHeight;
  }

  @Override
  public boolean mouseClicked(MouseButtonEvent event, boolean isClickInside) {
    if (!active || !visible || !isClickInside || !isValidClickButton(event.buttonInfo())) {
      return false;
    }
    playDownSound(net.minecraft.client.Minecraft.getInstance().getSoundManager());
    onClick(event, isClickInside);
    return true;
  }

  @Override
  protected MutableComponent createNarrationMessage() {
    return wrapDefaultNarrationMessage(getMessage());
  }

  @Override
  public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    defaultButtonNarrationText(narrationElementOutput);
  }

  private void updateSize(boolean shouldOpen) {
    currentWidth = stepToward(currentWidth, shouldOpen ? maxTabWidth : MIN_SIZE);
    currentHeight = stepToward(currentHeight, shouldOpen ? maxTabHeight : MIN_SIZE);
    setSize(currentWidth, currentHeight);
  }

  private static int stepToward(int current, int target) {
    if (current < target) {
      return Math.min(target, current + EXPAND_SPEED);
    }
    if (current > target) {
      return Math.max(target, current - EXPAND_SPEED);
    }
    return current;
  }

  private void drawTabIcon(GuiGraphicsExtractor graphics, int x, int y) {
    graphics.blit(RenderPipelines.GUI_TEXTURED, iconTexture, x, y, 0, 0, 16, 16, 16, 16);
  }

  private static void drawTabBackground(
      GuiGraphicsExtractor graphics, int x, int y, int width, int height, int color) {
    int packedColor = color | 0xFF000000;
    graphics.blit(
        RenderPipelines.GUI_TEXTURED,
        TAB_TEXTURE,
        x,
        y + 4,
        0,
        TAB_TEXTURE_SIZE - height + 4,
        4,
        height - 4,
        4,
        height - 4,
        TAB_TEXTURE_SIZE,
        TAB_TEXTURE_SIZE,
        packedColor);
    graphics.blit(
        RenderPipelines.GUI_TEXTURED,
        TAB_TEXTURE,
        x + 4,
        y,
        TAB_TEXTURE_SIZE - width + 4,
        0,
        width - 4,
        4,
        width - 4,
        4,
        TAB_TEXTURE_SIZE,
        TAB_TEXTURE_SIZE,
        packedColor);
    graphics.blit(
        RenderPipelines.GUI_TEXTURED,
        TAB_TEXTURE,
        x,
        y,
        0,
        0,
        4,
        4,
        4,
        4,
        TAB_TEXTURE_SIZE,
        TAB_TEXTURE_SIZE,
        packedColor);
    graphics.blit(
        RenderPipelines.GUI_TEXTURED,
        TAB_TEXTURE,
        x + 4,
        y + 4,
        TAB_TEXTURE_SIZE - width + 4,
        TAB_TEXTURE_SIZE - height + 4,
        width - 4,
        height - 4,
        width - 4,
        height - 4,
        TAB_TEXTURE_SIZE,
        TAB_TEXTURE_SIZE,
        packedColor);
  }
}
