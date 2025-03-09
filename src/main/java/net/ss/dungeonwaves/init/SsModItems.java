package net.ss.dungeonwaves.init;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.ss.dungeonwaves.DungeonWavesMod;
import net.ss.dungeonwaves.item.ModEmeraldItem;
import net.ss.dungeonwaves.item.ModWeaponItem;

public class SsModItems {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, DungeonWavesMod.MODID);

    //ModWeaponItem
    public static final RegistryObject<Item> WOODEN_SWORD = REGISTRY.register("wooden_sword",
            () -> new ModWeaponItem(ModWeaponItem.WeaponType.SWORD, 3, -2.4f, new Item.Properties(), 10, 2.0)); // Kiếm chém quét

    public static final RegistryObject<Item> WOODEN_AXE = REGISTRY.register("wooden_axe",
            () -> new ModWeaponItem(ModWeaponItem.WeaponType.AXE, 5, -3.2f, new Item.Properties(), 15, 2.0)); // Rìu đa mục tiêu

    public static final RegistryObject<Item> WOODEN_PICKAXE = REGISTRY.register("wooden_pickaxe",
            () -> new ModWeaponItem(ModWeaponItem.WeaponType.PICKAXE, 6, -3.4f, new Item.Properties(), 12, 2.0)); // Cúp đơn mục tiêu

    public static final RegistryObject<Item> WOODEN_SHOVEL = REGISTRY.register("wooden_shovel",
            () -> new ModWeaponItem(ModWeaponItem.WeaponType.SHOVEL, 3, -2.8f, new Item.Properties(), 8, 2.0)); // Xẻng bật lùi mạnh hơn

    public static final RegistryObject<Item> WOODEN_HOE = REGISTRY.register("wooden_hoe",
            () -> new ModWeaponItem(ModWeaponItem.WeaponType.HOE, 2, -1.6f, new Item.Properties(), 5, 2.5)); // Cuốc có tầm đánh xa nhất

    //ModEmeraldItem
    public static final RegistryObject<Item> SMALL_EMERALD = REGISTRY.register("small_emerald",
            () -> new ModEmeraldItem(new Item.Properties(), 1));

    public static final RegistryObject<Item> EMERALD = REGISTRY.register("emerald",
            () -> new ModEmeraldItem(new Item.Properties(), 5));

    public static final RegistryObject<Item> LARGE_EMERALD = REGISTRY.register("large_emerald",
            () -> new ModEmeraldItem(new Item.Properties(), 10));

}