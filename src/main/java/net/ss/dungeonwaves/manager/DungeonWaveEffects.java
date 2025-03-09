package net.ss.dungeonwaves.manager;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.ss.dungeonwaves.util.Log;

public class DungeonWaveEffects {

    public static void playWaveStartEffect(ServerLevel world, int waveNumber) {
        Log.d("🔥 Hiệu ứng đặc biệt khi Wave " + waveNumber + " bắt đầu! 🔥");

        for (ServerPlayer player : world.players()) {
            player.displayClientMessage(Component.literal("§6🔥 Wave " + waveNumber + " đã bắt đầu! 🔥"), false);
            player.sendSystemMessage(Component.literal("§6⚔ Chuẩn bị chiến đấu!"));
            player.connection.send(new net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket(Component.literal("§cWave " + waveNumber + " bắt đầu!")));
            player.connection.send(new net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket(Component.literal("§7Tiêu diệt tất cả quái vật!")));
        }
    }

    public static void playWaveEndEffect(ServerLevel world, int waveNumber) {
        Log.d("💀 Hiệu ứng khi Wave " + waveNumber + " kết thúc! 💀");

        for (ServerPlayer player : world.players()) {
            player.displayClientMessage(Component.literal("§a💀 Wave " + waveNumber + " đã kết thúc! 💀"), false);
            player.sendSystemMessage(Component.literal("§a✅ Bạn đã sống sót qua wave này!"));
            player.connection.send(new net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket(Component.literal("§eWave " + waveNumber + " hoàn thành!")));
            player.connection.send(new net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket(Component.literal("§7Chuẩn bị cho wave tiếp theo...")));
        }
    }
}
