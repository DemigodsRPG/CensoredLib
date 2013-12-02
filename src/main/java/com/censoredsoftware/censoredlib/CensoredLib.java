package com.censoredsoftware.censoredlib;

import com.censoredsoftware.censoredlib.helper.MojangIdGrabber;
import org.bukkit.plugin.java.JavaPlugin;

public class CensoredLib extends JavaPlugin
{
    public static String SAVE_PATH;

    /**
     * The Bukkit enable method.
     */
    @Override
    public void onEnable()
    {
        SAVE_PATH = getDataFolder() + "/data/";

        // Load Mojang Id Grabber
        MojangIdGrabber.load(this);
    }

    /**
     * The Bukkit disable method.
     */
    @Override
    public void onDisable()
    {}
}
