package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.managers.MinigameManager;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.langkeys.MgCommandLangKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgMenuLangKey;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemDisplayLoadout;
import au.com.mineauz.minigames.menu.MenuItemLoadoutAdd;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GlobalLoadoutCommand extends ACommand { //todo merge with loadout command
    private final MinigameManager mdata = Minigames.getPlugin().getMinigameManager();

    @Override
    public @NotNull String getName() {
        return "globalloadout";
    }

    @Override
    public @NotNull String @Nullable [] getAliases() {
        return new String[]{"gloadout", "loadout"};
    }

    @Override
    public boolean canBeConsole() {
        return false;
    }

    @Override
    public @NotNull Component getDescription() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_GLOBALLOADOUT_DESCRIPTION);
    }

    @Override
    public Component getUsage() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_GLOBALLOADOUT_USAGE);
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.globalloadout";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull String @NotNull [] args) {
        MinigamePlayer player = Minigames.getPlugin().getPlayerManager().getMinigamePlayer((Player) sender);
        Menu loadouts = new Menu(6, getName(), player);

        List<MenuItem> mi = new ArrayList<>();
        for (String ld : mdata.getLoadouts()) {
            Material displayMaterial = Material.WHITE_STAINED_GLASS_PANE;
            if (!mdata.getLoadout(ld).getItemSlots().isEmpty()) {
                displayMaterial = mdata.getLoadout(ld).getItem((Integer) mdata.getLoadout(ld).getItemSlots().toArray()[0]).getType();
            }
            mi.add(new MenuItemDisplayLoadout(displayMaterial, ld,
                    MinigameMessageManager.getMgMessageList(MgMenuLangKey.MENU_DELETE_SHIFTRIGHTCLICK), mdata.getLoadout(ld)));
        }
        loadouts.addItem(new MenuItemLoadoutAdd(Material.ITEM_FRAME, "Add Loadout", mdata.getLoadoutMap()), 53);
        loadouts.addItems(mi);

        loadouts.displayMenu(player);
        return true;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, @NotNull String @Nullable [] args) {
        return null;
    }
}
