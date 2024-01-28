package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.minigame.Minigame;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MenuItemSaveMinigame extends MenuItem {
    private final @NotNull Minigame mgm;

    public MenuItemSaveMinigame(Component name, Material displayItem, @NotNull Minigame minigame) {
        super(name, displayItem);
        mgm = minigame;
    }

    public MenuItemSaveMinigame(Component name, List<@NotNull Component> description, Material displayItem, @NotNull Minigame minigame) {
        super(name, description, displayItem);
        mgm = minigame;
    }

    @Override
    public ItemStack onClick() {
        mgm.saveMinigame();
        getContainer().getViewer().sendMessage("Saved the '" + mgm.getName(false) + "' Minigame.", MinigameMessageType.INFO);
        return getDisplayItem();
    }

}
