package net.ss.dungeonwaves.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.netty.buffer.Unpooled;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkHooks;
import net.ss.dungeonwaves.event.PlayerEventHandler;
import net.ss.dungeonwaves.event.WaveEventHandler;
import net.ss.dungeonwaves.manager.DungeonWaveManager;
import net.ss.dungeonwaves.manager.DungeonWaveSpawner;
import net.ss.dungeonwaves.manager.MonsterType;
import net.ss.dungeonwaves.network.SsModVariables;
import net.ss.dungeonwaves.world.inventory.StarterGearGuiMenu;

@Mod.EventBusSubscriber
public class WaveCommand {

    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("wave")
                .requires(source -> source.hasPermission(2)) // Admin level
                .then(Commands.literal("start")
                        .executes(context -> startWave(context.getSource())))
                .then(Commands.literal("stop")
                        .executes(context -> stopWave(context.getSource())))
                .then(Commands.literal("set")
                        .then(Commands.argument("wave", IntegerArgumentType.integer(1))
                                .executes(context -> setWave(context.getSource(), IntegerArgumentType.getInteger(context, "wave")))))
                .then(Commands.literal("spawn")
                        .then(Commands.argument("monster", StringArgumentType.string())
                                .executes(context -> spawnMonster(context.getSource(), StringArgumentType.getString(context, "monster")))))
                .then(Commands.literal("restart")
                        .executes(context -> restartWave(context.getSource())))

        );
    }

    private static int startWave(CommandSourceStack source) {
        ServerLevel world = source.getLevel();
        System.out.println("🔥 [DEBUG] Lệnh startWave được gọi.");
        DungeonWaveManager.startNextWave(world);
        source.sendSuccess(() -> Component.literal("🔥 Bắt đầu wave mới!"), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int stopWave(CommandSourceStack source) {
        ServerLevel world = source.getLevel();
        System.out.println("💀 [DEBUG] Lệnh stopWave được gọi.");
        DungeonWaveManager.endWave(world);
        source.sendSuccess(() -> Component.literal("💀 Đã kết thúc wave hiện tại!"), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int setWave(CommandSourceStack source, int waveNumber) {
        ServerLevel world = source.getLevel();
        System.out.println("🔢 [DEBUG] Lệnh setWave được gọi. Đặt wave thành: " + waveNumber);

        SsModVariables.MapVariables data = SsModVariables.MapVariables.get(world);
        data.wave = waveNumber;
        data.syncData(world);

        source.sendSuccess(() -> Component.literal("🔢 Đã đặt wave thành " + waveNumber), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int spawnMonster(CommandSourceStack source, String monsterType) {
        ServerLevel world = source.getLevel();
        try {
            MonsterType type = MonsterType.valueOf(monsterType.toUpperCase());
            SsModVariables.MapVariables data = SsModVariables.MapVariables.get(world);
            DungeonWaveSpawner.spawnWave(data.wave, world);
            source.sendSuccess(() -> Component.literal("⚔ Đã spawn quái: " + monsterType), true);
            return Command.SINGLE_SUCCESS;
        } catch (IllegalArgumentException e) {
            source.sendFailure(Component.literal("❌ Quái vật không hợp lệ! Dùng: ZOMBIE, SKELETON, CREEPER, SPIDER, RANDOM"));
            return 0;
        }
    }
    private static int restartWave(CommandSourceStack source) {
        System.out.println("🔄 [DEBUG] Restarting Dungeon...");
        if (source.getEntity() instanceof ServerPlayer serverPlayer) {
            ServerLevel world = (ServerLevel) serverPlayer.level();
            WaveEventHandler.restartGame(world);
        }


        source.sendSuccess(() -> Component.literal("🔄 Dungeon has been restarted!"), true);
        return Command.SINGLE_SUCCESS;
    }


}
