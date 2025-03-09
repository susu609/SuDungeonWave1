package net.ss.dungeonwaves.util;

import net.minecraft.world.item.ItemStack;
import net.ss.dungeonwaves.init.SsModItems;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import java.util.List;
import java.util.Random;

public class ItemRandomizer {
    private final Random random = DungeonRandom.getRNG("gear");

    // ✅ Danh sách tất cả potion có thể uống
    private static final List<Potion> DRINKABLE_POTIONS = List.of(
            Potions.NIGHT_VISION, Potions.INVISIBILITY, Potions.LEAPING, Potions.FIRE_RESISTANCE,
            Potions.SWIFTNESS, Potions.SLOWNESS, Potions.TURTLE_MASTER, Potions.WATER_BREATHING,
            Potions.HEALING, Potions.HARMING, Potions.POISON, Potions.REGENERATION,
            Potions.STRENGTH, Potions.WEAKNESS, Potions.LUCK, Potions.SLOW_FALLING
    );

    // ✅ Thay thế dụng cụ gỗ Vanilla bằng dụng cụ từ mod
    private static final List<ItemStack> TOOLS = List.of(
            new ItemStack(SsModItems.WOODEN_SWORD.get()),
            new ItemStack(SsModItems.WOODEN_AXE.get()),
            new ItemStack(SsModItems.WOODEN_PICKAXE.get()),
            new ItemStack(SsModItems.WOODEN_SHOVEL.get()),
            new ItemStack(SsModItems.WOODEN_HOE.get())
    );

    // ✅ Danh sách cổ vật (Placeholder, có thể mở rộng)
    private static final List<ItemStack> RELICS = List.of(
            new ItemStack(SsModItems.WOODEN_SWORD.get()),  // Tạm thời, có thể thay bằng item mod khác
            new ItemStack(SsModItems.WOODEN_AXE.get()),
            new ItemStack(SsModItems.WOODEN_PICKAXE.get())
    );

    public ItemStack getRandomDrinkablePotion() {
        return PotionUtils.setPotion(new ItemStack(net.minecraft.world.item.Items.POTION),
                DRINKABLE_POTIONS.get(random.nextInt(DRINKABLE_POTIONS.size())));
    }

    public ItemStack getRandomThrowablePotion() {
        return PotionUtils.setPotion(new ItemStack(net.minecraft.world.item.Items.SPLASH_POTION),
                DRINKABLE_POTIONS.get(random.nextInt(DRINKABLE_POTIONS.size())));
    }

    public ItemStack getRandomTool() {
        return TOOLS.get(random.nextInt(TOOLS.size()));
    }

    public ItemStack getRandomRelic() {
        return RELICS.get(random.nextInt(RELICS.size()));
    }
}
