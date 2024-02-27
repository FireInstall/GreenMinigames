package au.com.mineauz.minigames.tool;

import au.com.mineauz.minigames.managers.language.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.langkeys.MgMenuLangKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgMiscLangKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EndLocationMode implements ToolMode {

    @Override
    public String getName() {
        return "END";
    }

    @Override
    public Component getDisplayName() {
        return MinigameMessageManager.getMgMessage(MgMenuLangKey.MENU_TOOL_LOCATION_END_NAME);
    }

    @Override
    public List<Component> getDescription() {
        return MinigameMessageManager.getMgMessageList(MgMenuLangKey.MENU_TOOL_LOCATION_END_DESCRIPTION);
    }

    @Override
    public Material getIcon() {
        return Material.GOLD_BLOCK;
    }

    @Override
    public void onLeftClick(@NotNull MinigamePlayer mgPlayer, @NotNull Minigame minigame,
                            @Nullable Team team, @NotNull PlayerInteractEvent event) {

    }

    @Override
    public void onRightClick(@NotNull MinigamePlayer mgPlayer, @NotNull Minigame minigame,
                             @Nullable Team team, @NotNull PlayerInteractEvent event) {
        minigame.setEndLocation(mgPlayer.getLocation());
        MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MgMiscLangKey.TOOL_SET_ENDLOCATION);
    }

    @Override
    public void select(@NotNull MinigamePlayer mgPlayer, @NotNull Minigame minigame, @Nullable Team team) {
        if (minigame.getEndLocation() != null) {
            mgPlayer.getPlayer().sendBlockChange(minigame.getEndLocation(),
                    Material.SKELETON_SKULL.createBlockData());
            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MgMiscLangKey.TOOL_SELECTED_ENDLOCATION);
        } else {
            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MgMiscLangKey.TOOL_ERROR_NOENDLOCATION);
        }
    }

    @Override
    public void deselect(@NotNull MinigamePlayer mgPlayer, @NotNull Minigame minigame, @Nullable Team team) {
        if (minigame.getEndLocation() != null) {
            mgPlayer.getPlayer().sendBlockChange(minigame.getEndLocation(),
                    minigame.getEndLocation().getBlock().getBlockData());
            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MgMiscLangKey.TOOL_DESELECTED_ENDLOCATION);
        } else {
            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MgMiscLangKey.TOOL_ERROR_NOENDLOCATION);
        }
    }

    @Override
    public void onSetMode(@NotNull MinigamePlayer player, @NotNull MinigameTool tool) {
    }

    @Override
    public void onUnsetMode(@NotNull MinigamePlayer mgPlayer, MinigameTool tool) {
    }
}
