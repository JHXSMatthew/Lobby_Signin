package com.mcndsj.lobby_signin;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.mcndsj.lobby_signin.listeners.PlayerListener;
import com.mcndsj.lobby_signin.managers.SignManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Slime;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Matthew on 3/07/2016.
 */
public class LobbySignIn extends JavaPlugin{

    private static LobbySignIn instance;

    public void onEnable(){
        instance = this;
        getServer().getPluginManager().registerEvents(new PlayerListener(),this);
        Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays");
        SignManager.get();
    }

    public static LobbySignIn get(){
        return instance;
    }
}
