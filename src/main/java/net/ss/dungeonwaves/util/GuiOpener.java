package net.ss.dungeonwaves.util;

import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkHooks;
import net.ss.dungeonwaves.world.inventory.ChosenModeGuiMenu;
import net.ss.dungeonwaves.world.inventory.MenuModGuiMenu;
import net.ss.dungeonwaves.world.inventory.ShopGuiMenu;
import net.ss.dungeonwaves.world.inventory.StarterGearGuiMenu;
import org.jetbrains.annotations.NotNull;

public class GuiOpener {

    public static void openChosenModeGui (ServerPlayer player) {
        openGui(player, "ChosenModeGui", (id, inventory) ->
                new ChosenModeGuiMenu(id, inventory, createBuffer(player.blockPosition())));
    }

    public static void openStarterGearGui (ServerPlayer player) {
        openGui(player, "StarterGearGui", (id, inventory) ->
                new StarterGearGuiMenu(id, inventory, createBuffer(player.blockPosition())));
    }

    public static void openMenuModGui (ServerPlayer player) {
        openGui(player, "MenuModGui", (id, inventory) ->
                new MenuModGuiMenu(id, inventory, createBuffer(player.blockPosition())));
    }

    public static void openShopGui (ServerPlayer player) {
        openGui(player, "ShopGui", (id, inventory) ->
                new ShopGuiMenu(id, inventory, createBuffer(player.blockPosition())));
    }

    private static void openGui (ServerPlayer player, String title, GuiMenuFactory factory) {
        BlockPos pos = player.blockPosition();
        NetworkHooks.openScreen(player, new SimpleMenuProvider(title, factory, pos), buf -> buf.writeBlockPos(pos));
    }

    private static FriendlyByteBuf createBuffer(BlockPos pos) {
        return new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(pos);
    }


    @FunctionalInterface
    public interface GuiMenuFactory {
        AbstractContainerMenu create (int id, Inventory inventory);
    }

    private record SimpleMenuProvider(String title, GuiMenuFactory factory,
                                      BlockPos pos) implements net.minecraft.world.MenuProvider {

        @Override
            public @NotNull Component getDisplayName () {
                return Component.literal(title);
            }

            @Override
            public AbstractContainerMenu createMenu (int id, @NotNull Inventory inventory, net.minecraft.world.entity.player.@NotNull Player player) {
                return factory.create(id, inventory);
            }
        }
}
