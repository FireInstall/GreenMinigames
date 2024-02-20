package au.com.mineauz.minigames.config;

import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.MenuItemDecimal;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FloatFlag extends AFlag<Float> {

    public FloatFlag(Float value, String name) {
        setFlag(value);
        setDefaultFlag(value);
        setName(name);
    }

    @Override
    public void saveValue(@NotNull FileConfiguration config, @NotNull String path) {
        config.set(path + "." + getName(), getFlag().doubleValue());
    }

    @Override
    public void loadValue(@NotNull FileConfiguration config, @NotNull String path) {
        setFlag(((Double) config.getDouble(path + "." + getName())).floatValue());
    }

    @Override
    public MenuItemDecimal getMenuItem(@Nullable Material displayMat, @Nullable Component name,
                                       @Nullable List<@NotNull Component> description) {
        return this.getMenuItem(displayMat, name, description, 1d, 1d, 0d, Double.POSITIVE_INFINITY);
    }

    public MenuItemDecimal getMenuItem(@Nullable Material displayMat, @Nullable Component name,
                                       double lowerinc, double upperinc, @Nullable Double min, @Nullable Double max) {
        return this.getMenuItem(displayMat, name, null, lowerinc, upperinc, min, max);
    }

    public MenuItemDecimal getMenuItem(@Nullable Material displayMat, @Nullable Component name,
                                       @Nullable List<@NotNull Component> description,
                                       double lowerinc, double upperinc, @Nullable Double min, @Nullable Double max) {
        return new MenuItemDecimal(displayMat, name, description, new Callback<>() {

            @Override
            public Double getValue() {
                return getFlag().doubleValue();
            }

            @Override
            public void setValue(Double value) {
                setFlag(value.floatValue());
            }

        }, lowerinc, upperinc, min, max);
    }
}
