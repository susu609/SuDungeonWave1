package net.ss.dungeonwaves.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.ss.dungeonwaves.world.inventory.StarterGearGuiMenu;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class StarterGearGuiScreen extends AbstractContainerScreen<StarterGearGuiMenu> {

    private final static HashMap<String, Object> guistate = StarterGearGuiMenu.guistate;

    private final Level world;
    private final int x, y, z;
    private final Player entity;

    public StarterGearGuiScreen (StarterGearGuiMenu container, Inventory inventory, Component text) {
        super(container, inventory, text);
        this.world = container.world;
        this.x = container.x;
        this.y = container.y;
        this.z = container.z;
        this.entity = container.entity;
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    private static final ResourceLocation texture = new ResourceLocation("dungeon_waves:textures/screens/starter_gear_gui.png");

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        updateSlotState(); // ✅ Cập nhật GUI ngay khi item bị xóa
    }

    private void updateSlotState() {
        for (int i = 0; i < 9; i++) {
            if (this.menu.get().get(i).hasItem()) {
                if (i < 3) disableOtherSlots(0, 3);
                else if (i < 6) disableOtherSlots(3, 6);
                else disableOtherSlots(6, 9);
            }
        }
    }

    private void disableOtherSlots(int start, int end) {
        for (int i = start; i < end; i++) {
            if (!this.menu.get().get(i).hasItem()) {
                this.menu.get().get(i).set(ItemStack.EMPTY);
            }
        }
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
            this.minecraft.player.closeContainer();
            return true;
        }

        return super.keyPressed(key, b, c);
    }

    @Override
    protected void renderLabels (GuiGraphics guiGraphics, int mouseX, int mouseY) {
    }

    @Override
    public void init () {
        super.init();

    }

}