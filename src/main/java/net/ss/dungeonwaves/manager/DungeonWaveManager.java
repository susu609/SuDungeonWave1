package net.ss.dungeonwaves.manager;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.ss.dungeonwaves.DungeonWavesMod;
import net.ss.dungeonwaves.event.WaveEvent;
import net.ss.dungeonwaves.network.SsModVariables;
import net.ss.dungeonwaves.util.DungeonRandom;
import net.ss.dungeonwaves.util.GuiOpener;
import net.ss.dungeonwaves.util.Log;
import net.ss.dungeonwaves.world.inventory.ShopGuiMenu;
import java.util.Random;

public class DungeonWaveManager {

    public static void startNextWave(Level world) {
        SsModVariables.MapVariables data = SsModVariables.MapVariables.get(world);

        // 🚨 Ngăn chặn bắt đầu wave nếu đang restart
        if (data.isRestarting) {
            Log.w("🚫 Restarting... Skipping wave start.");
            return;
        }

        Random random = DungeonRandom.getRNG("wave_" + data.wave);
        int newSummonPoints = (int) (data.summonPoints + (data.wave * 10) + 20 + random.nextInt(10));
        if (data.inCombat) {
            Log.w("⚠ Wave đang chạy, không thể bắt đầu wave mới.");
            return;
        }

        Log.d("🔥 Bắt đầu wave " + data.wave);
        data.inCombat = true;
        data.wave++;

        // ✅ Reset lại enemyCount về 0 khi bắt đầu wave mới
        data.enemyCount = 0;

        // ✅ Cấp lại summonPoints theo wave hiện tại
        int previousSummonPoints = (int) data.summonPoints;
        data.summonPoints = newSummonPoints;
        data.syncData(world);

        Log.d("🔄 SummonPoints được cấp lại: " + previousSummonPoints + " ➝ " + newSummonPoints);
        Log.d("🧟 Reset enemyCount về 0");

        DungeonWaveSpawner.spawnWave(data.wave, world);
        DungeonWaveEffects.playWaveStartEffect((ServerLevel) world, data.wave);
    }

    public static void endWave(Level world) {
        if (!(world instanceof ServerLevel serverWorld)) {
            Log.w("⚠ Cannot end wave because world is not a ServerLevel.");
            return;
        }

        SsModVariables.MapVariables data = SsModVariables.MapVariables.get(serverWorld);

        // 🚨 Ngăn chặn kết thúc wave nếu đang restart
        if (data.isRestarting) {
            Log.w("🚫 Restarting... Skipping wave end.");
            return;
        }

        MinecraftForge.EVENT_BUS.post(new WaveEvent.End(serverWorld, data.wave));

        if (!data.inCombat) {
            Log.w("⚠ No wave is currently running to stop.");
            return;
        }

        Log.d("💀 Wave " + data.wave + " has ended.");
        data.inCombat = false;

        // ✅ Tìm người chơi hợp lệ trong dungeon
        ServerPlayer player = serverWorld.getPlayers(p -> p.level() == serverWorld).stream().findFirst().orElse(null);
        if (player == null) {
            Log.w("⚠ No players found in Dungeon. Skipping rewards and shop.");
            return;
        }

        // ✅ Tăng summonPoints theo wave
        int previousSummonPoints = (int) data.summonPoints;
        int wave = data.wave;
        int newSummonPoints = previousSummonPoints + (wave * 5) + 10;
        data.summonPoints = newSummonPoints;
        data.syncData(serverWorld);

        Log.d("🔄 SummonPoints updated: " + previousSummonPoints + " ➝ " + newSummonPoints);

        // ✅ Gọi hệ thống phần thưởng
        WaveRewardManager.giveWaveRewards(serverWorld, player);

        // ✅ Reset shop ngẫu nhiên mỗi wave
        ShopGuiMenu.refreshShopItems(serverWorld);

        // ✅ Mở Shop GUI nếu có thương nhân
        if (!data.merchantGone) {
            GuiOpener.openShopGui(player);
        }

        // ✅ Hiệu ứng kết thúc wave
        DungeonWaveEffects.playWaveEndEffect(serverWorld, wave);

        // ✅ Chờ 20 giây rồi bắt đầu wave tiếp theo
        Log.d("⏳ Next wave starts in 20 seconds...");
        DungeonWavesMod.queueServerWork("next_wave", 400, () -> {
            Log.d("🔥 Starting the next wave!");
            startNextWave(serverWorld);
        });
    }

}
