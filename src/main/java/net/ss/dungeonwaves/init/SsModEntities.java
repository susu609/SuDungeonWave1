package net.ss.dungeonwaves.init;

import net.ss.dungeonwaves.DungeonWavesMod;
import net.ss.dungeonwaves.entity.WanderingMerchantEntity;


import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class SsModEntities {
    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, DungeonWavesMod.MODID);
    public static final RegistryObject<EntityType<WanderingMerchantEntity>> WANDERING_MERCHANT = register("wandering_merchant",
            EntityType.Builder.<WanderingMerchantEntity>of(WanderingMerchantEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(WanderingMerchantEntity::new)

                    .sized(0.6f, 1.95f));

    // Start of user code block custom entities
    // End of user code block custom entities
    private static <T extends Entity> RegistryObject<EntityType<T>> register(String registryname, EntityType.Builder<T> entityTypeBuilder) {
        return REGISTRY.register(registryname, () -> (EntityType<T>) entityTypeBuilder.build(registryname));
    }

    @SubscribeEvent
    public static void init(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            WanderingMerchantEntity.init();
        });
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(WANDERING_MERCHANT.get(), WanderingMerchantEntity.createAttributes().build());
    }
}
