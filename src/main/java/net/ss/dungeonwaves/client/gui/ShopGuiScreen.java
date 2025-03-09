package net.ss.dungeonwaves.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.ss.dungeonwaves.world.inventory.ShopGuiMenu;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class ShopGuiScreen extends AbstractContainerScreen<ShopGuiMenu> {

    private final static HashMap<String, Object> guistate = ShopGuiMenu.guistate;

    private final Level world;
    private final int x, y, z;
    private final Player entity;

    Button button_reroll;
    Button button_sell;

    public ShopGuiScreen(ShopGuiMenu container, Inventory inventory, Component text) {
        super(container, inventory, text);
        this.world = container.world;
        this.x = container.x;
        this.y = container.y;
        this.z = container.z;
        this.entity = container.entity;
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    private static final ResourceLocation texture = new ResourceLocation("dungeon_waves:textures/screens/shop_gui.png");

    @Override
    public void render (@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);

        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        this.renderTooltip(guiGraphics, mouseX, mouseY);

    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int gx, int gy) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        guiGraphics.blit(texture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);

        guiGraphics.blit(new ResourceLocation("ss:textures/screens/shop_gui.png"), this.leftPos + 1, this.topPos + 0, 0, 0, 195, 166, 195, 166);

        RenderSystem.disableBlend();
    }

    @Override
    public boolean keyPressed(int key, int b, int c) {
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
    }

    @Override
    public void init() {
        super.init();

        button_reroll = Button.builder(Component.translatable("gui.ss.shop_gui.button_reroll"), e -> {
        }).bounds(this.leftPos + 172, this.topPos + 140, 56, 20).build();

        guistate.put("button:button_reroll", button_reroll);
        this.addRenderableWidget(button_reroll);

        button_sell = Button.builder(Component.translatable("gui.ss.shop_gui.button_sell"), e -> {
        }).bounds(this.leftPos + 172, this.topPos + 118, 46, 20).build();

        guistate.put("button:button_sell", button_sell);
        this.addRenderableWidget(button_sell);

    }

}