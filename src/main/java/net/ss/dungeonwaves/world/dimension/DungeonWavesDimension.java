package net.ss.dungeonwaves.world.dimension;

import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.ss.dungeonwaves.entity.WanderingMerchantEntity;
import net.ss.dungeonwaves.init.SsModEntities;
import net.ss.dungeonwaves.manager.DungeonStructureManager;
import net.ss.dungeonwaves.util.GuiOpener;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber
public class DungeonWavesDimension {
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class DungeonWavesSpecialEffectsHandler {
        @SubscribeEvent
        @OnlyIn(Dist.CLIENT)
        public static void registerDimensionSpecialEffects (RegisterDimensionSpecialEffectsEvent event) {
            DimensionSpecialEffects customEffect = new DimensionSpecialEffects(192f, true, DimensionSpecialEffects.SkyType.NONE, false, false) {
                @Override
                public @NotNull Vec3 getBrightnessDependentFogColor (@NotNull Vec3 color, float sunHeight) {
                    return color;
                }

                @Override
                public boolean isFoggyAt (int x, int y) {
                    return false;
                }
            };
            event.register(new ResourceLocation("ss:dungeon_waves"), customEffect);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimensionEvent(PlayerEvent.PlayerChangedDimensionEvent event) {
        Entity entity = event.getEntity();
        Level world = entity.level();

        if (event.getTo() == ResourceKey.create(Registries.DIMENSION, new ResourceLocation("ss:dungeon_waves"))) {
            if (entity instanceof ServerPlayer _ent) {
                ServerLevel serverWorld = _ent.serverLevel();
                DungeonStructureManager.generateDungeon(serverWorld);

                _ent.teleportTo(0.5, 3, 0.5); // Dịch chuyển người chơi về trung tâm
                serverWorld.getGameRules().getRule(GameRules.RULE_DAYLIGHT).set(false, serverWorld.getServer());
                serverWorld.setDayTime(18000); // Đặt thời gian thành nửa đêm
                serverWorld.getGameRules().getRule(GameRules.RULE_DOMOBSPAWNING).set(false, serverWorld.getServer());

                // ✅ Bỏ phần spawn thương nhân vì đã có trong `MerchantEventHandler`
                GuiOpener.openMenuModGui(_ent); // Mở GUI khi vào dimension
            }
        }
    }

}
