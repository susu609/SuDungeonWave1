package net.ss.dungeonwaves.item;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.ss.dungeonwaves.client.gui.ShopGuiScreen;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ModItem extends Item {
    protected final int emeraldCost;

    public ModItem(Properties properties, int emeraldCost) {
        super(properties.stacksTo(1));
        this.emeraldCost = emeraldCost;
    }

    public int getEmeraldCost() {
        return emeraldCost;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        if (net.minecraft.client.Minecraft.getInstance().screen instanceof ShopGuiScreen) {
            tooltip.add(Component.literal("Cost: " + emeraldCost + " Emeralds")
                    .setStyle(Style.EMPTY.withColor(0x00FF00))); // Chỉ hiện khi trong Shop
        }
    }
}
