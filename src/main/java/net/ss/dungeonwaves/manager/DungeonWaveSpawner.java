package net.ss.dungeonwaves.manager;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.ss.dungeonwaves.DungeonWavesMod;
import net.ss.dungeonwaves.network.SsModVariables;
import net.ss.dungeonwaves.util.DungeonRandom;
import net.ss.dungeonwaves.util.Log;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class DungeonWaveSpawner {
    private static final Random random1 = new Random();
    private final Random random = DungeonRandom.getRNG("spawn");
    private static MonsterType lastPickedMonster = null; // Lưu quái lần trước

    public static void spawnWave (int waveNumber, Level world) {
        if (!(world instanceof ServerLevel serverWorld)) return;

        Log.d("👾 Spawn wave " + waveNumber);

        SsModVariables.MapVariables data = SsModVariables.MapVariables.get(world);
        int summonPoints = (int) data.summonPoints;
        long dungeonSeed = data.dungeonSeed; // ✅ Lấy seed từ MapVariables

        if (summonPoints < 10) {
            Log.w("⚠ Không đủ summonPoints để triệu hồi quái.");
            return;
        }

        int spawnedEnemies = 0;
        while (summonPoints >= 10) {
            Random waveRandom = new Random(dungeonSeed);
            MonsterType chosenType = pickMonster(waveRandom, waveNumber, summonPoints);
            // ✅ Gọi với seed
            if (chosenType == null) {
                Log.w("⚠ Không còn quái hợp lệ để triệu hồi.");
                break;
            }

            int summonCost = MonsterAttributes.get(chosenType).summonCost();
            summonPoints -= summonCost;
            spawnedEnemies++;

            String taskName = "spawn_enemy_" + spawnedEnemies;
            if (!DungeonWavesMod.hasServerWork(taskName)) {
                DungeonWavesMod.queueServerWork(taskName, 200, () -> spawnMonster(serverWorld, chosenType));
            } else {
                Log.w("⚠ Tác vụ '" + taskName + "' đã tồn tại, không thêm lại.");
            }
        }

        data.enemyCount += spawnedEnemies;
        data.summonPoints = summonPoints;
        data.syncData(world);

        Log.d("📊 Sử dụng " + (data.summonPoints - summonPoints) + " SP, triệu hồi " + spawnedEnemies + " quái.");
    }

    private static MonsterType pickMonster(Random waveRandom, int waveNumber, int summonPoints) {
        List<MonsterType> validMonsters = Arrays.stream(MonsterType.values())
                .filter(type -> {
                    MonsterAttributes attr = MonsterAttributes.get(type);
                    return waveNumber >= attr.minWave() && summonPoints >= attr.summonCost();
                })
                .toList();

        if (validMonsters.isEmpty()) return null; // Không có quái hợp lệ

        // Nếu có nhiều hơn 1 quái hợp lệ, loại bỏ quái vừa xuất hiện
        List<MonsterType> filteredMonsters = validMonsters.stream()
                .filter(type -> type != lastPickedMonster)
                .toList();

        MonsterType chosen;
        if (filteredMonsters.isEmpty()) {
            chosen = validMonsters.get(waveRandom.nextInt(validMonsters.size())); // Nếu chỉ còn 1 loại quái, chọn đại
        } else {
            chosen = filteredMonsters.get(waveRandom.nextInt(filteredMonsters.size())); // Chọn từ danh sách đã lọc
        }

        lastPickedMonster = chosen; // Cập nhật con quái vừa chọn
        return chosen;
    }


    private static void spawnMonster (ServerLevel world, MonsterType type) {
        if (world.players().isEmpty()) {
            Log.w("⚠ Không có ai trong dungeon, hủy spawn quái.");
            return;
        }

        int minX = -30, maxX = 30;
        int minZ = -30, maxZ = 30;

        boolean spawned = false;
        for (int i = 0; i < 10; i++) { // ✅ Thử tối đa 10 lần để tìm vị trí hợp lệ
            int x = random1.nextInt(maxX - minX) + minX;
            int z = random1.nextInt(maxZ - minZ) + minZ;
            int y = 1; // ✅ Đảm bảo spawn tại y = 1

            BlockPos pos = new BlockPos(x, y, z);
            if (world.getBlockState(pos.below()).isSolid()) {
                EntityType<?> entityType = EntityType.byString(type.getStringId()).orElse(null);
                if (entityType == null) {
                    Log.e("❌ Không thể tìm thấy loại quái: " + type.getStringId());
                    return;
                }

                Monster monster = (Monster) entityType.create(world);
                if (monster == null) {
                    Log.e("❌ Không thể tạo quái: " + type.getStringId());
                    return;
                }

                monster.setPos(x, y, z);
                world.addFreshEntity(monster);
                Log.d("⚔ " + type.name() + " xuất hiện tại (" + x + ", " + y + ", " + z + ")");
                spawned = true;
                break;
            }
        }

        if (!spawned) {
            Log.w("⚠ Không thể spawn quái, không tìm thấy vị trí hợp lệ.");
        }
    }


}
