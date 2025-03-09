package net.ss.dungeonwaves.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.registries.ForgeRegistries;
import net.ss.dungeonwaves.init.SsModEntities;
import net.ss.dungeonwaves.network.SsModVariables;
import net.ss.dungeonwaves.util.GuiOpener;
import net.ss.dungeonwaves.util.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WanderingMerchantEntity extends AbstractVillager {

    public WanderingMerchantEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(SsModEntities.WANDERING_MERCHANT.get(), world);
    }

    public WanderingMerchantEntity(EntityType<WanderingMerchantEntity> type, Level world) {
        super(type, world);
        setMaxUpStep(0.6f);
        xpReward = 0;
        setNoAi(false);

    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket () {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, false) {

            @Override
            protected double getAttackReachSqr (@NotNull LivingEntity entity) {
                return this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth();
            }

        });
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, new FloatGoal(this));

    }

    @Override
    public @NotNull MobType getMobType () {
        return MobType.UNDEFINED;
    }

    @Override
    public SoundEvent getHurtSound (@NotNull DamageSource ds) {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.hurt"));
    }

    @Override
    public SoundEvent getDeathSound() {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.death"));
    }

    public static void init() {
        SpawnPlacements.register(SsModEntities.WANDERING_MERCHANT.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (entityType, world, reason, pos, random) -> (world.getDifficulty() != Difficulty.PEACEFUL && Monster.isDarkEnoughToSpawn(world, pos, random) && Mob.checkMobSpawnRules(entityType, world, reason, pos, random)));

    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.3);
        builder = builder.add(Attributes.MAX_HEALTH, 10);
        builder = builder.add(Attributes.ARMOR, 0);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 3);
        builder = builder.add(Attributes.FOLLOW_RANGE, 16);

        return builder;
    }

    @Override
    protected void rewardTradeXp (@NotNull MerchantOffer merchantOffer) {

    }

    @Override
    protected void updateTrades () {

    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring (@NotNull ServerLevel serverLevel, @NotNull AgeableMob ageableMob) {
        return null;
    }

    @Override
    public void aiStep() {
        super.aiStep();

        SsModVariables.MapVariables data = SsModVariables.MapVariables.get(this.level());

        if (data.inCombat) {
            this.setInvisible(true);
            this.setInvulnerable(true);
        } else {
            this.setInvisible(false);
            this.setInvulnerable(false);
        }
    }

    public void teleportWithEffect() {
        if (this.level().isClientSide) return;

        ServerLevel world = (ServerLevel) this.level();

        // Dịch chuyển đến một vị trí gần (0.5, 2, 0.5)
        double x = 0.5 + (this.random.nextDouble() - 0.5) * 4; // Dịch chuyển trong phạm vi ±2 block
        double z = 0.5 + (this.random.nextDouble() - 0.5) * 4;
        double y = 2.0; // Cố định độ cao

        this.setPos(x, y, z);

        // Hiệu ứng dịch chuyển
        world.sendParticles(ParticleTypes.PORTAL, this.getX(), this.getY() + 1, this.getZ(), 30, 0.5, 1, 0.5, 0.2);
        world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 1.0F, 1.0F);

        Log.d("✨ Merchant teleported near (0.5, 2, 0.5)");
    }

    @Override
    public @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        if (!this.level().isClientSide && player instanceof ServerPlayer serverPlayer) {
            SsModVariables.MapVariables data = SsModVariables.MapVariables.get(this.level());

            if (data.wave == 0) {
                GuiOpener.openStarterGearGui(serverPlayer);
            } else {
                GuiOpener.openShopGui(serverPlayer); // 🛒 Mở GUI Shop nếu wave đã bắt đầu
            }
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

}