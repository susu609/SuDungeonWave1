package net.ss.dungeonwaves.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.ss.dungeonwaves.DungeonWavesMod;
import net.ss.dungeonwaves.network.MenuModGuiButtonMessage;
import net.ss.dungeonwaves.world.inventory.MenuModGuiMenu;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class MenuModGuiScreen extends AbstractContainerScreen<MenuModGuiMenu> {

    private final static HashMap<String, Object> guistate = MenuModGuiMenu.guistate;

    private final Level world;
    private final int x, y, z;
    private final Player entity;

    EditBox Seed;

    Button button_play;

    public MenuModGuiScreen (MenuModGuiMenu container, Inventory inventory, Component text) {
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

        Seed.render(guiGraphics, mouseX, mouseY, partialTicks);

        this.renderTooltip(guiGraphics, mouseX, mouseY);
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

        if (Seed.isFocused())
            return Seed.keyPressed(key, b, c);

        return super.keyPressed(key, b, c);
    }

    @Override
    public void containerTick () {
        super.containerTick();
        Seed.tick();
    }

    @Override
    public void resize (Minecraft minecraft, int width, int height) {
        String SeedValue = Seed.getValue();
        super.resize(minecraft, width, height);
        Seed.setValue(SeedValue);
    }

    @Override
    protected void renderLabels (GuiGraphics guiGraphics, int mouseX, int mouseY) {
    }

    @Override
    public boolean isPauseScreen () {
        return true; // Dừng game khi mở GUI
    }

    @Override
    public void init () {
        super.init();

        // 🏷️ Tiêu đề GUI
        Component title = Component.translatable("gui.ss.menu_mod_gui.title");

        // 📜 Ô nhập Seed
        Seed = new EditBox(this.font, this.leftPos + 48, this.topPos + 80, 80, 18, Component.translatable("gui.ss.menu_mod_gui.Seed")) {
            @Override
            public void insertText (String text) {
                super.insertText(text);
                if (getValue().isEmpty())
                    setSuggestion(Component.translatable("gui.ss.menu_mod_gui.Seed").getString());
                else
                    setSuggestion(null);
            }

            @Override
            public void moveCursorTo (int pos) {
                super.moveCursorTo(pos);
                if (getValue().isEmpty())
                    setSuggestion(Component.translatable("gui.ss.menu_mod_gui.Seed").getString());
                else
                    setSuggestion(null);
            }
        };
        Seed.setSuggestion(Component.translatable("gui.ss.menu_mod_gui.Seed").getString());
        Seed.setMaxLength(32767);

        guistate.put("text:Seed", Seed);
        this.addWidget(this.Seed);

        // ✅ Nút Play để bắt đầu
        button_play = Button.builder(Component.translatable("gui.ss.menu_mod_gui.button_play"), e -> {
            DungeonWavesMod.PACKET_HANDLER.sendToServer(new MenuModGuiButtonMessage(0, x, y, z, Seed.getValue()));
            this.minecraft.player.closeContainer();
        }).bounds(this.leftPos + 58, this.topPos + 110, 60, 20).build();

        guistate.put("button:button_play", button_play);
        this.addRenderableWidget(button_play);
    }
}
