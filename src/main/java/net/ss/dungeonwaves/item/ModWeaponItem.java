package net.ss.dungeonwaves.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class ModWeaponItem extends ModItem {
    public enum WeaponType {
        SWORD, AXE, PICKAXE, SHOVEL, HOE
    }

    private final WeaponType weaponType;
    private final int attackDamage;
    private final float attackSpeed;

    public ModWeaponItem (WeaponType type, int attackDamage, float attackSpeed, Properties properties, int emeraldCost) {
        super(properties, emeraldCost);
        this.weaponType = type;
        this.attackDamage = attackDamage;
        this.attackSpeed = attackSpeed;
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

    @Override
    public boolean hurtEnemy (ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof Player player) {
            if (player.getAttackStrengthScale(0.5f) == 1.0f) { // Full cooldown (100% lực đánh)
                if (player.getAttackStrengthScale(0.5f) == 1.0f) { // Full cooldown (100% lực đánh)
                    float damage = getAttackDamage(); // ✅ Sử dụng giá trị từ getAttackDamage()
                    target.hurt(player.damageSources().playerAttack(player), damage);
                }

                float pitch = player.getXRot(); // Lấy góc nhìn dọc
                double dx = target.getX() - player.getX();
                double dz = target.getZ() - player.getZ();
                double distance = Math.sqrt(dx * dx + dz * dz);

                switch (weaponType) {
                    case SWORD:
                        if (canSweepAttack(player)) {
                            performSweepAttack(player, target);
                        }
                        break;

                    case AXE:
                    case PICKAXE:
                        if (pitch <= -45.0F) {
                            float slamDamage = (pitch <= -60.0F) ? attackDamage * 2.0F : attackDamage * 1.75F;
                            target.hurt(player.damageSources().playerAttack(player), slamDamage);
                            if (distance > 0.0) {
                                target.knockback(1.5F, dx / distance, dz / distance);
                            }
                            if (player.onGround()) {
                                createShockwave(player);
                            }
                        }
                        break;

                    case SHOVEL:
                        target.knockback(2.0f, dx / distance, dz / distance);
                        break;

                    case HOE:
                        break;
                }
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
            entity.knockback(0.4F, Mth.sin(player.getYRot() * 0.017453292F), -Mth.cos(player.getYRot() * 0.017453292F));
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
