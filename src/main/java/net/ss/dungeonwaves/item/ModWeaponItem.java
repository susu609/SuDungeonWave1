package net.ss.dungeonwaves.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ModWeaponItem extends ModItem {
    public enum WeaponType {
        SWORD, AXE, PICKAXE, SHOVEL, HOE
    }

    private final WeaponType weaponType;
    private final int attackDamage;
    private final float attackSpeed;
    private final double attackRange;
    private final Multimap<Attribute, AttributeModifier> defaultModifiers;

    public ModWeaponItem (WeaponType type, int baseDamage, float baseSpeed, Properties properties, int emeraldCost, double range) {
        super(properties, emeraldCost);
        this.weaponType = type;
        this.attackDamage = baseDamage;
        this.attackSpeed = baseSpeed;
        this.attackRange = range;

        // 📌 Sử dụng AttributeModifier giống SwordItem
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", baseDamage, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", baseSpeed, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
    }

    @Override
    public @NotNull Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers (EquipmentSlot slot) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", attackDamage, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", attackSpeed - 4.0, AttributeModifier.Operation.ADDITION)); // ⚡ Sửa đổi attackSpeed

        return slot == EquipmentSlot.MAINHAND ? builder.build() : super.getDefaultAttributeModifiers(slot);
    }


    public int getAttackDamage () {
        return attackDamage;
    }

    public float getAttackSpeed () {
        return attackSpeed;
    }

    public WeaponType getWeaponType () {
        return weaponType;
    }

    public double getAttackRange () {
        return attackRange;
    }

    @Override
    public boolean hurtEnemy (@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        if (attacker instanceof Player player) {
            float attackStrength = player.getAttackStrengthScale(0.5f); // Tính toán cooldown đánh

            if (attackStrength >= 1.0f) { // Đảm bảo full cooldown
                float damage = getAttackDamage();
                target.hurt(player.damageSources().playerAttack(player), damage);

                switch (weaponType) {
                    case SWORD:
                        if (canSweepAttack(player)) {
                            performSweepAttack(player, target);
                        }
                        break;

                    case AXE:
                        if (attackStrength >= 0.8f) { // Giảm sát thương nếu không full cooldown
                            createShockwave(player);
                        }
                        break;

                    case PICKAXE:
                        if (player.fallDistance > 0.5F) { // Cúp tăng sát thương khi nhảy
                            target.hurt(player.damageSources().playerAttack(player), damage * 1.5f);
                        }
                        break;

                    case SHOVEL:
                        if (attackStrength >= 0.7f) {
                            target.knockback(0.5F, -Mth.sin(player.getYRot() * 0.017453292F), Mth.cos(player.getYRot() * 0.017453292F));
                        }
                        break;

                    case HOE:
                        // Cuốc không có hiệu ứng đặc biệt, nhưng tốc độ đánh nhanh
                        break;
                }

                // 📌 Âm thanh khi tấn công
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.PLAYER_ATTACK_STRONG, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    private boolean canSweepAttack (Player player) {
        return player.getAttackStrengthScale(0.5f) > 0.9f // Đòn đánh mạnh
                && player.onGround()  // Đứng trên mặt đất
                && !player.isSprinting(); // Không chạy nước rút
    }

    private void performSweepAttack (Player player, LivingEntity target) {
        float sweepDamage = attackDamage * 0.75F; // Sát thương chém quét giảm 25%
        double range = 3.0D; // Phạm vi chém quét

        // Lấy tất cả kẻ địch xung quanh trong phạm vi
        List<LivingEntity> nearbyEntities = player.level().getEntitiesOfClass(
                LivingEntity.class, target.getBoundingBox().inflate(range),
                e -> e != player && e != target && !player.isAlliedTo(e)
        );

        // Tấn công tất cả kẻ địch xung quanh
        for (LivingEntity entity : nearbyEntities) {
            entity.hurt(player.damageSources().playerAttack(player), sweepDamage);
            entity.knockback(0.1F, Mth.sin(player.getYRot() * 0.017453292F), -Mth.cos(player.getYRot() * 0.017453292F));
        }

        // Hiệu ứng chém quét
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F, 1.0F);
        player.sweepAttack();
    }

    private void createShockwave (Player player) {
        Level world = player.level();
        float pitch = player.getXRot();
        double range = (pitch <= -60.0F) ? 5.0D : 3.5D;

        // Phát sóng chấn động
        for (int i = 0; i < 10; i++) {
            world.addParticle(ParticleTypes.CLOUD,
                    player.getX() + (Math.random() - 0.5) * range,
                    player.getY(),
                    player.getZ() + (Math.random() - 0.5) * range,
                    0, 0.1, 0);
        }

        // Âm thanh chấn động
        world.playSound(null, player.blockPosition(), SoundEvents.ANVIL_LAND, SoundSource.PLAYERS, 1.0F, 0.8F);
    }
}
