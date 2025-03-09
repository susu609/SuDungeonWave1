package net.ss.dungeonwaves.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkEvent;
import net.ss.dungeonwaves.DungeonWavesMod;
import net.ss.dungeonwaves.manager.DungeonStructureManager;
import net.ss.dungeonwaves.world.inventory.MenuModGuiMenu;

import java.util.HashMap;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class MenuModGuiButtonMessage {

    private final int buttonID, x, y, z;
    private final String seedValue; // ✅ Thêm biến nhận Seed từ GUI

    public MenuModGuiButtonMessage(FriendlyByteBuf buffer) {
        this.buttonID = buffer.readInt();
        this.x = buffer.readInt();
        this.y = buffer.readInt();
        this.z = buffer.readInt();
        this.seedValue = buffer.readUtf(32767); // ✅ Đọc Seed từ GUI
    }

    public MenuModGuiButtonMessage(int buttonID, int x, int y, int z, String seedValue) {
        this.buttonID = buttonID;
        this.x = x;
        this.y = y;
        this.z = z;
        this.seedValue = seedValue;
    }

    public static void buffer(MenuModGuiButtonMessage message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.buttonID);
        buffer.writeInt(message.x);
        buffer.writeInt(message.y);
        buffer.writeInt(message.z);
        buffer.writeUtf(message.seedValue); // ✅ Gửi Seed đến server
    }

    public static void handler(MenuModGuiButtonMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            Player entity = context.getSender();
            int buttonID = message.buttonID;
            int x = message.x;
            int y = message.y;
            int z = message.z;
            String seedValue = message.seedValue; // ✅ Lấy Seed từ message

            assert entity != null;
            handleButtonAction(entity, buttonID, x, y, z, seedValue);
        });
        context.setPacketHandled(true);
    }

    public static void handleButtonAction(Player entity, int buttonID, int x, int y, int z, String seedValue) {
        Level world = entity.level();
        HashMap<String, Object> guistate = MenuModGuiMenu.guistate;

        if (!world.hasChunkAt(new BlockPos(x, y, z)))
            return;

        // ✅ Lấy biến MapVariables để chỉnh sửa
        SsModVariables.MapVariables data = SsModVariables.MapVariables.get(world);

        if (buttonID == 0) { // Người chơi chọn "Yes"
            if (entity instanceof ServerPlayer _player && !_player.level().isClientSide()) {
                ServerLevel serverWorld = _player.serverLevel();

                // ✅ Xử lý Seed: Nếu trống, tạo Seed ngẫu nhiên
                long seed;
                if (seedValue.trim().isEmpty()) {
                    seed = System.currentTimeMillis();
                } else {
                    seed = seedValue.hashCode();
                }

                System.out.println("🎲 Seed được sử dụng: " + seed);

                // ✅ Lưu Seed vào MapVariables
                data.dungeonSeed = seed;
                data.syncData(world);

                // ✅ Xác định vị trí đấu trường (căn giữa)
                BlockPos arenaCenter = new BlockPos(0, 0, 0);

                // ✅ Dịch chuyển người chơi đến khu vực đấu trường
                _player.teleportTo(arenaCenter.getX() + 0.5, arenaCenter.getY() + 2, arenaCenter.getZ() + 0.5);

                // ✅ Gọi hàm tạo đấu trường
                DungeonStructureManager.generateDungeon(serverWorld);
            }
        }
    }

    @SubscribeEvent
    public static void registerMessage(FMLCommonSetupEvent event) {
        DungeonWavesMod.addNetworkMessage(MenuModGuiButtonMessage.class, MenuModGuiButtonMessage::buffer, MenuModGuiButtonMessage::new, MenuModGuiButtonMessage::handler);
    }
}
