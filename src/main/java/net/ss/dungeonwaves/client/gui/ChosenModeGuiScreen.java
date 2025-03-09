package net.ss.dungeonwaves.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.ss.dungeonwaves.DungeonWavesMod;
import net.ss.dungeonwaves.network.ChosenModeGuiButtonMessage;
import net.ss.dungeonwaves.world.inventory.ChosenModeGuiMenu;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ChosenModeGuiScreen extends AbstractContainerScreen<ChosenModeGuiMenu> {

    private final static HashMap<String, Object> guistate = ChosenModeGuiMenu.guistate;
    private final Level world;
    private final int x, y, z;
    private final Player entity;

    Button button_yes;
    Button button_no;

    public ChosenModeGuiScreen (ChosenModeGuiMenu container, Inventory inventory, Component text) {
        super(container, inventory, text);
        this.world = container.world;
        this.x = container.x;
        this.y = container.y;
        this.z = container.z;
        this.entity = container.entity;
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    private static final ResourceLocation texture = new ResourceLocation("dungeon_waves:textures/screens/template_gui.png");

    @Override
    public void render (@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);

        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        this.renderTooltip(guiGraphics, mouseX, mouseY);

    }

    @Override
    public boolean isPauseScreen () {
        return true; // Dừng game khi mở GUI
    }

    @Override
    protected void renderBg (GuiGraphics guiGraphics, float partialTicks, int gx, int gy) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        guiGraphics.blit(texture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);

        RenderSystem.disableBlend();
    }

    @Override
    public boolean keyPressed (int key, int b, int c) {
        if (key == 256) {
            assert this.minecraft != null;
            assert this.minecraft.player != null;
            this.minecraft.player.closeContainer();
            return true;
        }

        return super.keyPressed(key, b, c);
    }

    @Override
    protected void renderLabels (@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        Component question = Component.translatable("gui.ss.chosen_mode_gui.label_do_you_want_to_play_the_mods_ga");
        List<FormattedCharSequence> wrappedText = this.font.split(question, this.imageWidth - 20); // Giới hạn chiều rộng chữ

        int startY = 20; // Vị trí dòng đầu tiên
        for (int i = 0; i < wrappedText.size(); i++) {
            int textWidth = this.font.width(wrappedText.get(i));
            guiGraphics.drawString(this.font, wrappedText.get(i).toString(), (this.imageWidth - textWidth) / 2, startY + (i * 12), -12829636, false);
        }
    }

    @Override
    public void init () {
        super.init();

        int buttonWidth = 60;
        int buttonHeight = 20;
        int buttonSpacing = 10;

        int centerX = this.leftPos + (this.imageWidth / 2);
        int buttonY = this.topPos + this.imageHeight - 30;

        // Nút Yes (Dịch chuyển)
        button_yes = Button.builder(Component.translatable("gui.ss.chosen_mode_gui.button_yes"), e -> {
            DungeonWavesMod.PACKET_HANDLER.sendToServer(new ChosenModeGuiButtonMessage(0, x, y, z));
            ChosenModeGuiButtonMessage.handleButtonAction(entity, 0, x, y, z);

        }).bounds(centerX - buttonWidth - (buttonSpacing / 2), buttonY, buttonWidth, buttonHeight).build();
        guistate.put("button:button_yes", button_yes);
        this.addRenderableWidget(button_yes);

        // Nút No (Đóng GUI)
        button_no = Button.builder(Component.translatable("gui.ss.chosen_mode_gui.button_no"), e -> {
            DungeonWavesMod.PACKET_HANDLER.sendToServer(new ChosenModeGuiButtonMessage(1, x, y, z));
            ChosenModeGuiButtonMessage.handleButtonAction(entity, 1, x, y, z);
        }).bounds(centerX + (buttonSpacing / 2), buttonY, buttonWidth, buttonHeight).build();
        guistate.put("button:button_no", button_no);
        this.addRenderableWidget(button_no);
    }

}
