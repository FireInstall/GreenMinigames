package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigames.minigame.modules.InfectionModule;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import net.kyori.adventure.text.Component;
import org.apache.commons.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SetSurvivorTeamCommand implements ICommand {
    @Override
    public @NotNull String getName() {
        return "survivorteam";
    }

    @Override
    public @NotNull String @Nullable [] getAliases() {
        return new String[]{"svteam"};
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public @NotNull Component getDescription() {
        return "Set which team color will represent the Survivor team in an Infection Minigame (Default: blue).";
    }

    @Override
    public @NotNull String @Nullable [] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> survivorteam <TeamColor>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to set the survivor team of an Infection Minigame!";
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.set.survivorteam";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Minigame minigame, @NotNull String label, String @NotNull [] args) {
        if (args != null) {
            if (args[0].equalsIgnoreCase("None")) {
                InfectionModule.getMinigameModule(minigame).setSurvivorTeam(null);
                sender.sendMessage(ChatColor.GRAY + "The survivor team of " + minigame + " has been set to none.");
            } else if (args[0].equalsIgnoreCase("Default")) {
                InfectionModule.getMinigameModule(minigame).setSurvivorTeam(WordUtils.capitalize(InfectionModule.getMinigameModule(minigame).getDefaultSurvivorTeam().toString().toLowerCase().replace("_", " ")));
                sender.sendMessage(ChatColor.GRAY + "The survivor team of " + minigame + " has been set to " +
                        WordUtils.capitalize(InfectionModule.getMinigameModule(minigame).getDefaultSurvivorTeam().toString().toLowerCase().replace("_", " ")));
            } else {
                if (TeamColor.matchColor(args[0]) == InfectionModule.getMinigameModule(minigame).getDefaultInfectedTeam() ||
                        TeamColor.matchColor(args[0]) == InfectionModule.getMinigameModule(minigame).getDefaultSurvivorTeam() ||
                        TeamsModule.getMinigameModule(minigame).hasTeam(TeamColor.matchColor(args[0]))) {
                    InfectionModule.getMinigameModule(minigame).setSurvivorTeam(args[0]);
                    sender.sendMessage(ChatColor.GRAY + "The survivor team of " + minigame + " has been set to " + args[0] + ".");
                } else {
                    sender.sendMessage(ChatColor.RED + "There is no team for the color " + args[0]);
                }
            }
        }
        return false;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, Minigame minigame, String alias, @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            List<String> teams = new ArrayList<>();
            for (Team t : TeamsModule.getMinigameModule(minigame).getTeams()) {
                teams.add(t.getColor().toString().toLowerCase());
            }
            teams.add("none");
            teams.add("default");
            teams.add(WordUtils.capitalize(InfectionModule.getMinigameModule(minigame).getDefaultInfectedTeam().toString().toLowerCase().replace("_", " ")));
            teams.add(WordUtils.capitalize(InfectionModule.getMinigameModule(minigame).getDefaultSurvivorTeam().toString().toLowerCase().replace("_", " ")));
            return MinigameUtils.tabCompleteMatch(teams, args[0]);
        }
        return null;
    }
}
