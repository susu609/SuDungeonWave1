package net.ss.dungeonwaves.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.ss.dungeonwaves.util.GuiOpener;

@Mod.EventBusSubscriber
public class OpenGuiCommand {

    @SubscribeEvent
    public static void registerCommands (RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("opengui")
                        .then(Commands.argument("name", StringArgumentType.string())
                                .executes(context -> {
                                    String guiName = StringArgumentType.getString(context, "name");
                                    CommandSourceStack source = context.getSource();

                                    if (source.getEntity() instanceof ServerPlayer player) {
                                        openGuiByName(player, guiName);
                                    } else {
                                        source.sendFailure(Component.literal("This command can only be used by players!"));
                                    }
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
        );
    }

    private static void openGuiByName (ServerPlayer player, String guiName) {
        switch (guiName.toLowerCase()) {
            case "chosenmode" -> GuiOpener.openChosenModeGui(player);
            case "startergear" -> GuiOpener.openStarterGearGui(player);
            case "menumod" -> GuiOpener.openMenuModGui(player);
            default -> player.sendSystemMessage(Component.literal("Invalid GUI name!"));
        }
    }
}
