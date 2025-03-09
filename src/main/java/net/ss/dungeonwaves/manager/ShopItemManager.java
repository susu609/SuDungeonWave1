package net.ss.dungeonwaves.manager;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.ss.dungeonwaves.init.SsModItems;
import net.ss.dungeonwaves.item.ModWeaponItem;
import net.ss.dungeonwaves.util.DungeonRandom;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShopItemManager {

    public static List<ItemStack> getShopItems(int wave, long dungeonSeed) {
        List<ItemStack> shopItems = new ArrayList<>();
        Random random = DungeonRandom.getRNG("shop_items");

        if (wave >= 1) {
            shopItems.add(new ItemStack(SsModItems.WOODEN_SWORD.get(), 1));
        }
        if (wave >= 1 && random.nextDouble() > 0.4) {
            shopItems.add(new ItemStack(SsModItems.WOODEN_AXE.get(), 1));
        }
        if (wave >= 1) {
            shopItems.add(new ItemStack(SsModItems.WOODEN_PICKAXE.get(), 1));
        }
        if (wave >= 1) {
            shopItems.add(new ItemStack(SsModItems.WOODEN_SHOVEL.get(), 1));
        }
        if (wave >= 1 && random.nextDouble() > 0.3) {
            shopItems.add(new ItemStack(SsModItems.WOODEN_HOE.get(), 1));
        }
        return shopItems;
    }

    // ✅ Xác định giá của mỗi món đồ trong Shop
    public static int getItemCost(ItemStack item) {
        if (item.getItem() instanceof ModWeaponItem weapon) {
            return weapon.getEmeraldCost();
        }
        return 5; // Giá mặc định nếu không phải vũ khí
    }
    public static int getPlayerEmeraldCount(Player player) {
        int count = player.getInventory().countItem(SsModItems.SMALL_EMERALD.get());
        count += player.getInventory().countItem(SsModItems.EMERALD.get()) * 5;
        count += player.getInventory().countItem(SsModItems.LARGE_EMERALD.get()) * 10;
        return count;
    }
    public static boolean purchaseItem(Player player, ItemStack item) {
        int cost = getItemCost(item);
        int emeralds = getPlayerEmeraldCount(player);

        if (emeralds >= cost) {
            boolean success = removeEmeralds(player, cost);
            if (success) {
                player.getInventory().add(item);
                player.displayClientMessage(Component.literal("✅ Đã mua " + item.getHoverName() + " với giá " + cost + " Emeralds"), true);
                return true;
            } else {
                player.displayClientMessage(Component.literal("❌ Giao dịch thất bại!"), true);
            }
        } else {
            player.displayClientMessage(Component.literal("❌ Không đủ Emeralds!"), true);
        }
        return false;
    }
    private static boolean removeEmeralds(Player player, int amount) {
        int remaining = amount;
        Inventory inventory = player.getInventory();

        // Ưu tiên sử dụng Large Emerald trước
        while (remaining >= 10 && inventory.contains(SsModItems.LARGE_EMERALD.get().getDefaultInstance())) {
            if (removeSingleItem(inventory, SsModItems.LARGE_EMERALD.get())) {
                remaining -= 10;
            } else {
                break;
            }
        }

        // Sau đó dùng Normal Emerald
        while (remaining >= 5 && inventory.contains(SsModItems.EMERALD.get().getDefaultInstance())) {
            if (removeSingleItem(inventory, SsModItems.EMERALD.get())) {
                remaining -= 5;
            } else {
                break;
            }
        }

        // Cuối cùng dùng Small Emerald
        while (remaining > 0 && inventory.contains(SsModItems.SMALL_EMERALD.get().getDefaultInstance())) {
            if (removeSingleItem(inventory, SsModItems.SMALL_EMERALD.get())) {
                remaining -= 1;
            } else {
                break;
            }
        }

        // Nếu vẫn còn dư, nghĩa là xóa không đủ
        if (remaining > 0) {
            player.displayClientMessage(Component.literal("⚠ Lỗi: Không thể xóa đủ Emeralds! Đang hoàn trả giao dịch..."), true);
            player.getInventory().add(new ItemStack(SsModItems.SMALL_EMERALD.get(), amount)); // Hoàn trả
            return false;
        }
        return true;
    }

    // ✅ Xóa 1 viên Emerald (hỗ trợ Large, Normal, Small)
    private static boolean removeSingleItem(Inventory inventory, @NotNull Item item) {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.getItem() == item && !stack.isEmpty()) {
                stack.shrink(1);
                if (stack.isEmpty()) {
                    inventory.setItem(i, ItemStack.EMPTY);
                }
                return true;
            }
        }
        return false;
    }


}
