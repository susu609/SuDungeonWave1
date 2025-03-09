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
            () -> new ModWeaponItem(ModWeaponItem.WeaponType.SWORD, 3, -2.4f, new Item.Properties(), 10));

    public static final RegistryObject<Item> WOODEN_AXE = REGISTRY.register("wooden_axe",
            () -> new ModWeaponItem(ModWeaponItem.WeaponType.AXE, 6, -3.1f, new Item.Properties(), 15));

    public static final RegistryObject<Item> WOODEN_PICKAXE = REGISTRY.register("wooden_pickaxe",
            () -> new ModWeaponItem(ModWeaponItem.WeaponType.PICKAXE, 2, -2.8f, new Item.Properties(), 12));

    public static final RegistryObject<Item> WOODEN_SHOVEL = REGISTRY.register("wooden_shovel",
            () -> new ModWeaponItem(ModWeaponItem.WeaponType.SHOVEL, 1, -3.0f, new Item.Properties(), 8));

    public static final RegistryObject<Item> WOODEN_HOE = REGISTRY.register("wooden_hoe",
            () -> new ModWeaponItem(ModWeaponItem.WeaponType.HOE, 0, -3.0f, new Item.Properties(), 5));
//ModEmeraldItem
    public static final RegistryObject<Item> SMALL_EMERALD = REGISTRY.register("small_emerald",
            () -> new ModEmeraldItem(new Item.Properties(), 1));

    public static final RegistryObject<Item> EMERALD = REGISTRY.register("emerald",
            () -> new ModEmeraldItem(new Item.Properties(), 5));

    public static final RegistryObject<Item> LARGE_EMERALD = REGISTRY.register("large_emerald",
            () -> new ModEmeraldItem(new Item.Properties(), 10));

}