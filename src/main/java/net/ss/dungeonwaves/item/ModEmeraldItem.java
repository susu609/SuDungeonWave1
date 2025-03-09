package net.ss.dungeonwaves.item;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ModEmeraldItem extends Item {
    private final int emeraldValue;

    public ModEmeraldItem(Properties properties, int emeraldValue) {
        super(properties);
        this.emeraldValue = emeraldValue;
    }

    public int getEmeraldValue() {
        return emeraldValue;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal("Value: " + emeraldValue + " Emeralds")
                .setStyle(Style.EMPTY.withColor(0x00FF00)));
    }
}
