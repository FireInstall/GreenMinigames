package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.managers.language.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.langkeys.MgCommandLangKey;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBack;
import au.com.mineauz.minigames.menu.MenuItemString;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.script.ExpressionParser;
import au.com.mineauz.minigames.script.ScriptObject;
import au.com.mineauz.minigames.script.ScriptReference;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.language.RegionLangKey;
import au.com.mineauz.minigamesregions.language.RegionMessageManager;
import au.com.mineauz.minigamesregions.util.NullCommandSender;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public class ExecuteCommandAction extends AAction {
    private final StringFlag comd = new StringFlag("say Hello World!", "command");
    private final BooleanFlag silentExecute = new BooleanFlag(false, "silent");

    protected ExecuteCommandAction(@NotNull String name) {
        super(name);
    }

    @Override
    public @NotNull Component getDisplayname() {
        return RegionMessageManager.getMessage(RegionLangKey.MENU_ACTION_EXECUTECMD_NAME);
    }

    @Override
    public @NotNull IActionCategory getCategory() {
        return RegionActionCategories.SERVER;
    }

    @Override
    public @NotNull Map<@NotNull Component, @Nullable Component> describe() {
        return Map.of(RegionMessageManager.getMessage(RegionLangKey.MENU_COMMANDACTION_SILENT_NAME), Component.text(comd.getFlag()),
                RegionMessageManager.getMessage(RegionLangKey.MENU_COMMANDACTION_SILENT_NAME),
                MinigameMessageManager.getMgMessage(silentExecute.getFlag() ? MgCommandLangKey.COMMAND_STATE_ENABLED : MgCommandLangKey.COMMAND_STATE_DISABLED));
    }

    @Override
    public boolean useInRegions() {
        return true;
    }

    @Override
    public boolean useInNodes() {
        return true;
    }

    private String replacePlayerTags(MinigamePlayer player, String string) { //todo dataFixerUpper
        if (player == null) {
            return string;
        }

        return string
                .replace("{player}", player.getName())
                .replace("{dispplayer}", player.getDisplayName())
                .replace("{px}", String.valueOf(player.getLocation().getX()))
                .replace("{py}", String.valueOf(player.getLocation().getY()))
                .replace("{pz}", String.valueOf(player.getLocation().getZ()))
                .replace("{yaw}", String.valueOf(player.getLocation().getYaw()))
                .replace("{pitch}", String.valueOf(player.getLocation().getPitch()))
                .replace("{minigame}", player.getMinigame().getName())
                .replace("{dispminigame}", PlainTextComponentSerializer.plainText().serialize(player.getMinigame().getDisplayName()))
                .replace("{deaths}", String.valueOf(player.getDeaths()))
                .replace("{kills}", String.valueOf(player.getKills()))
                .replace("{reverts}", String.valueOf(player.getReverts()))
                .replace("{score}", String.valueOf(player.getScore()))
                .replace("{team}", (player.getTeam() != null ? player.getTeam().getDisplayName() : ""));
    }

    @Override
    public void executeRegionAction(final @Nullable MinigamePlayer mgPlayer, final @NotNull Region region) {
        debug(mgPlayer, region);
        String command = replacePlayerTags(mgPlayer, comd.getFlag());
        command = command.replace("{region}", region.getName());

        // New expression system
        ScriptObject base = new ScriptObject() {
            @Override
            public Set<String> getKeys() {
                return Set.of("player", "area", "minigame", "team");
            }

            @Override
            public String getAsString() {
                return "";
            }

            @Override
            public ScriptReference get(String name) {
                if (name.equalsIgnoreCase("player")) {
                    return mgPlayer;
                } else if (name.equalsIgnoreCase("area")) {
                    return region;
                } else if (name.equalsIgnoreCase("minigame")) {
                    return mgPlayer.getMinigame();
                } else if (name.equalsIgnoreCase("team")) {
                    return mgPlayer.getTeam();
                }

                return null;
            }
        };

        command = ExpressionParser.stringResolve(command, base, true, true);
        dispatch(command);
    }

    @Override
    public void executeNodeAction(final @NotNull MinigamePlayer mgPlayer, final @NotNull Node node) {
        debug(mgPlayer, node);
        String command = replacePlayerTags(mgPlayer, comd.getFlag());
        command = command
                .replace("{x}", String.valueOf(node.getLocation().getBlockX()))
                .replace("{y}", String.valueOf(node.getLocation().getBlockY()))
                .replace("{z}", String.valueOf(node.getLocation().getBlockZ()))
                .replace("{node}", node.getName());

        // New expression system
        ScriptObject base = new ScriptObject() {
            @Override
            public Set<String> getKeys() {
                return Set.of("player", "area", "minigame", "team");
            }

            @Override
            public String getAsString() {
                return "";
            }

            @Override
            public ScriptReference get(String name) {
                if (name.equalsIgnoreCase("player")) {
                    return mgPlayer;
                } else if (name.equalsIgnoreCase("area")) {
                    return node;
                } else if (name.equalsIgnoreCase("minigame")) {
                    return mgPlayer.getMinigame();
                } else if (name.equalsIgnoreCase("team")) {
                    return mgPlayer.getTeam();
                }

                return null;
            }
        };

        command = ExpressionParser.stringResolve(command, base, true, true);
        dispatch(command);
    }

    private void dispatch(String command) {
        if (silentExecute.getFlag()) {
            Bukkit.dispatchCommand(new NullCommandSender(), command);
        } else {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }

    @Override
    public void saveArguments(@NotNull FileConfiguration config,
                              @NotNull String path) {
        comd.saveValue(config, path);
        silentExecute.saveValue(config, path);
    }

    @Override
    public void loadArguments(@NotNull FileConfiguration config,
                              @NotNull String path) {
        comd.loadValue(config, path);
        silentExecute.loadValue(config, path);
    }

    @Override
    public boolean displayMenu(@NotNull MinigamePlayer mgPlayer, Menu previous) {
        Menu m = new Menu(3, getDisplayname(), mgPlayer);
        m.addItem(new MenuItemBack(previous), m.getSize() - 9);

        m.addItem(new MenuItemString(Material.COMMAND_BLOCK, RegionMessageManager.getMessage(RegionLangKey.MENU_COMMANDACTION_COMMAND_NAME),
                RegionMessageManager.getMessageList(RegionLangKey.MENU_COMMANDACTION_COMMAND_DESCRIPTION), new Callback<>() {

            @Override
            public String getValue() {
                return comd.getFlag();
            }

            @Override
            public void setValue(String value) {
                if (value.startsWith("./")) {
                    value = value.replaceFirst("./", "/");
                }
                comd.setFlag(value);
            }
        }));

        m.addItem(silentExecute.getMenuItem(Material.NOTE_BLOCK, RegionMessageManager.getMessage(RegionLangKey.MENU_COMMANDACTION_SILENT_NAME),
                RegionMessageManager.getMessageList(RegionLangKey.MENU_COMMANDACTION_SILENT_DESCRIPTION)));
        m.displayMenu(mgPlayer);
        return true;
    }
}
