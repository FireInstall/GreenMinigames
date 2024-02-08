package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.LangKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgMenuLangKey;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MenuItemString extends MenuItem {
    protected final static String DESCRIPTION_TOKEN = "String_description";
    protected final Callback<String> stringCallback;
    private boolean allowNull = false;

    public MenuItemString(@Nullable Material displayMat, @NotNull LangKey langKey, @NotNull Callback<String> stringCallback) {
        super(displayMat, langKey);
        this.stringCallback = stringCallback;
        updateDescription();
    }

    public MenuItemString(@Nullable Material displayMat, @Nullable Component name, @NotNull Callback<String> stringCallback) {
        super(displayMat, name);
        this.stringCallback = stringCallback;
        updateDescription();
    }

    public MenuItemString(@Nullable Material displayMat, @Nullable Component name,
                          @Nullable List<@NotNull Component> description, @NotNull Callback<String> stringCallback) {
        super(displayMat, name, description);
        this.stringCallback = stringCallback;
        updateDescription();
    }

    public void setAllowNull(boolean allow) {
        allowNull = allow;
    }

    public void updateDescription() {
        String setting = stringCallback.getValue();
        if (setting == null) {
            setDescriptionPartAtEnd(DESCRIPTION_TOKEN, List.of(
                    MinigameMessageManager.getMgMessage(MgMenuLangKey.MENU_ELEMENTNOTSET).color(NamedTextColor.GRAY)));
        } else if (setting.length() > 20) {
            setting = setting.substring(0, 17) + "...";
            setDescriptionPartAtEnd(DESCRIPTION_TOKEN, List.of(Component.text(setting, NamedTextColor.GREEN)));
        }
    }

    @Override
    public ItemStack onDoubleClick() {
        MinigamePlayer mgPlayer = getContainer().getViewer();
        mgPlayer.setNoClose(true);
        mgPlayer.getPlayer().closeInventory();
        MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MgMenuLangKey.MENU_STRING_ENTERCHAT,
                Placeholder.component(MinigamePlaceHolderKey.TYPE.getKey(), getName()));
        if (allowNull) {
            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MgMenuLangKey.MENU_STRING_ALLOWNULL,
                    Placeholder.component(MinigamePlaceHolderKey.TYPE.getKey(), getName()));
        }
        mgPlayer.setManualEntry(this);
        getContainer().startReopenTimer(20);

        return null;
    }

    @Override
    public void checkValidEntry(String entry) {
        if (entry.equals("null") && allowNull) {
            stringCallback.setValue(null);
        } else {
            stringCallback.setValue(entry);
        }

        updateDescription();
        getContainer().cancelReopenTimer();
        getContainer().displayMenu(getContainer().getViewer());
    }
}
