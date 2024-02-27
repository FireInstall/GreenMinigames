package au.com.mineauz.minigames.minigame;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.config.*;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.langkeys.LangKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgMenuLangKey;
import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.script.ScriptCollection;
import au.com.mineauz.minigames.script.ScriptObject;
import au.com.mineauz.minigames.script.ScriptReference;
import au.com.mineauz.minigames.script.ScriptValue;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.commons.text.WordUtils;
import org.bukkit.Location;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Team implements ScriptObject {
    private final IntegerFlag maxPlayers = new IntegerFlag(0, "maxPlayers");
    private final List<Location> startLocations = new ArrayList<>();
    private final StringFlag playerAssignMsg = new StringFlag(MinigameMessageManager.getUnformattedMgMessage(MinigameLangKey.PLAYER_TEAM_ASSIGN_JOINTEAM), "assignMsg");
    private final StringFlag joinAnnounceMsg = new StringFlag(MinigameMessageManager.getUnformattedMgMessage(MinigameLangKey.PLAYER_TEAM_ASSIGN_JOINANNOUNCE), "gameAssignMsg");
    private final StringFlag autobalanceMsg = new StringFlag(MinigameMessageManager.getUnformattedMgMessage(MinigameLangKey.PLAYER_TEAM_AUTOBALANCE_PLYMSG), "autobalanceMsg");
    private final StringFlag gameAutobalanceMsg = new StringFlag(MinigameMessageManager.getUnformattedMgMessage(MinigameLangKey.PLAYER_TEAM_AUTOBALANCE_MINIGAMEMSG), "gameAutobalanceMsg");
    private final EnumFlag<OptionStatus> nametagVisibility = new EnumFlag<>(OptionStatus.ALWAYS, "nametagVisibility");
    private final BooleanFlag autoBalance = new BooleanFlag(true, "autoBalance");
    private final List<MinigamePlayer> players = new ArrayList<>();
    private final Minigame mgm;
    private final String scoreboardName;
    private String displayName;
    private @NotNull TeamColor color;
    private int score = 0;

    /**
     * Creates a team for the use in a specific Minigame
     *
     * @param color    - The unique team color to identify the team by.
     * @param minigame - The Minigame this team is assigned to.
     */
    public Team(TeamColor color, Minigame minigame) {
        this.color = color;
        displayName = WordUtils.capitalizeFully(color.toString()) + " Team";
        scoreboardName = color.toString().toLowerCase();
        mgm = minigame;
    }

    /**
     * Gets the teams Minigame
     *
     * @return The Minigame this team is assigned to.
     */
    public Minigame getMinigame() {
        return mgm;
    }

    /**
     * Changes the color of the team for the Minigame its assigned to.
     *
     * @param color - The color to change this team to.
     * @return true if the Minigame doesn't have the team color already available, fails if it already has that team.
     */
    public boolean setColor(TeamColor color) {
        if (!TeamsModule.getMinigameModule(mgm).hasTeam(color)) {
            if (displayName.toLowerCase().equals(this.color.toString().toLowerCase() + " team"))
                displayName = WordUtils.capitalizeFully(color.toString()) + " Team";
            TeamsModule.getMinigameModule(mgm).removeTeam(this.color);
            this.color = color;
            TeamsModule.getMinigameModule(mgm).addTeam(color, this);

            return true;
        }
        return false;
    }

    /**
     * Gets the teams color.
     *
     * @return The teams color.
     */
    public @NotNull TeamColor getColor() {
        return color;
    }

    /**
     * Gets the teams TextColor alternative.
     *
     * @return The TextColor
     */
    public @NotNull NamedTextColor getTextColor() {
        return color.getColor();
    } //todo

    /**
     * Gets the teams display name. If none is set, it will return the teams color followed by "Team".
     *
     * @return The display name or the teams color followed by "Team"
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Sets the display name for this team. If the name is longer than 32 characters,
     * it'll be trimmed to that length (Minecraft limitation).
     *
     * @param name - The name to change the team to.
     */
    public void setDisplayName(String name) {
        if (name.length() > 32)
            name = name.substring(0, 31);
        displayName = name;
    }

    /**
     * Gets the display name prefixed with its color. If none is set,
     * it will return the teams color followed by "Team".
     *
     * @return The colored display name or the team color followed by "Team"
     */
    public Component getColoredDisplayName() {
        return Component.text(getDisplayName(), getTextColor());
    }

    public Set<AFlag<?>> getFlags() {
        Set<AFlag<?>> flags = new HashSet<>();
        flags.add(maxPlayers);
        flags.add(playerAssignMsg);
        flags.add(joinAnnounceMsg);
        flags.add(gameAutobalanceMsg);
        flags.add(autobalanceMsg);
        flags.add(nametagVisibility);
        flags.add(autoBalance);

        return flags;
    }

    public int getMaxPlayers() {
        return maxPlayers.getFlag();
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers.setFlag(maxPlayers);
    }

    public boolean hasRoom() {
        return maxPlayers.getFlag() == 0 || players.size() < maxPlayers.getFlag();
    }


    /**
     * Gets the teams current score
     *
     * @return The score.
     */
    public int getScore() {
        return score;
    }

    /**
     * Sets the teams score to a specific value.
     *
     * @param amount The score amount to set for the team.
     */
    public void setScore(int amount) {
        score = amount;
        Objective obj = mgm.getScoreboardManager().getObjective(mgm.getName());
        if (obj != null) {
            obj.getScore(getDisplayName()).setScore(score);
        }
    }

    /**
     * Adds 1 point to the team.
     */
    public int addScore() {
        return addScore(1);
    }

    /**
     * Adds a specific amount to the teams score.
     *
     * @param amount - The amount of points to add to the team
     * @return The new score for the team.
     */
    public int addScore(int amount) {
        score += amount;
        Objective obj = mgm.getScoreboardManager().getObjective(mgm.getName());
        if (obj != null) {
            obj.getScore(getDisplayName()).setScore(score);
        }
        return score;
    }

    /**
     * Sets the teams score back to 0.
     */
    public void resetScore() {
        score = 0;
        mgm.getScoreboardManager().resetScores(getDisplayName());
    }

    /**
     * Gets a list of all the players assigned to this team.
     *
     * @return A list of all players assigned to the team.
     */
    public List<MinigamePlayer> getPlayers() {
        return players;
    }

    /**
     * Adds a player to the team.
     *
     * @param player - The player to add.
     */
    public void addPlayer(MinigamePlayer player) {
        players.add(player);
        player.setTeam(this);
        player.getPlayer().setScoreboard(mgm.getScoreboardManager());
        org.bukkit.scoreboard.Team team = mgm.getScoreboardManager().getTeam(scoreboardName);
        if (team != null) {
            team.addEntry(player.getDisplayName(mgm.usePlayerDisplayNames()));
        }
    }

    /**
     * Removes a player from the team.
     *
     * @param player - The player to remove.
     */
    public void removePlayer(MinigamePlayer player) {
        players.remove(player);
        Scoreboard board = mgm.getScoreboardManager();
        org.bukkit.scoreboard.Team team = board.getTeam(scoreboardName);
        if (team != null) {
            team.removeEntry(player.getDisplayName(mgm.usePlayerDisplayNames()));
        }
        player.getPlayer().setScoreboard(Minigames.getPlugin().getServer().getScoreboardManager().getMainScoreboard());
    }

    /**
     * Adds a starting location for the team to spawn at.
     *
     * @param loc - The location to add to the team.
     */
    public void addStartLocation(Location loc) {
        startLocations.add(loc);
    }

    /**
     * Replaces a starting location already assigned on the team.
     *
     * @param loc    - The new location
     * @param number - The number id of the original starting location (Ranging from 1 to the amount of start points [Not 0])
     */
    public void addStartLocation(Location loc, int number) {
        if (startLocations.size() >= number) {
            startLocations.set(number - 1, loc);
        } else {
            startLocations.add(loc);
        }
    }

    /**
     * Gets all the starting locations for this team.
     *
     * @return The teams starting locations.
     */
    public List<Location> getStartLocations() {
        return startLocations;
    }

    /**
     * Gets whether the team has start locations
     *
     * @return true if the team has start locations
     */
    public boolean hasStartLocations() {
        return !startLocations.isEmpty();
    }

    /**
     * Removes a specific start location from this team.
     *
     * @param locNumber - The id of the starting location.
     * @return true if removal was successful.
     */
    public boolean removeStartLocation(int locNumber) {
        if (startLocations.size() > locNumber) {
            startLocations.remove(locNumber);
            return true;
        }
        return false;
    }

    public String getPlayerAssignMessage() {
        return playerAssignMsg.getFlag();
    }

    public StringFlag getPlayerAssignMessageFlag() {
        return playerAssignMsg;
    }

    public void setPlayerAssignMsg(String msg) {
        playerAssignMsg.setFlag(msg);
    }

    public String getJoinAnnounceMessage() {
        return joinAnnounceMsg.getFlag();
    }

    public void setGameAssignMessage(String msg) {
        joinAnnounceMsg.setFlag(msg);
    }

    public String getAutobalanceMessage() {
        return autobalanceMsg.getFlag();
    }

    public void setAutobalanceMessage(String msg) {
        autobalanceMsg.setFlag(msg);
    }

    public String getGameAutobalanceMessage() {
        return gameAutobalanceMsg.getFlag();
    }

    public void setGameAutobalanceMessage(String msg) {
        gameAutobalanceMsg.setFlag(msg);
    }

    public StringFlag getAutoBalanceMsgFlag() {
        return autobalanceMsg;
    }

    public StringFlag getGameAutoBalanceMsgFlag() {
        return gameAutobalanceMsg;
    }

    public OptionStatus getNameTagVisibility() {
        return nametagVisibility.getFlag();
    }

    public void setNameTagVisibility(OptionStatus vis) {
        nametagVisibility.setFlag(vis);
        org.bukkit.scoreboard.Team team = mgm.getScoreboardManager().getTeam(color.toString().toLowerCase());
        if (team != null)
            team.setOption(Option.NAME_TAG_VISIBILITY, vis);
        else
            Minigames.getCmpnntLogger().warn("No team set for visibility call");
    }

    public Callback<VisibilityMapper> getNameTagVisibilityCallback() {
        return new Callback<>() {

            @Override
            public VisibilityMapper getValue() {
                return VisibilityMapper.getMapping(getNameTagVisibility());
            }

            @Override
            public void setValue(VisibilityMapper value) {
                setNameTagVisibility(value.getStatus());
            }
        };
    }

    public Callback<Boolean> getAutoBalanceCallBack() {
        return new Callback<>() {

            @Override
            public Boolean getValue() {
                return getAutoBalanceTeam();
            }

            @Override
            public void setValue(Boolean value) {
                setAutoBalance(value);
            }
        };
    }

    public boolean getAutoBalanceTeam() {
        return autoBalance.getFlag();
    }

    public void setAutoBalance(Boolean flag) {
        autoBalance.setFlag(flag);
    }

    @Override
    public ScriptReference get(String name) {
        if (name.equalsIgnoreCase("colorname")) {
            return ScriptValue.of(getColor().name());
        } else if (name.equalsIgnoreCase("color")) {
            return ScriptValue.of(getTextColor().toString());
        } else if (name.equalsIgnoreCase("name")) {
            return ScriptValue.of(getDisplayName());
        } else if (name.equalsIgnoreCase("score")) {
            return ScriptValue.of(score);
        } else if (name.equalsIgnoreCase("players")) {
            return ScriptCollection.of(players);
        } else if (name.equalsIgnoreCase("minigame")) {
            return mgm;
        }

        return null;
    }

    @Override
    public Set<String> getKeys() {
        return Set.of("colorname", "color", "name", "score", "players", "minigame");
    }

    @Override
    public String getAsString() {
        return getColor().name();
    }

    /**
     * I have no Idea, why whoever fucked the naming in Bukkit up, but it is pretty bad
     */
    public enum VisibilityMapper {
        /**
         * Apply this option to everyone.
         */
        ALWAYS(OptionStatus.ALWAYS, MgMenuLangKey.MENU_TEAM_NAMEVISIBILITY_ALWAYSVISIBLE),
        /**
         * Never apply this option.
         */
        NEVER(OptionStatus.NEVER, MgMenuLangKey.MENU_TEAM_NAMEVISIBILITY_NEVERVISIBLE),
        /**
         * Apply this option only for opposing teams.
         */
        HIDE_FOR_OTHER_TEAMS(OptionStatus.FOR_OTHER_TEAMS, MgMenuLangKey.MENU_TEAM_NAMEVISIBILITY_HIDEOTHERTEAM),
        /**
         * Apply this option for only team members.
         */
        HIDE_FOR_OWN_TEAM(OptionStatus.FOR_OWN_TEAM, MgMenuLangKey.MENU_TEAM_NAMEVISIBILITY_HIDEOWNTEAM);

        private final @NotNull OptionStatus status;
        private final @NotNull String name;

        VisibilityMapper(@NotNull OptionStatus status, @NotNull LangKey langKey) {
            this.status = status;
            this.name = MinigameMessageManager.getUnformattedMgMessage(langKey);
        }

        static @NotNull VisibilityMapper getMapping(@NotNull OptionStatus status) {
            for (VisibilityMapper mapping : VisibilityMapper.values()) {
                if (status == mapping.status) {
                    return mapping;
                }
            }

            // fallback should never get used unless Mojang decides to add another visibility
            return ALWAYS;
        }

        @NotNull OptionStatus getStatus() {
            return status;
        }

        @Override
        public @NotNull String toString() {
            return name;
        }
    }
}
