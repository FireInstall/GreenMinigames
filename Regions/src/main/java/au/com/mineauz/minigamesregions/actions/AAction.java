package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.managers.language.MinigameMessageManager;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.script.ScriptObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class AAction implements ActionInterface {
    protected final @NotNull String name;

    protected AAction(@NotNull String name) {
        this.name = name;
    }

    /**
     * Logs Debug re these 2 items.
     *
     * @param mgPlayer     the player
     * @param scriptObject a script object
     */
    public void debug(final @Nullable MinigamePlayer mgPlayer, final @NotNull ScriptObject scriptObject) {
        if (Minigames.getPlugin().isDebugging()) {
            MinigameMessageManager.debugMessage("Debug: Execute on Obj:"
                    + scriptObject.getAsString() + " as Action: " + this + " Player: "
                    + ((mgPlayer == null) ? "no player" : mgPlayer.getAsString()));
        }
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    /**
     * Set winners losers.
     *
     * @param winner the winner
     */
    void setWinnersLosers(final @NotNull MinigamePlayer winner) {
        if (winner.getMinigame().getType() != MinigameType.SINGLEPLAYER) {
            final List<MinigamePlayer> w;
            final List<MinigamePlayer> l;
            if (winner.getMinigame().isTeamGame()) {
                w = new ArrayList<>(winner.getTeam().getPlayers());
                l = new ArrayList<>(winner.getMinigame().getPlayers().size()
                        - winner.getTeam().getPlayers().size());
                for (final Team t
                        : TeamsModule.getMinigameModule(winner.getMinigame()).getTeams()) {
                    if (t != winner.getTeam()) {
                        l.addAll(t.getPlayers());
                    }
                }
            } else {
                w = new ArrayList<>(1);
                l = new ArrayList<>(winner.getMinigame().getPlayers().size());
                w.add(winner);
                l.addAll(winner.getMinigame().getPlayers());
                l.remove(winner);
            }
            Minigames.getPlugin().getPlayerManager().endMinigame(winner.getMinigame(), w, l);
        } else {
            Minigames.getPlugin().getPlayerManager().endMinigame(winner);
        }
    }
}
