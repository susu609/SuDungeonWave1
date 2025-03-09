package net.ss.dungeonwaves.event;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.ss.dungeonwaves.DungeonWavesMod;
import net.ss.dungeonwaves.entity.WanderingMerchantEntity;
import net.ss.dungeonwaves.init.SsModEntities;
import net.ss.dungeonwaves.network.SsModVariables;
import net.ss.dungeonwaves.util.GuiOpener;
import net.ss.dungeonwaves.util.Log;

import java.util.List;

@Mod.EventBusSubscriber
public class MerchantEventHandler {

    @SubscribeEvent
    public static void onPlayerEnterDungeon(PlayerEvent.PlayerChangedDimensionEvent event) {
        Entity entity = event.getEntity();
        Level world = entity.level();

        if (world instanceof ServerLevel serverWorld && event.getTo().location().equals(new ResourceLocation("ss:dungeon_waves"))) {
            Log.d("🎭 Người chơi vào DungeonWavesDimension, triệu hồi thương nhân lang thang.");
            spawnWanderingMerchant(serverWorld, new BlockPos(2, 4, 2));
        }
    }

    private static void spawnWanderingMerchant(ServerLevel world, BlockPos pos) {
        WanderingMerchantEntity merchant = new WanderingMerchantEntity(SsModEntities.WANDERING_MERCHANT.get(), world);
        merchant.setPos(pos.getX(), pos.getY(), pos.getZ());

        // ✨ Hiệu ứng Ender Teleport khi xuất hiện
        world.sendParticles(ParticleTypes.PORTAL, pos.getX(), pos.getY() + 0.5, pos.getZ(), 30, 1.0, 1.0, 1.0, 0.2);
        world.playSound(null, pos, SoundEvents.ENDERMAN_TELEPORT, SoundSource.AMBIENT, 1.0F, 1.0F);

        world.addFreshEntity(merchant);
        Log.d("🛒 Thương nhân lang thang đã xuất hiện tại " + pos);
    }

    @SubscribeEvent
    public static void onMerchantDamaged(LivingDamageEvent event) {
        if (event.getEntity() instanceof WanderingMerchantEntity merchant) {
            if (merchant.getHealth() - event.getAmount() <= 1) {
                event.setCanceled(true); // Ngăn không cho thương nhân chết
                handleMerchantEscape(merchant); // Kích hoạt tẩu thoát
            }
        }
    }

    public static void handleMerchantEscape(WanderingMerchantEntity merchant) {
        if (!(merchant.level() instanceof ServerLevel world)) return;

        SsModVariables.MapVariables data = SsModVariables.MapVariables.get(world);
        data.merchantGone = true;
        data.syncData(world);

        world.sendParticles(ParticleTypes.SMOKE, merchant.getX(), merchant.getY() + 1, merchant.getZ(), 20, 0.5, 1, 0.5, 0.1);
        world.sendParticles(ParticleTypes.PORTAL, merchant.getX(), merchant.getY() + 1, merchant.getZ(), 40, 0.5, 1, 0.5, 0.2);
        world.playSound(null, merchant.getX(), merchant.getY(), merchant.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 1.0F, 1.0F);

        merchant.remove(Entity.RemovalReason.DISCARDED);

        Log.w("💨 Thương Nhân Lang Thang đã tẩu thoát và cửa hàng đóng vĩnh viễn!");

        // ✅ Quay lại sau 5 phút nếu chưa bị giết
        DungeonWavesMod.queueServerWork("merchant_return", 6000, () -> {
            if (!data.merchantGone) {
                spawnWanderingMerchant(world, new BlockPos(2, 4, 2));
                Log.d("🛒 Thương nhân lang thang đã quay lại sau 5 phút!");
            }
        });
    }
    @SubscribeEvent
    public static void onWaveEnd(WaveEvent.End event) {
        ServerLevel world = event.getWorld();
        SsModVariables.MapVariables data = SsModVariables.MapVariables.get(world);

        if (data.merchantGone) {
            Log.w("🚫 Merchant is gone, skipping teleport.");
            return;
        }

        List<ServerPlayer> players = world.players().stream()
                .filter(player -> player.level().dimension().location().equals(new ResourceLocation("ss:dungeon_waves")))
                .toList();

        if (players.isEmpty()) {
            Log.w("⚠ No players in dungeon, merchant stays.");
            return;
        }

        ServerPlayer targetPlayer = players.get(world.getRandom().nextInt(players.size()));
        BlockPos playerPos = targetPlayer.blockPosition();

        // Tìm và dịch chuyển thương nhân đến gần người chơi
        for (WanderingMerchantEntity merchant : world.getEntitiesOfClass(WanderingMerchantEntity.class, targetPlayer.getBoundingBox().inflate(64))) {
            teleportMerchant(merchant, playerPos);
            return;
        }

        Log.w("🚫 No merchant found, skipping teleport.");
    }

    private static void teleportMerchant(WanderingMerchantEntity merchant, BlockPos targetPos) {
        ServerLevel world = (ServerLevel) merchant.level();

        BlockPos safePos = findSafeSpawnPosition(world, targetPos, 3, 6);
        merchant.setPos(safePos.getX() + 0.5, safePos.getY(), safePos.getZ() + 0.5);

        // Hiệu ứng dịch chuyển
        world.sendParticles(ParticleTypes.PORTAL, merchant.getX(), merchant.getY() + 1, merchant.getZ(), 30, 0.5, 1, 0.5, 0.2);
        world.playSound(null, merchant.getX(), merchant.getY(), merchant.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 1.0F, 1.0F);

        Log.d("✨ Merchant teleported near " + targetPos);
    }

    private static BlockPos findSafeSpawnPosition(ServerLevel world, BlockPos center, int minRadius, int maxRadius) {
        for (int i = 0; i < 10; i++) { // Thử tìm tối đa 10 lần
            int dx = world.getRandom().nextInt(maxRadius * 2) - maxRadius;
            int dz = world.getRandom().nextInt(maxRadius * 2) - maxRadius;
            BlockPos pos = center.offset(dx, 0, dz);

            int y = world.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, pos).getY();
            BlockPos spawnPos = new BlockPos(pos.getX(), y, pos.getZ());

            if (world.getBlockState(spawnPos.below()).isSolid()) {
                return spawnPos; // Trả về vị trí hợp lệ
            }
        }
        return center; // Nếu không tìm được thì giữ nguyên vị trí cũ
    }

}
