package au.com.mineauz.minigames.minigame.modules;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.config.Flag;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.ResourcePack;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ResourcePackModule extends MinigameModule { //todo rework to work with multiple ressource packs
    private final BooleanFlag enabled = new BooleanFlag(false, "resourcePackEnabled");
    private final StringFlag resourcePackName = new StringFlag("", "resourcePackName");
    private final BooleanFlag forced = new BooleanFlag(false, "forceResourcePack");

    public ResourcePackModule(@NotNull Minigame mgm, @NotNull String name) {
        super(mgm, name);
    }

    public static @Nullable ResourcePackModule getMinigameModule(@NotNull Minigame mgm) {
        return ((ResourcePackModule) mgm.getModule(MgModules.RESOURCEPACK.getName()));
    }

    public boolean isEnabled() {
        return enabled.getFlag();
    }

    public void setEnabled(Boolean bool) {
        enabled.setFlag(bool);
    }

    public boolean isForced() {
        return forced.getFlag();
    }

    public void setResourcePackname(String name) {
        resourcePackName.setFlag(name);
    }

    public String getResourcePackName() {
        return resourcePackName.getFlag();
    }

    @Override
    public Map<String, Flag<?>> getFlags() {
        Map<String, Flag<?>> map = new HashMap<>();
        addConfigFlag(enabled, map);
        addConfigFlag(resourcePackName, map);
        return map;
    }

    private void addConfigFlag(Flag<?> flag, Map<String, Flag<?>> flags) {
        flags.put(flag.getName(), flag);
    }

    @Override
    public boolean useSeparateConfig() {
        return false;
    }

    @Override
    public void save(FileConfiguration config) {
    }

    @Override
    public void load(FileConfiguration config) {

    }

    @Override
    public void addEditMenuOptions(Menu previousMenu) {
        Menu teamsMenu = new Menu(3, "Teams", previousMenu.getViewer());
        teamsMenu.setPreviousPage(previousMenu);
        teamsMenu.addItem(enabled.getMenuItem("Enable Resource Pack", Material.MAP));
        MenuItem item = new MenuItemString(Material.PAPER, "Resource Pack Name", new Callback<>() {
            @Override
            public String getValue() {
                return resourcePackName.getFlag();
            }

            @Override
            public void setValue(String value) {
                resourcePackName.setFlag(value);
            }
        }) {
            @Override
            public void checkValidEntry(String entry) {
                if (entry.isEmpty()) {
                    super.checkValidEntry(entry);
                    return;
                }
                ResourcePack pack = Minigames.getPlugin().getResourceManager().getResourcePack(entry);
                if (pack == null) {
                    getContainer().cancelReopenTimer();
                    getContainer().displayMenu(getContainer().getViewer());
                    MinigameMessageManager.sendMgMessage(getContainer().getViewer(), MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_RESSOURCEPACK_NORESSOURCEPACK,
                            Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), entry));
                } else {
                    super.checkValidEntry(entry);
                }
            }
        };
        teamsMenu.addItem(item);
        teamsMenu.addItem(forced.getMenuItem(Material.SKELETON_SKULL, "Force Resource Pack"));
        MenuItemPage p = new MenuItemPage(Material.MAP, "Resource Pack Options", teamsMenu);
        teamsMenu.addItem(new MenuItemBack(previousMenu), teamsMenu.getSize() - 9);
        previousMenu.addItem(p);
    }

    @Override
    public boolean displayMechanicSettings(Menu previous) {
        return false;
    }
}
