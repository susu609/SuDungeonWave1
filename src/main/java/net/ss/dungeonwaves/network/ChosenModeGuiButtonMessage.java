package net.ss.dungeonwaves.network;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkEvent;
import net.ss.dungeonwaves.DungeonWavesMod;
import net.ss.dungeonwaves.world.inventory.ChosenModeGuiMenu;

import java.util.HashMap;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ChosenModeGuiButtonMessage {

    private final int buttonID, x, y, z;

    public ChosenModeGuiButtonMessage (FriendlyByteBuf buffer) {
        this.buttonID = buffer.readInt();
        this.x = buffer.readInt();
        this.y = buffer.readInt();
        this.z = buffer.readInt();
    }

    public ChosenModeGuiButtonMessage (int buttonID, int x, int y, int z) {
        this.buttonID = buttonID;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static void buffer (ChosenModeGuiButtonMessage message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.buttonID);
        buffer.writeInt(message.x);
        buffer.writeInt(message.y);
        buffer.writeInt(message.z);
    }

    public static void handler (ChosenModeGuiButtonMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            Player entity = context.getSender();
            int buttonID = message.buttonID;
            int x = message.x;
            int y = message.y;
            int z = message.z;

            handleButtonAction(entity, buttonID, x, y, z);
        });
        context.setPacketHandled(true);
    }

    public static void handleButtonAction (Player entity, int buttonID, int x, int y, int z) {
        Level world = entity.level();
        HashMap<String, Object> guistate = ChosenModeGuiMenu.guistate;

        if (!world.hasChunkAt(new BlockPos(x, y, z)))
            return;

        // Lấy biến MapVariables để chỉnh sửa
        SsModVariables.MapVariables data = SsModVariables.MapVariables.get(world);

        if (buttonID == 0) { // Người chơi chọn "Yes"
            if (entity instanceof ServerPlayer _player && !_player.level().isClientSide()) {
                ResourceKey<Level> destinationType = ResourceKey.create(Registries.DIMENSION, new ResourceLocation("ss:dungeon_waves"));
                if (_player.level().dimension() == destinationType)
                    return;

                ServerLevel nextLevel = _player.server.getLevel(destinationType);
                if (nextLevel != null) {
                    _player.teleportTo(nextLevel, _player.getX(), _player.getY(), _player.getZ(), _player.getYRot(), _player.getXRot());
                    _player.connection.send(new ClientboundPlayerAbilitiesPacket(_player.getAbilities()));
                    _player.connection.send(new ClientboundLevelEventPacket(1032, BlockPos.ZERO, 0, false));
                }
            }
            entity.closeContainer();
        }

        if (buttonID == 1) { // Người chơi chọn "No"
            entity.closeContainer();
        }

        // Dù chọn Yes hay No, biến hasChosenMode sẽ thành true
        data.hasChosenMode = true;
        data.syncData(world);
    }

    @SubscribeEvent
    public static void registerMessage (FMLCommonSetupEvent event) {
        DungeonWavesMod.addNetworkMessage(ChosenModeGuiButtonMessage.class, ChosenModeGuiButtonMessage::buffer, ChosenModeGuiButtonMessage::new, ChosenModeGuiButtonMessage::handler);
    }

}