package au.com.mineauz.minigamesregions.menu;

import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemBack;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.language.RegionLangKey;
import au.com.mineauz.minigamesregions.language.RegionMessageManager;
import au.com.mineauz.minigamesregions.triggers.Trigger;
import au.com.mineauz.minigamesregions.triggers.TriggerRegistry;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MenuItemRegionExecutorAdd extends MenuItem { // todo merge with MenuItemNodeExecutorAdd
    private final @NotNull Region region;

    public MenuItemRegionExecutorAdd(@Nullable Material displayMat, @Nullable RegionLangKey langKey, @NotNull Region region) {
        super(displayMat, RegionMessageManager.getMessage(langKey));
        this.region = region;
    }

    public MenuItemRegionExecutorAdd(@Nullable Material displayMat, @Nullable Component name, @NotNull Region region) {
        super(displayMat, name);
        this.region = region;
    }

    public MenuItemRegionExecutorAdd(@Nullable Material displayMat, @Nullable Component name,
                                     @Nullable List<@NotNull Component> description, @NotNull Region region) {
        super(displayMat, name, description);
        this.region = region;
    }

    @Override
    public ItemStack onClick() {
        Menu menu = new Menu(6, RegionMessageManager.getMessage(RegionLangKey.MENU_REGIONEXECUTOR_ADD_TRIGGER_NAME), getContainer().getViewer());

        for (Trigger trig : TriggerRegistry.getAllRegionTriggers()) {
            menu.addItem(new MenuItemTrigger(trig, region, getContainer()));
        }

        menu.addItem(new MenuItemBack(getContainer()), menu.getSize() - 9);
        menu.displayMenu(getContainer().getViewer());

        return null;
    }
}