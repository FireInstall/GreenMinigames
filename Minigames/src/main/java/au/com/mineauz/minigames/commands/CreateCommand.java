package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgCommandLangKey;
import au.com.mineauz.minigames.minigame.Minigame;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CreateCommand extends ACommand {

    @Override
    public @NotNull String getName() {
        return "create";
    }

    @Override
    public boolean canBeConsole() {
        return false;
    }

    @Override
    public @NotNull Component getDescription() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_CREATE_DESCRIPTION);
    }

    @Override
    public Component getUsage() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_CREATE_USAGE);
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.create";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        if (args.length > 0) {
            Player player = (Player) sender;
            String mgmName = args[0];
            if (MinigameUtils.sanitizeYamlString(mgmName) == null) {
                throw new CommandException("Name is not valid for use in a Config.");
            }
            if (!PLUGIN.getMinigameManager().hasMinigame(mgmName)) {
                MinigameType type;
                if (args.length >= 2) {
                    if (MinigameType.hasValue(args[1].toUpperCase())) {
                        type = MinigameType.valueOf(args[1].toUpperCase());
                    } else {
                        MinigameMessageManager.sendMgMessage(player, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTTYPE,
                                Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[1]));
                        return false;
                    }
                } else {
                    type = MinigameType.SINGLEPLAYER;
                }
                Minigame mgm = new Minigame(mgmName, type, player.getLocation());
                MinigameMessageManager.sendMgMessage(player, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_CREATE_SUCCESS,
                        Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), args[0]));
                List<String> mgs;
                if (PLUGIN.getConfig().contains("minigames")) {
                    mgs = PLUGIN.getConfig().getStringList("minigames");
                } else {
                    mgs = new ArrayList<>();
                }
                mgs.add(mgmName);
                PLUGIN.getConfig().set("minigames", mgs);
                PLUGIN.saveConfig();

                mgm.saveMinigame();
                PLUGIN.getMinigameManager().addMinigame(mgm);
            } else {
                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_CREATE_ERROR_EXISTS);
            }
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender,
                                                         @NotNull String @NotNull [] args) {
        if (args.length == 2) {
            List<String> types = new ArrayList<>(MinigameType.values().length);
            for (MinigameType type : MinigameType.values()) {
                types.add(type.toString().toLowerCase());
            }
            return CommandDispatcher.tabCompleteMatch(types, args[1]);
        }
        return null;
    }
}
