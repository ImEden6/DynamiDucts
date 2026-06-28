package com.mervyn.dynamiducts.client.gui.widget.tab;

import com.mervyn.dynamiducts.DynamiDucts;
import com.mervyn.dynamiducts.core.attachment.RedstoneMode;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class RedstoneControlTabWidget extends SideTabWidget {

  private static final Identifier ICON_REDSTONE_TAB =
      Identifier.fromNamespaceAndPath(DynamiDucts.MODID, "textures/gui/icons/icon_redstone_on.png");
  private static final Identifier ICON_BUTTON =
      Identifier.fromNamespaceAndPath(DynamiDucts.MODID, "textures/gui/icons/icon_button.png");
  private static final Identifier ICON_BUTTON_HIGHLIGHT =
      Identifier.fromNamespaceAndPath(
          DynamiDucts.MODID, "textures/gui/icons/icon_button_highlight.png");
  private static final Identifier ICON_REDSTONE_OFF =
      Identifier.fromNamespaceAndPath(
          DynamiDucts.MODID, "textures/gui/icons/icon_redstone_off.png");
  private static final Identifier ICON_RS_TORCH_OFF =
      Identifier.fromNamespaceAndPath(
          DynamiDucts.MODID, "textures/gui/icons/icon_rs_torch_off.png");
  private static final Identifier ICON_RS_TORCH_ON =
      Identifier.fromNamespaceAndPath(DynamiDucts.MODID, "textures/gui/icons/icon_rs_torch_on.png");

  private final Supplier<RedstoneMode> modeSupplier;
  private final Consumer<RedstoneMode> modeSetter;

  public RedstoneControlTabWidget(
      int x, int y, Supplier<RedstoneMode> modeSupplier, Consumer<RedstoneMode> modeSetter) {
    super(
        x,
        y,
        112,
        92,
        Component.translatable("info.dynamiducts.redstoneControl"),
        ICON_REDSTONE_TAB,
        0xE1C92F,
        0xAAAFB8,
        0x000000,
        0xD0230A);
    this.modeSupplier = modeSupplier;
    this.modeSetter = modeSetter;
  }

  @Override
  protected void renderTabContents(
      GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
    RedstoneMode mode = modeSupplier.get();

    drawHeader(graphics, getMessage());

    graphics.blit(
        RenderPipelines.GUI_TEXTURED,
        TAB_TEXTURE,
        getX() + 24,
        getY() + 16,
        16.0F,
        20.0F,
        64,
        24,
        64,
        24,
        TAB_TEXTURE_SIZE,
        TAB_TEXTURE_SIZE,
        0xFF7D1405);

    drawModeButton(graphics, getX() + 28, getY() + 20, ICON_REDSTONE_OFF, mode.isDisabled());
    drawModeButton(graphics, getX() + 48, getY() + 20, ICON_RS_TORCH_OFF, mode == RedstoneMode.LOW);
    drawModeButton(graphics, getX() + 68, getY() + 20, ICON_RS_TORCH_ON, mode == RedstoneMode.HIGH);

    drawBodyText(
        graphics,
        Component.translatable("info.dynamiducts.controlStatus").append(":"),
        6,
        42,
        true);
    drawBodyText(
        graphics,
        mode.isDisabled()
            ? Component.translatable("info.dynamiducts.disabled")
            : Component.translatable("info.dynamiducts.enabled"),
        14,
        54,
        false);

    drawBodyText(
        graphics,
        Component.translatable("info.dynamiducts.signalRequired").append(":"),
        6,
        66,
        true);
    Component signalText =
        switch (mode) {
          case LOW -> Component.translatable("info.dynamiducts.low");
          case HIGH -> Component.translatable("info.dynamiducts.high");
          default -> Component.translatable("info.dynamiducts.ignored");
        };
    drawBodyText(graphics, signalText, 14, 78, false);

    Component tooltip = getButtonTooltip(mouseX, mouseY);
    if (tooltip != null) {
      graphics.setTooltipForNextFrame(Minecraft.getInstance().font, tooltip, mouseX, mouseY);
    }
  }

  @Override
  public boolean mouseClicked(MouseButtonEvent event, boolean isClickInside) {
    if (!active || !visible || !isMouseOver(event.x(), event.y())) {
      return false;
    }
    if (!isFullyOpen()) {
      return true;
    }
    double mouseX = event.x();
    double mouseY = event.y();
    if (insideButton(mouseX, mouseY, 28)) {
      modeSetter.accept(RedstoneMode.DISABLED);
      playClickSound(0.4F);
    } else if (insideButton(mouseX, mouseY, 48)) {
      modeSetter.accept(RedstoneMode.LOW);
      playClickSound(0.6F);
    } else if (insideButton(mouseX, mouseY, 68)) {
      modeSetter.accept(RedstoneMode.HIGH);
      playClickSound(0.8F);
    }
    return true;
  }

  private boolean insideButton(double mouseX, double mouseY, int x) {
    return mouseX >= getX() + x
        && mouseX < getX() + x + 16
        && mouseY >= getY() + 20
        && mouseY < getY() + 36;
  }

  private Component getButtonTooltip(int mouseX, int mouseY) {
    if (!isFullyOpen()) {
      return null;
    }
    if (insideButton(mouseX, mouseY, 28)) {
      return Component.translatable("info.dynamiducts.ignored");
    }
    if (insideButton(mouseX, mouseY, 48)) {
      return Component.translatable("info.dynamiducts.low");
    }
    if (insideButton(mouseX, mouseY, 68)) {
      return Component.translatable("info.dynamiducts.high");
    }
    return null;
  }

  private static void drawModeButton(
      GuiGraphicsExtractor graphics, int x, int y, Identifier icon, boolean active) {
    graphics.blit(
        RenderPipelines.GUI_TEXTURED,
        active ? ICON_BUTTON_HIGHLIGHT : ICON_BUTTON,
        x,
        y,
        0.0F,
        0.0F,
        16,
        16,
        16,
        16);
    graphics.blit(RenderPipelines.GUI_TEXTURED, icon, x, y, 0.0F, 0.0F, 16, 16, 16, 16);
  }

  private void playClickSound(float pitch) {
    Minecraft.getInstance()
        .getSoundManager()
        .play(
            net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(
                net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK, pitch));
  }
}
