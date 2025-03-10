package net.ss.dungeonwaves.util;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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

    public ItemStack getRandomTool(Random rng) {
        return TOOLS.get(rng.nextInt(TOOLS.size()));
    }

    public ItemStack getRandomDrinkablePotion(Random rng) {
        return PotionUtils.setPotion(new ItemStack(Items.POTION),
                DRINKABLE_POTIONS.get(rng.nextInt(DRINKABLE_POTIONS.size())));
    }

    public ItemStack getRandomRelic(Random rng) {
        return RELICS.get(rng.nextInt(RELICS.size()));
    }

}
