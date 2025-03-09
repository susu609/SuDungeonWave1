package net.ss.dungeonwaves.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class BlockEventHandler {

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        Entity entity = event.getEntity();
        if (entity == null || entity.level().isClientSide()) return;

        // Nếu người chơi ở chế độ Creative, cho phép phá block
        if (isCreativeMode(entity)) return;

        // ✅ Hủy sự kiện nếu không đủ điều kiện phá block
        EventCancel.cancel(event);
    }

    private static boolean isCreativeMode(Entity entity) {
        if (entity instanceof ServerPlayer serverPlayer) {
            return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
        } else if (entity instanceof Player player) {
            return player.isCreative();
        }
        return false;
    }
}
