package au.com.mineauz.minigames.managers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.config.MinigameSave;
import au.com.mineauz.minigames.objects.ResourcePack;
import com.google.common.collect.Lists;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;


/**
 * Created for the AddstarMC Project. Created by Narimm on 12/02/2019.
 */
public class  ResourcePackManager {

    private boolean enabled = true;

    public static Path getResourceDir() {
        return resourceDir;
    }

    final static Path resourceDir = Paths.get(Minigames.getPlugin().getDataFolder().toString(),"resources");

    private Map<String, ResourcePack> resources = new HashMap<>();

    public ResourcePackManager() {
        if(!Files.notExists(resourceDir))
            try {
                Path path = Files.createDirectories(resourceDir);
                if(Files.notExists(path)){
                    Minigames.log().severe("Cannot create a resource directory to house resources " +
                            "- they will be unavailable");
                    enabled = false;
                } else {
                    if(Files.exists(path))
                    enabled = true;
                    else {
                        enabled = false;
                        Minigames.log().severe("Cannot create a resource directory to house resources " +
                                "- they will be unavailable.");
                    }
                }

            }catch (IOException e){
                Minigames.log().severe("Cannot create a resource directory to house resources " +
                        "- they will be unavailable: Message" + e.getMessage() );
                enabled = false;
            }
    }

    private boolean loadEmptyPack() {
        try {
            URL u = new URL("https://github.com/AddstarMC/Minigames/raw/resourcepack/Minigames/src/main/resources/resourcepack/emptyResourcePack.zip");
            ResourcePack empty = new ResourcePack("empty",u);
            if(empty.isValid()){
               addResourcePack(empty);
            }
            return true;
        }catch (MalformedURLException e){
            return false;
        }
    }
    
    public ResourcePack getResourcePack(String name){
        if(!enabled)return null;
        ResourcePack pack = resources.get(name);
        if(pack != null && pack.isValid()) return pack;
        else return null;
    }
    
    public ResourcePack addResourcePack(ResourcePack pack){
        if(!enabled)return null;
        return resources.put(pack.getName(),pack);
    };
    
    public boolean initialize(FileConfiguration config){
        boolean emptyPresent = false;
        final List<ResourcePack> resources = new ArrayList<>();
        final Object objects = config.get("resources");
        if(objects instanceof List){
            final List obj = (List) objects;
            for(final Object object : obj){
                if(object instanceof ResourcePack){
                    resources.add((ResourcePack) object);
                }
            }
        }
        for(final ResourcePack pack:resources){
            if (pack.getName().equals("empty")) {
                emptyPresent = true;
            }
            addResourcePack(pack);
        }
        if(!emptyPresent){
            if(!loadEmptyPack()){
                Minigames.log().warning("Minigames Resource Manager could not create the empty reset pack");
                enabled = false;
                return false;
            }
            enabled = true;
        }
        return true;
    }
    
    public void saveResources(MinigameSave mSave){
        List<ResourcePack> resourceList = new ArrayList<>(resources.values());
        mSave.getConfig().set("resources",resourceList);
        mSave.saveConfig();
    }


}