package au.com.mineauz.minigames.config;

import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.langkeys.LangKey;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.MenuItemInteger;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class IntegerFlag extends AFlag<Integer> {

    public IntegerFlag(Integer value, String name) {
        setFlag(value);
        setDefaultFlag(value);
        setName(name);
    }

    @Override
    public void saveValue(@NotNull FileConfiguration config, @NotNull String path) {
        config.set(path + "." + getName(), getFlag());
    }

    @Override
    public void loadValue(@NotNull FileConfiguration config, @NotNull String path) {
        setFlag(config.getInt(path + "." + getName()));
    }

    @Deprecated
    @Override
    public MenuItemInteger getMenuItem(@Nullable Material displayMat, @Nullable Component name) {
        return getMenuItem(displayMat, name, 0, null);
    }

    public MenuItemInteger getMenuItem(@Nullable Material displayMat, @Nullable Component name,
                                       @Nullable Integer min, @Nullable Integer max) {
        return getMenuItem(displayMat, name, null, min, max);
    }

    public MenuItemInteger getMenuItem(@Nullable Material displayMat, @NotNull LangKey langKey,
                                       @Nullable Integer min, @Nullable Integer max) {
        return new MenuItemInteger(displayMat, langKey, null, new Callback<>() {

            @Override
            public Integer getValue() {
                return getFlag();
            }

            @Override
            public void setValue(Integer value) {
                setFlag(value);
            }

        }, min, max);
    }

    @Deprecated
    @Override
    public MenuItemInteger getMenuItem(@Nullable Material displayMat, @Nullable Component name,
                                       @Nullable List<@NotNull Component> description) {
        return getMenuItem(displayMat, name, description, 0, null);
    }

    public MenuItemInteger getMenuItem(@Nullable Material displayMat, @NotNull LangKey nameLangKey,
                                       @NotNull LangKey descriptionLangkey, @Nullable Integer min, @Nullable Integer max) {
        return new MenuItemInteger(displayMat, nameLangKey, MinigameMessageManager.getMgMessageList(descriptionLangkey), new Callback<>() {

            @Override
            public Integer getValue() {
                return getFlag();
            }

            @Override
            public void setValue(Integer value) {
                setFlag(value);
            }

        }, min, max);
    }

    public MenuItemInteger getMenuItem(@Nullable Material displayMat, @Nullable Component name,
                                       @Nullable List<@NotNull Component> description, @Nullable Integer min, @Nullable Integer max) {
        return new MenuItemInteger(displayMat, name, description, new Callback<>() {

            @Override
            public Integer getValue() {
                return getFlag();
            }

            @Override
            public void setValue(Integer value) {
                setFlag(value);
            }

        }, min, max);
    }
}
