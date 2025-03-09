package net.ss.dungeonwaves.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkEvent;
import net.ss.dungeonwaves.DungeonWavesMod;
import net.ss.dungeonwaves.util.Log;
import net.ss.dungeonwaves.world.inventory.StarterGearGuiMenu;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class StarterGearButtonMessage {
    private final int buttonID;

    public StarterGearButtonMessage (FriendlyByteBuf buffer) {
        this.buttonID = buffer.readInt();
    }

    public StarterGearButtonMessage (int buttonID) {
        this.buttonID = buttonID;
    }

    public static void buffer (StarterGearButtonMessage message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.buttonID);
    }

    public static void handler (StarterGearButtonMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            Player player = context.getSender();
            if (player instanceof ServerPlayer serverPlayer) {
                handleButtonAction(serverPlayer, message.buttonID);
            }
        });
        context.setPacketHandled(true);
    }

    private static void handleButtonAction (ServerPlayer player, int buttonID) {
        Level world = player.level();
        if (player.containerMenu instanceof StarterGearGuiMenu menu) {
            ItemStack selectedItem = menu.getContainer().getItem(buttonID);

            if (!selectedItem.isEmpty()) {
                int groupIndex = buttonID / 3; // Xác định nhóm dựa trên vị trí (0: vũ khí, 1: thuốc, 2: relics)

                SsModVariables.MapVariables data = SsModVariables.MapVariables.get(world);
                if (data.selectedGroups[groupIndex]) {
                    Log.w("⚠ Bạn đã chọn vật phẩm trong nhóm này rồi!");
                    return;
                }

                player.getInventory().add(selectedItem.copy());
                menu.getContainer().setItem(buttonID, ItemStack.EMPTY);
                player.containerMenu.broadcastChanges();
                data.selectedGroups[groupIndex] = true;
                data.syncData(world); // ✅ Đồng bộ dữ liệu để tránh lỗi
            }
        }
    }

    @SubscribeEvent
    public static void registerMessage (FMLCommonSetupEvent event) {
        DungeonWavesMod.addNetworkMessage(StarterGearButtonMessage.class, StarterGearButtonMessage::buffer, StarterGearButtonMessage::new, StarterGearButtonMessage::handler);
    }
}
