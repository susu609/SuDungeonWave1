package net.ss.dungeonwaves.event;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.ss.dungeonwaves.init.SsModEntities;
import net.ss.dungeonwaves.manager.DungeonWaveManager;
import net.ss.dungeonwaves.network.SsModVariables;
import net.ss.dungeonwaves.util.GuiOpener;
import net.ss.dungeonwaves.util.Log;

import java.util.Comparator;
import java.util.List;

@Mod.EventBusSubscriber
public class WaveEventHandler {

    @SubscribeEvent
    public static void onMonsterDie(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Monster)) return;

        LivingEntity entity = event.getEntity();
        Level world = entity.level();

        if (world.isClientSide) return;

        SsModVariables.MapVariables data = SsModVariables.MapVariables.get(world);

        // ✅ Chặn trừ quái nếu đang restart
        if (!data.inCombat) return;

        int newEnemyCount = Math.max(0, data.enemyCount - 1);
        data.enemyCount = newEnemyCount;
        data.syncData(world);

        if (newEnemyCount == 0) {
            DungeonWaveManager.endWave(world);
            System.out.println("✅ [DEBUG] Tất cả quái đã bị tiêu diệt! Kết thúc wave...");
        }
    }

    public static void restartGame (ServerLevel world) {
        // Đặt lại biến toàn cục
        SsModVariables.MapVariables data = SsModVariables.MapVariables.get(world);
        data.wave = 0;
        data.inCombat = false;
        data.summonPoints = 100.0;
        data.merchantGone = false;
        data.syncData(world);

        // Xóa tất cả quái vật trong thế giới
        {
            final Vec3 _center = new Vec3(0.5, 1, 0.5);
            List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(64 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center.x, _center.y, _center.z))
            ).toList();
            for (Entity entityiterator : _entfound) {
                if (!entityiterator.level().isClientSide())
                    entityiterator.discard();
            }
        }


        // Đặt lại tất cả người chơi trong dungeon
        for (ServerPlayer player : world.players()) {
            player.teleportTo(world, 0, 5, 0, 0.0F, 0.0F); // Đưa về điểm spawn
            player.getInventory().clearContent(); // Xóa đồ của người chơi
        }

        // Tái tạo thương nhân lang thang
        if (!data.merchantGone) {
            Entity entityToSpawn = SsModEntities.WANDERING_MERCHANT.get().spawn(world, BlockPos.containing(2, 3, 2), MobSpawnType.MOB_SUMMONED);
            if (entityToSpawn != null) {
                entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
            }
        }

        // Gọi menu chọn chế độ chơi
        for (ServerPlayer player : world.players()) {
            GuiOpener.openChosenModeGui(player);
        }

        Log.d("🔄 Dungeon has been fully reset!");
    }
}

