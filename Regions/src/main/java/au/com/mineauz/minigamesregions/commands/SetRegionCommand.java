package au.com.mineauz.minigamesregions.commands;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameLangKey;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.RegionMessageManager;
import au.com.mineauz.minigamesregions.RegionModule;
import au.com.mineauz.minigamesregions.language.RegionLangKey;
import au.com.mineauz.minigamesregions.language.RegionPlaceHolderKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SetRegionCommand implements ICommand {

    @Override
    public @NotNull String getName() {
        return "region";
    }

    @Override
    public @NotNull String @Nullable [] getAliases() {
        return null;
    }

    @Override
    public boolean canBeConsole() {
        return false;
    }

    @Override
    public @NotNull Component getDescription() {
        return List.of("Creates, edits and deletes Minigame regions");
    }

    @Override
    public @NotNull String @Nullable [] getParameters() {
        return new String[]{"select", "create", "delete", "modify"};
    }

    @Override
    public Component getUsage() {
        return new String[]{
                "/minigame set <Minigame> region select <1/2>",
                "/minigame set <Minigame> region create <name>",
                "/minigame set <Minigame> region delete <name>",
                "/minigame set <Minigame> region modify"
        };
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.set.region";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @Nullable Minigame minigame,
                             @NotNull String label, @NotNull String @Nullable [] args) {
        if (args != null) {
            MinigamePlayer mgPlayer = Minigames.getPlugin().getPlayerManager().getMinigamePlayer((Player) sender);
            RegionModule rmod = RegionModule.getMinigameModule(minigame);
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("select")) {
                    Location ploc = mgPlayer.getLocation();
                    ploc.setY(ploc.getY() - 1);

                    if (args[1].equals("1")) {
                        Location p2 = mgPlayer.getSelectionPoints()[1];
                        mgPlayer.clearSelection();
                        mgPlayer.setSelection(ploc, p2);

                        MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MinigameLangKey.PLAYER_SELECT_POS1);
                    } else {
                        Location p2 = mgPlayer.getSelectionPoints()[0];
                        mgPlayer.clearSelection();
                        mgPlayer.setSelection(p2, ploc);

                        MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MinigameLangKey.PLAYER_SELECT_POS2);
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("create")) {
                    if (mgPlayer.hasSelection()) {
                        String name = args[1];
                        rmod.addRegion(name, new Region(name, mgPlayer.getSelectionPoints()[0], mgPlayer.getSelectionPoints()[1]));
                        mgPlayer.clearSelection();

                        MinigameMessageManager.sendMessage(mgPlayer, MinigameMessageType.INFO, RegionMessageManager.getBundleKey(),
                                RegionLangKey.REGION_CREATED,
                                Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(false)),
                                Placeholder.unparsed(RegionPlaceHolderKey.REGION.getKey(), name));
                    } else {
                        MinigameMessageManager.sendMessage(mgPlayer, MinigameMessageType.ERROR, RegionMessageManager.getBundleKey(),
                                RegionLangKey.REGION_ERROR_NOSELECTION);
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("delete")) {
                    if (rmod.hasRegion(args[1])) {
                        rmod.removeRegion(args[1]);
                        MinigameMessageManager.sendMessage(mgPlayer, MinigameMessageType.INFO, RegionMessageManager.getBundleKey(),
                                RegionLangKey.REGION_REMOVED,
                                Placeholder.unparsed(RegionPlaceHolderKey.REGION.getKey(), args[1]),
                                Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(false)));
                    } else {
                        MinigameMessageManager.sendMessage(mgPlayer, MinigameMessageType.ERROR, RegionMessageManager.getBundleKey(),
                                RegionLangKey.REGION_ERROR_NOREGION,
                                Placeholder.unparsed(RegionPlaceHolderKey.REGION.getKey(), args[1]),
                                Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(false)));
                    }
                    return true;
                }

            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("modify")) {
                    rmod.displayMenu(mgPlayer, null);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, Minigame minigame,
                                      String alias, @NotNull String @NotNull [] args) {

        if (args.length == 1) {
            List<String> tab = new ArrayList<>();
            tab.add("select");
            tab.add("create");
            tab.add("modify");
            tab.add("delete");
            return MinigameUtils.tabCompleteMatch(tab, args[0]);
        } else if (args.length == 2) {
            List<String> tab = new ArrayList<>();
            if (args[0].equalsIgnoreCase("select")) {
                tab.add("1");
                tab.add("2");
            } else if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("delete")) {
                RegionModule rmod = RegionModule.getMinigameModule(minigame);
                for (Region reg : rmod.getRegions()) {
                    tab.add(reg.getName());
                }
            }
            return MinigameUtils.tabCompleteMatch(tab, args[1]);
        }
        return null;
    }

}
