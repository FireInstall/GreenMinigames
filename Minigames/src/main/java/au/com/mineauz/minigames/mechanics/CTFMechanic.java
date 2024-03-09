package au.com.mineauz.minigames.mechanics;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.events.DropFlagEvent;
import au.com.mineauz.minigames.events.FlagCaptureEvent;
import au.com.mineauz.minigames.events.TakeCTFFlagEvent;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.managers.language.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgMiscLangKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigames.minigame.modules.CTFModule;
import au.com.mineauz.minigames.minigame.modules.MgModules;
import au.com.mineauz.minigames.minigame.modules.MinigameModule;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigames.objects.CTFFlag;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.signs.CTFFlagSign;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class CTFMechanic extends GameMechanicBase {

    protected CTFMechanic() {
    }

    /**
     * Sending a ctf relevant message
     *
     * @param minigame The minigame in which this message shall be sent
     * @param message  The message
     */
    private static void sendCTFMessage(final @NotNull Minigame minigame, final @NotNull Component message) {
        if (!minigame.getShowCTFBroadcasts()) {
            return;
        }
        MinigameMessageManager.sendBroadcastMessageUnchecked(minigame, message, MinigameMessageType.INFO, null);
    }

    @Override
    public String getMechanicName() {
        return "ctf";
    }

    @Override
    public EnumSet<MinigameType> validTypes() {
        return EnumSet.of(MinigameType.MULTIPLAYER);
    }

    @Override
    public boolean checkCanStart(@NotNull Minigame minigame, @Nullable MinigamePlayer caller) {
        return true;
    }

    @Override
    public MinigameModule displaySettings(@NotNull Minigame minigame) {
        return minigame.getModule(MgModules.CAPTURE_THE_FLAG.getName());
    }

    @Override
    public void startMinigame(@NotNull Minigame minigame, @Nullable MinigamePlayer caller) {
    }

    @Override
    public void stopMinigame(@NotNull Minigame minigame) {
    }

    @Override
    public void onJoinMinigame(@NotNull Minigame minigame, @NotNull MinigamePlayer player) {
    }

    @Override
    public void quitMinigame(@NotNull Minigame minigame, @NotNull MinigamePlayer mgPlayer, boolean forced) {
        CTFFlag carriedFlag = minigame.getCarriedFlag(mgPlayer);
        if (carriedFlag != null) {
            carriedFlag.stopCarrierParticleEffect();
            carriedFlag.respawnFlag();
            minigame.removeFlagCarrier(mgPlayer);
        }
        if (minigame.getPlayers().size() == 1) {
            minigame.resetFlags();
        }
    }

    @Override
    public void endMinigame(@NotNull Minigame minigame, @NotNull List<@NotNull MinigamePlayer> winners, @NotNull List<@NotNull MinigamePlayer> losers) {
        for (MinigamePlayer mgPlayer : winners) {
            CTFFlag carriedFlag = minigame.getCarriedFlag(mgPlayer);
            if (carriedFlag != null) {
                carriedFlag.stopCarrierParticleEffect();
                carriedFlag.respawnFlag();
                minigame.removeFlagCarrier(mgPlayer);
            }
        }
        if (minigame.getPlayers().size() == 1) {
            minigame.resetFlags();
        }
    }

    @EventHandler
    private void takeFlag(@NotNull PlayerInteractEvent event) { //todo better system of getting type of sign --> should be a getter in sign base
        MinigamePlayer mgPlayer = pdata.getMinigamePlayer(event.getPlayer());
        if (mgPlayer.isInMinigame() && !mgPlayer.getPlayer().isDead() && mgPlayer.getMinigame().hasStarted()) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK &&
                    event.getClickedBlock() != null &&
                    event.getClickedBlock().getState() instanceof Sign sign &&
                    mgPlayer.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR) {
                SignSide signFrontSide = sign.getSide(Side.FRONT);
                PlainTextComponentSerializer plainTextSerializer = PlainTextComponentSerializer.plainText();

                Minigame mgm = mgPlayer.getMinigame();
                if (mgm.getMechanic() == GameMechanics.MgMechanics.CTF.getMechanic() && new CTFFlagSign().isType(signFrontSide.line(1))) { // I hate that java does have this static inheritance restriction
                    Team team = mgPlayer.getTeam();

                    String sloc = MinigameUtils.createLocationID(event.getClickedBlock().getLocation());
                    @Nullable TeamColor colorOnLine2 = TeamColor.matchColor(plainTextSerializer.serialize(signFrontSide.line(2)));
                    if (colorOnLine2 == team.getColor() &&
                            mgm.hasDroppedFlag(sloc) &&
                            !(sloc.equals(MinigameUtils.createLocationID(mgm.getDroppedFlag(sloc).getSpawnLocation())))) {
                        if (CTFModule.getMinigameModule(mgm).getBringFlagBackManual()) {
                            CTFFlag flag = mgm.getDroppedFlag(sloc);
                            flag.stopTimer();
                            mgm.removeDroppedFlag(sloc);
                            String newID = MinigameUtils.createLocationID(flag.getSpawnLocation());
                            mgm.addDroppedFlag(newID, flag);
                            flag.respawnFlag();

                            MinigameMessageManager.sendMinigameMessage(mgm, MinigameMessageManager.getMgMessage(MgMiscLangKey.MINIGAME_FLAG_RETURNEDTEAM,
                                    Placeholder.component(MinigamePlaceHolderKey.TEAM.getKey(), Component.text(team.getDisplayName(), team.getTextColor()))), MinigameMessageType.INFO);
                        }
                    } else if ((colorOnLine2 != team.getColor() &&
                            !signFrontSide.getLine(2).equalsIgnoreCase(ChatColor.GREEN + "Capture")) ||
                            signFrontSide.getLine(2).equalsIgnoreCase(ChatColor.GRAY + "Neutral")) {
                        if (mgm.getCarriedFlag(mgPlayer) == null) {
                            TakeCTFFlagEvent ev = null;
                            if (!mgm.hasDroppedFlag(sloc) &&
                                    (TeamsModule.getMinigameModule(mgm).hasTeam(colorOnLine2) ||
                                            signFrontSide.getLine(2).equalsIgnoreCase(ChatColor.GRAY + "Neutral"))) {
                                Team oTeam = TeamsModule.getMinigameModule(mgm).getTeam(colorOnLine2);
                                CTFFlag flag = new CTFFlag(sign, oTeam, mgm);
                                ev = new TakeCTFFlagEvent(mgm, mgPlayer, flag);
                                Bukkit.getPluginManager().callEvent(ev);
                                if (!ev.isCancelled()) {
                                    mgm.addFlagCarrier(mgPlayer, flag);
                                    flag.removeFlag();
                                }
                            } else if (mgm.hasDroppedFlag(sloc)) {
                                CTFFlag flag = mgm.getDroppedFlag(sloc);
                                ev = new TakeCTFFlagEvent(mgm, mgPlayer, flag);
                                Bukkit.getPluginManager().callEvent(ev);
                                if (!ev.isCancelled()) {
                                    mgm.addFlagCarrier(mgPlayer, flag);
                                    if (!flag.isAtHome()) {
                                        flag.stopTimer();
                                    }
                                    flag.removeFlag();
                                }
                            }

                            if (mgm.getCarriedFlag(mgPlayer) != null && !ev.isCancelled()) {
                                if (mgm.getCarriedFlag(mgPlayer).getTeam() != null) {
                                    Team flagTeam = mgm.getCarriedFlag(mgPlayer).getTeam();
                                    sendCTFMessage(mgm, MinigameMessageManager.getMgMessage(MgMiscLangKey.PLAYER_CTF_STOLE,
                                            Placeholder.unparsed(MinigamePlaceHolderKey.PLAYER.getKey(), mgPlayer.getName()),
                                            Placeholder.component(MinigamePlaceHolderKey.TEAM.getKey(), Component.text(flagTeam.getDisplayName(), flagTeam.getTextColor())))
                                    );
                                    mgm.getCarriedFlag(mgPlayer).startCarrierParticleEffect(mgPlayer.getPlayer());
                                } else {
                                    sendCTFMessage(mgm, MinigameMessageManager.getMgMessage(MgMiscLangKey.PLAYER_CTF_NEUTRAL_STOLE,
                                            Placeholder.unparsed(MinigamePlaceHolderKey.PLAYER.getKey(), mgPlayer.getName()))
                                    );
                                    mgm.getCarriedFlag(mgPlayer).startCarrierParticleEffect(mgPlayer.getPlayer());
                                }
                            }
                        }

                    } else if (team == TeamsModule.getMinigameModule(mgm).getTeam(colorOnLine2) && CTFModule.getMinigameModule(mgm).getUseFlagAsCapturePoint() ||
                            (team == TeamsModule.getMinigameModule(mgm).getTeam(TeamColor.matchColor(plainTextSerializer.serialize(signFrontSide.line(3)))) &&
                                    signFrontSide.getLine(2).equalsIgnoreCase(ChatColor.GREEN + "Capture")) ||
                            (signFrontSide.getLine(2).equalsIgnoreCase(ChatColor.GREEN + "Capture") &&
                                    signFrontSide.getLine(3).equalsIgnoreCase(ChatColor.GRAY + "Neutral"))) {

                        String clickID = MinigameUtils.createLocationID(event.getClickedBlock().getLocation());

                        if (mgm.getCarriedFlag(mgPlayer) != null && (!mgm.hasDroppedFlag(clickID) || mgm.getDroppedFlag(clickID).isAtHome())) {
                            CTFFlag flag = mgm.getCarriedFlag(mgPlayer);
                            FlagCaptureEvent ev = new FlagCaptureEvent(mgm, mgPlayer, flag);
                            Bukkit.getPluginManager().callEvent(ev);
                            if (!ev.isCancelled()) {
                                flag.respawnFlag();
                                String id = MinigameUtils.createLocationID(flag.getSpawnLocation());
                                mgm.addDroppedFlag(id, flag);
                                mgm.removeFlagCarrier(mgPlayer);

                                boolean end = false;

                                if (mgm.isTeamGame()) {
                                    mgPlayer.getTeam().addScore();
                                    if (mgm.getMaxScore() != 0 && mgPlayer.getTeam().getScore() >= mgm.getMaxScorePerPlayer()) {
                                        end = true;
                                    }

                                    if (!end) {
                                        sendCTFMessage(mgm, MinigameMessageManager.getMgMessage(MgMiscLangKey.PLAYER_CTF_CAPTURE,
                                                Placeholder.unparsed(MinigamePlaceHolderKey.PLAYER.getKey(), mgPlayer.getName()),
                                                Placeholder.component(MinigamePlaceHolderKey.TEAM.getKey(), Component.text(mgPlayer.getTeam().getDisplayName(), mgPlayer.getTeam().getTextColor()))
                                        ));
                                    }
                                    flag.stopCarrierParticleEffect();
                                    mgPlayer.addScore();
                                    mgm.setScore(mgPlayer, mgPlayer.getScore());

                                    if (end) {
                                        sendCTFMessage(mgm, MinigameMessageManager.getMgMessage(MgMiscLangKey.PLAYER_CTF_CAPTUREFINAL,
                                                Placeholder.unparsed(MinigamePlaceHolderKey.PLAYER.getKey(), mgPlayer.getName()),
                                                Placeholder.component(MinigamePlaceHolderKey.TEAM.getKey(), Component.text(mgPlayer.getTeam().getDisplayName(), mgPlayer.getTeam().getTextColor())))
                                        );
                                        List<MinigamePlayer> w = new ArrayList<>(mgPlayer.getTeam().getPlayers());
                                        List<MinigamePlayer> l = new ArrayList<>(mgm.getPlayers().size() - mgPlayer.getTeam().getPlayers().size());
                                        for (Team t : TeamsModule.getMinigameModule(mgm).getTeams()) {
                                            if (t != mgPlayer.getTeam())
                                                l.addAll(t.getPlayers());
                                        }
                                        plugin.getPlayerManager().endMinigame(mgm, w, l);
                                        mgm.resetFlags();
                                    }
                                } else {
                                    mgPlayer.addScore();
                                    mgm.setScore(mgPlayer, mgPlayer.getScore());
                                    if (mgm.getMaxScore() != 0 && mgPlayer.getScore() >= mgm.getMaxScorePerPlayer()) {
                                        end = true;
                                    }

                                    sendCTFMessage(mgm, MinigameMessageManager.getMgMessage(MgMiscLangKey.PLAYER_CTF_NEUTRAL_CAPTURE,
                                            Placeholder.unparsed(MinigamePlaceHolderKey.PLAYER.getKey(), mgPlayer.getName())));
                                    flag.stopCarrierParticleEffect();

                                    if (end) {
                                        sendCTFMessage(mgm, MinigameMessageManager.getMgMessage(MgMiscLangKey.PLAYER_CTF_NEUTRAL_CAPTUREFINAL,
                                                Placeholder.unparsed(MinigamePlaceHolderKey.PLAYER.getKey(), mgPlayer.getName())));

                                        pdata.endMinigame(mgPlayer);
                                        mgm.resetFlags();
                                    }
                                }
                            }
                        } else if (mgm.getCarriedFlag(mgPlayer) == null && mgm.hasDroppedFlag(clickID) && !mgm.getDroppedFlag(clickID).isAtHome()) {
                            CTFFlag flag = mgm.getDroppedFlag(sloc);
                            if (mgm.hasDroppedFlag(sloc)) {
                                mgm.removeDroppedFlag(sloc);
                                String newID = MinigameUtils.createLocationID(flag.getSpawnLocation());
                                mgm.addDroppedFlag(newID, flag);
                            }
                            flag.respawnFlag();

                            sendCTFMessage(mgm, MinigameMessageManager.getMgMessage(MgMiscLangKey.PLAYER_CTF_RETURNED,
                                    Placeholder.unparsed(MinigamePlaceHolderKey.PLAYER.getKey(), mgPlayer.getName()),
                                    Placeholder.component(MinigamePlaceHolderKey.TEAM.getKey(), Component.text(mgPlayer.getTeam().getDisplayName(), mgPlayer.getTeam().getTextColor())))
                            );
                        } else if (mgm.getCarriedFlag(mgPlayer) != null && mgm.hasDroppedFlag(clickID) && !mgm.getDroppedFlag(clickID).isAtHome()) {
                            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.LOSS, MgMiscLangKey.PLAYER_CTF_RETURNFAIL);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    private void dropFlag(@NotNull PlayerDeathEvent event) {
        MinigamePlayer mgPlayer = pdata.getMinigamePlayer(event.getEntity());
        if (mgPlayer.isInMinigame()) {
            Minigame mgm = mgPlayer.getMinigame();
            if (mgm.isFlagCarrier(mgPlayer)) {
                CTFFlag flag = mgm.getCarriedFlag(mgPlayer);
                Location loc = flag.spawnFlag(mgPlayer.getPlayer().getLocation());
                if (loc != null) {
                    DropFlagEvent ev = new DropFlagEvent(mgm, flag, mgPlayer);
                    Bukkit.getPluginManager().callEvent(ev);
                    if (!ev.isCancelled()) {
                        String id = MinigameUtils.createLocationID(loc);
                        Team team = mgm.getCarriedFlag(mgPlayer).getTeam();
                        mgm.addDroppedFlag(id, flag);
                        mgm.removeFlagCarrier(mgPlayer);

                        if (team != null) {
                            sendCTFMessage(mgm, MinigameMessageManager.getMgMessage(MgMiscLangKey.PLAYER_CTF_DROPPED,
                                    Placeholder.unparsed(MinigamePlaceHolderKey.PLAYER.getKey(), mgPlayer.getName()),
                                    Placeholder.component(MinigamePlaceHolderKey.TEAM.getKey(), Component.text(team.getDisplayName(), team.getTextColor())))
                            );
                        } else {
                            sendCTFMessage(mgm, MinigameMessageManager.getMgMessage(MgMiscLangKey.PLAYER_CTF_NEUTRAL_DROPPED,
                                    Placeholder.unparsed(MinigamePlaceHolderKey.PLAYER.getKey(), mgPlayer.getName()))
                            );
                        }
                        flag.stopCarrierParticleEffect();
                        flag.startReturnTimer();
                    }
                } else {
                    flag.respawnFlag();
                    mgm.removeFlagCarrier(mgPlayer);
                    flag.stopCarrierParticleEffect();
                }
            }
        }
    }

    @EventHandler
    private void playerAutoBalance(@NotNull PlayerDeathEvent event) {
        MinigamePlayer mgPlayer = pdata.getMinigamePlayer(event.getEntity());
        if (mgPlayer.isInMinigame() && mgPlayer.getMinigame().getType() == MinigameType.MULTIPLAYER && mgPlayer.getMinigame().isTeamGame()) {
            Minigame mgm = mgPlayer.getMinigame();
            if (mgm.getMechanicName().equalsIgnoreCase(MgModules.CAPTURE_THE_FLAG.getName())) {
                autoBalanceOnDeath(mgPlayer, mgm);
            }
        }
    }
}
