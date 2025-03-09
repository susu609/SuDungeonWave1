package net.ss.dungeonwaves.world.inventory;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.ss.dungeonwaves.init.SsModMenus;
import net.ss.dungeonwaves.network.SsModVariables;
import net.ss.dungeonwaves.util.ItemRandomizer;
import net.ss.dungeonwaves.util.Log;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class StarterGearGuiMenu extends AbstractContainerMenu {

    private final Container container;
    private final Set<Integer> selectedGroups = new HashSet<>(); // Lưu nhóm đã chọn

    public StarterGearGuiMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        super(SsModMenus.STARTER_GEAR_GUI.get(), id);
        this.container = new SimpleContainer(9);
        ItemRandomizer itemRandomizer = new ItemRandomizer();

        // Đặt vật phẩm ngẫu nhiên vào các ô
        container.setItem(0, itemRandomizer.getRandomTool());
        container.setItem(1, itemRandomizer.getRandomTool());
        container.setItem(2, itemRandomizer.getRandomTool());

        container.setItem(3, itemRandomizer.getRandomDrinkablePotion());
        container.setItem(4, itemRandomizer.getRandomThrowablePotion());
        container.setItem(5, itemRandomizer.getRandomDrinkablePotion());

        container.setItem(6, itemRandomizer.getRandomRelic());
        container.setItem(7, itemRandomizer.getRandomRelic());
        container.setItem(8, itemRandomizer.getRandomRelic());
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    public void selectItem(int index, Player player) {
        int groupIndex = index / 3; // Xác định nhóm dựa trên vị trí (0: vũ khí, 1: thuốc, 2: relics)

        if (selectedGroups.contains(groupIndex)) {
            player.displayClientMessage(Component.literal("⚠ Bạn đã chọn vật phẩm trong nhóm này rồi!"), true);
            return;
        }

        selectedGroups.add(groupIndex);
        ItemStack selectedItem = container.getItem(index);
        if (!selectedItem.isEmpty()) {
            player.getInventory().add(selectedItem.copy());
            container.setItem(index, ItemStack.EMPTY);
        }

        player.containerMenu.broadcastChanges();
    }


    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        selectItem(index, player);
        return ItemStack.EMPTY;
    }

    @Override
    public void removed(Player player) {
        if (!player.level().isClientSide && player instanceof ServerPlayer) {
            SimpleContainer container = (SimpleContainer) this.container;

            // Lấy vật phẩm chưa được chọn
            for (int group = 0; group < 3; group++) {
                if (!selectedGroups.contains(group)) {
                    int startIdx = group * 3;
                    for (int i = startIdx; i < startIdx + 3; i++) {
                        ItemStack item = container.getItem(i);
                        if (!item.isEmpty()) {
                            player.getInventory().add(item);
                            break;
                        }
                    }
                }
            }

            Log.d("📦 You have received your starter gear! The wave will start in 10 seconds.");
        }
        super.removed(player);
    }

    public Container getContainer() {
        return container;
    }
}
