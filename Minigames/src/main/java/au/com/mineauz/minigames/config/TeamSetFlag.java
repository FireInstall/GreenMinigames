package au.com.mineauz.minigames.config;

import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.TeamColor;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scoreboard.Team.Option;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class TeamSetFlag extends AFlag<Map<TeamColor, Team>> {
    private final Minigame mgm;

    public TeamSetFlag(Map<TeamColor, Team> value, String name, Minigame mgm) {
        setFlag(value);
        setDefaultFlag(value);
        setName(name);
        this.mgm = mgm;
    }

    @Override
    public void saveValue(@NotNull FileConfiguration config, @NotNull String path) {
        for (Team t : getFlag().values()) {
            TeamFlag tf = new TeamFlag(null, t.getColor().toString(), mgm);
            tf.setFlag(t);
            tf.saveValue(config, path + "." + getName());
        }
    }

    @Override
    public void loadValue(@NotNull FileConfiguration config, @NotNull String path) {
        Set<String> teams = config.getConfigurationSection(path + "." + getName()).getKeys(false);
        for (String t : teams) {
            TeamFlag tf = new TeamFlag(null, t, mgm);
            tf.loadValue(config, path + "." + getName());
            getFlag().put(tf.getFlag().getColor(), tf.getFlag());
            String sbTeam = tf.getFlag().getColor().toString().toLowerCase();
            mgm.getScoreboardManager().registerNewTeam(sbTeam);
            mgm.getScoreboardManager().getTeam(sbTeam).setAllowFriendlyFire(false);
            mgm.getScoreboardManager().getTeam(sbTeam).setCanSeeFriendlyInvisibles(true);
            mgm.getScoreboardManager().getTeam(sbTeam).setOption(Option.NAME_TAG_VISIBILITY, tf.getFlag().getNameTagVisibility());
            mgm.getScoreboardManager().getTeam(sbTeam).color(tf.getFlag().getTextColor());
        }
    }

    @Deprecated
    @Override
    public @Nullable MenuItem getMenuItem(@Nullable Material displayMat, @Nullable Component name) {
        return getMenuItem(displayMat, name, null);
    }

    @Deprecated
    @Override
    public @Nullable MenuItem getMenuItem(@Nullable Material displayMat, @Nullable Component name,
                                          @Nullable List<@NotNull Component> description) {
        return null;
    }
}
