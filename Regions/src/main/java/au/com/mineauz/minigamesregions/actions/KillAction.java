package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.RegionMessageManager;
import au.com.mineauz.minigamesregions.language.RegionLangKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class KillAction extends AAction {

    protected KillAction(@NotNull String name) {
        super(name);
    }

    @Override
    public @NotNull Component getDisplayname() {
        return RegionMessageManager.getMessage(RegionLangKey.MENU_ACTION_KILL_NAME);
    }

    @Override
    public @NotNull IActionCategory getCategory() {
        return RegionActionCategories.WORLD;
    }

    @Override
    public @NotNull Map<@NotNull Component, @Nullable ComponentLike> describe() {
        return Map.of();
    }

    @Override
    public boolean useInRegions() {
        return true;
    }

    @Override
    public boolean useInNodes() {
        return true;
    }

    @Override
    public void executeNodeAction(@NotNull MinigamePlayer mgPlayer,
                                  @NotNull Node node) {
        debug(mgPlayer, node);
        if (!mgPlayer.isInMinigame()) return;
        if (!mgPlayer.isLiving())
            mgPlayer.getPlayer().setHealth(0.0);
    }

    @Override
    public void executeRegionAction(@Nullable MinigamePlayer mgPlayer, @NotNull Region region) {
        if (mgPlayer == null || !mgPlayer.isInMinigame()) return;
        debug(mgPlayer, region);
        if (mgPlayer.isLiving())
            mgPlayer.getPlayer().setHealth(0.0);
    }

    @Override
    public void saveArguments(@NotNull FileConfiguration config, @NotNull String path) {
    }

    @Override
    public void loadArguments(@NotNull FileConfiguration config, @NotNull String path) {
    }

    @Override
    public boolean displayMenu(@NotNull MinigamePlayer mgPlayer, Menu previous) {
        return false;
    }
}
