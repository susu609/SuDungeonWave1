package net.ss.dungeonwaves.manager;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.ss.dungeonwaves.init.SsModItems;
import net.ss.dungeonwaves.network.SsModVariables;
import net.ss.dungeonwaves.util.DungeonRandom;
import net.ss.dungeonwaves.util.Log;

import java.util.Random;

public class WaveRewardManager {

    // ✅ Phát phần thưởng ngẫu nhiên theo seed & wave
    public static void dropRewards(ServerLevel world, Player player) {
        SsModVariables.MapVariables data = SsModVariables.MapVariables.get(world);
        int wave = data.wave;
        long seed = data.dungeonSeed;
        Random random = DungeonRandom.getRNG("wave_rewards"); // ✅ Dùng DungeonRandom thay vì new Random()

        int rewardCount = 2 + wave / 5 + random.nextInt(3); // ✅ Tăng số lượng theo wave

        for (int i = 0; i < rewardCount; i++) {
            ItemStack reward = getRandomReward(wave); // ✅ Truyền đúng số lượng tham số
            dropItemNearPlayer(world, player, reward);
        }
    }

    // ✅ Chọn phần thưởng dựa trên RNG
    private static ItemStack getRandomReward(int wave) {
        Random random = DungeonRandom.getRNG("wave_rewards");
        int roll = random.nextInt(100);

        if (roll < 40) return new ItemStack(SsModItems.SMALL_EMERALD.get(), 1 + wave / 4);
        if (roll < 60) return new ItemStack(SsModItems.EMERALD.get(), 1 + wave / 6);
        if (roll < 70) return new ItemStack(SsModItems.LARGE_EMERALD.get(), 1 + wave / 8);
        if (roll < 85) return new ItemStack(Items.GOLD_INGOT, 1 + wave / 6);
        if (roll < 95) return new ItemStack(Items.IRON_INGOT, 2 + wave / 3);
        return new ItemStack(Items.DIAMOND, 1 + wave / 10);
    }

    // ✅ Đảm bảo vật phẩm không rơi vào khoảng không hoặc bị kẹt
    private static void dropItemNearPlayer(ServerLevel world, Player player, ItemStack stack) {
        for (int attempts = 0; attempts < 5; attempts++) { // Thử tối đa 5 lần
            double offsetX = 0.5 + (world.random.nextDouble() - 0.5) * 2.0;
            double offsetZ = 0.5 + (world.random.nextDouble() - 0.5) * 2.0;
            BlockPos pos = player.blockPosition().offset((int) offsetX, (int) 1.5, (int) offsetZ);

            if (world.getBlockState(pos.below()).isSolid()) { // ✅ Chỉ spawn nếu bên dưới có block rắn
                ItemEntity itemEntity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack);
                world.addFreshEntity(itemEntity);
                return;
            }
        }
        Log.w("⚠ Failed to find a valid spawn location for rewards!");
    }

    // ✅ Gọi phương thức này khi người chơi cần nhận thưởng trực tiếp
    public static void giveWaveRewards(ServerLevel serverWorld, ServerPlayer player) {
        Log.d("🎁 Giving direct wave rewards to " + player.getScoreboardName());
        dropRewards(serverWorld, player);
    }
}
