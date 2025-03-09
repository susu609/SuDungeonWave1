package net.ss.dungeonwaves.event;

import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkHooks;
import net.ss.dungeonwaves.entity.WanderingMerchantEntity;
import net.ss.dungeonwaves.network.SsModVariables;
import net.ss.dungeonwaves.util.GuiOpener;
import net.ss.dungeonwaves.util.Log;
import net.ss.dungeonwaves.world.inventory.StarterGearGuiMenu;

@Mod.EventBusSubscriber
public class PlayerEventHandler {

    @SubscribeEvent
    public static void onPlayerLoggedIn (PlayerEvent.PlayerLoggedInEvent event) {
        Entity entity = event.getEntity();
        Level world = entity.level();

        if (entity instanceof ServerPlayer) {
            SsModVariables.MapVariables data = SsModVariables.MapVariables.get(world);
            if (!data.hasChosenMode) {
                GuiOpener.openChosenModeGui((ServerPlayer) entity);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath (LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        Entity entity = event.getEntity();
        Level level = player.level();

        SsModVariables.MapVariables data = SsModVariables.MapVariables.get(level);
        if (entity instanceof ServerPlayer _player) {
            ServerLevel serverWorld = _player.serverLevel();
            ResourceKey<Level> dungeonDimension = ResourceKey.create(Registries.DIMENSION, new ResourceLocation("ss:dungeon_waves"));
            if (_player.level().dimension() == dungeonDimension) {
                _player.setRespawnPosition(dungeonDimension, new BlockPos(0, 3, 0), 0.0F, true, false);
            }
            if (level.dimension().location().toString().equals("ss:dungeon_waves")) {
                Log.w("💀 Player " + player.getName().getString() + " died! Restarting game...");
                WaveEventHandler.restartGame(serverWorld);
            }
        }

    }

}
