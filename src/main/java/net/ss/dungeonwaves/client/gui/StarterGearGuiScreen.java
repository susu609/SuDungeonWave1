package net.ss.dungeonwaves.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.ss.dungeonwaves.DungeonWavesMod;
import net.ss.dungeonwaves.network.StarterGearButtonMessage;
import net.ss.dungeonwaves.world.inventory.StarterGearGuiMenu;
import org.jetbrains.annotations.NotNull;

public class StarterGearGuiScreen extends AbstractContainerScreen<StarterGearGuiMenu> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("dungeon_waves:textures/screens/template_gui.png");

    public StarterGearGuiScreen(StarterGearGuiMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
        RenderSystem.disableBlend();

        // ✅ Hiển thị hình ảnh vật phẩm có thể chọn
        for (int i = 0; i < 9; i++) {
            ItemStack stack = this.menu.getContainer().getItem(i);
            int x = this.leftPos + 30 + (i % 3) * 40;
            int y = this.topPos + 20 + (i / 3) * 30;

            if (!stack.isEmpty()) {
                guiGraphics.renderItem(stack, x, y);
            }
        }
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (int i = 0; i < 9; i++) {
            int x = this.leftPos + 30 + (i % 3) * 40;
            int y = this.topPos + 20 + (i / 3) * 30;

            if (mouseX >= x && mouseX <= x + 16 && mouseY >= y && mouseY <= y + 16) {
                DungeonWavesMod.PACKET_HANDLER.sendToServer(new StarterGearButtonMessage(i));
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
