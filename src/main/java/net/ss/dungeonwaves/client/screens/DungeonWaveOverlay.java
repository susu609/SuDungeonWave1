
package net.ss.dungeonwaves.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.Minecraft;
import net.ss.dungeonwaves.network.SsModVariables;

@Mod.EventBusSubscriber({Dist.CLIENT})
public class DungeonWaveOverlay {
    private static final ResourceLocation WAVE_ICON = new ResourceLocation("dungeon_waves:textures/screens/wave_icon.png");

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void eventHandler(RenderGuiEvent.Pre event) {
        int screenWidth = event.getWindow().getGuiScaledWidth();
        int screenHeight = event.getWindow().getGuiScaledHeight();
        Level world = null;
        double x = 0, y = 0, z = 0;
        Player entity = Minecraft.getInstance().player;

        if (entity != null) {
            world = entity.level();
            x = entity.getX();
            y = entity.getY();
            z = entity.getZ();
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        // Lấy dữ liệu wave từ MapVariables
        SsModVariables.MapVariables data = SsModVariables.MapVariables.get(mc.level);

        if (!data.inCombat) return; // Chỉ hiển thị khi đang trong combat

        // ✅ Vị trí góc trên bên phải
        int guiX = screenWidth - 80; // Dịch từ phải qua 80px
        int guiY = 10;               // Cách mép trên 10px

        GuiGraphics guiGraphics = event.getGuiGraphics();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        // Vẽ biểu tượng wave
        guiGraphics.blit(WAVE_ICON, guiX, guiY, 0, 0, 16, 16, 16, 16);

        // Hiển thị số wave
        guiGraphics.drawString(mc.font, Component.literal("Wave: " + data.wave), guiX + 20, guiY + 4, 0xFFFFFF);
    }

}
