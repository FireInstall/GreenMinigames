package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.langkeys.MgMenuLangKey;
import au.com.mineauz.minigames.minigame.reward.RewardGroup;
import au.com.mineauz.minigames.minigame.reward.RewardType;
import au.com.mineauz.minigames.minigame.reward.RewardTypes;
import au.com.mineauz.minigames.minigame.reward.Rewards;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MenuItemRewardAdd extends MenuItem {
    private Rewards rewards = null;
    private RewardGroup group = null;

    public MenuItemRewardAdd(@Nullable Material displayMat, @Nullable Component name, @NotNull Rewards rewards) {
        super(displayMat, name);
        this.rewards = rewards;
    }

    public MenuItemRewardAdd(@Nullable Material displayMat, @Nullable Component name,
                             @Nullable List<@NotNull Component> description, @NotNull Rewards rewards) {
        super(displayMat, name, description);
        this.rewards = rewards;
    }

    public MenuItemRewardAdd(@Nullable Material displayMat, @Nullable Component name, @NotNull RewardGroup group) {
        super(displayMat, name);
        this.group = group;
    }

    public MenuItemRewardAdd(@Nullable Material displayMat, @Nullable Component name,
                             @Nullable List<@NotNull Component> description, @NotNull RewardGroup group) {
        super(displayMat, name, description);
        this.group = group;
    }

    @Override
    public ItemStack onClick() {
        Menu m = new Menu(6, MinigameMessageManager.getMgMessage(MgMenuLangKey.MENU_REWARD_SELECTTYPE_NAME), getContainer().getViewer());
        final Menu orig = getContainer();
        for (String type : RewardTypes.getAllRewardTypeNames()) {
            final MenuItemCustom custom = new MenuItemCustom(Material.STONE, "TYPE");
            final RewardType rewType = RewardTypes.getRewardType(type, rewards);
            if (rewType.isUsable()) {
                ItemMeta meta = custom.getDisplayItem().getItemMeta();
                meta.setDisplayName(ChatColor.RESET + type);
                custom.getDisplayItem().setItemMeta(meta);
                custom.setDisplayItem(rewType.getMenuItem().getDisplayItem());
                custom.setClick(() -> {
                    if (rewards != null) {
                        rewards.addReward(rewType);
                    } else {
                        group.addItem(rewType);
                    }
                    orig.displayMenu(orig.getViewer());
                    orig.addItem(rewType.getMenuItem());
                    return null;
                });
                m.addItem(custom);
            }
        }
        m.addItem(new MenuItemBack(orig), m.getSize() - 9);
        m.displayMenu(m.getViewer());
        return null;
    }
}
