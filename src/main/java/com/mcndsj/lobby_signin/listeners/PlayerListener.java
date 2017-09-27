package com.mcndsj.lobby_signin.listeners;

import com.mcndsj.lobby_signin.managers.SignManager;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;

/**
 * Created by Matthew on 3/07/2016.
 */
public class PlayerListener implements Listener{

    private HashMap<String,Long> coolDown = new HashMap<String,Long>();
    @EventHandler
    public void onNPCClick(PlayerInteractEntityEvent evt){
        if(evt.getRightClicked().getName().contains("史姆莱") && evt.getRightClicked().hasMetadata("NPC")){
            if(coolDown.containsKey(evt.getPlayer().getName())){
                long l = coolDown.get(evt.getPlayer().getName());
                if(l > System.currentTimeMillis())
                    return;

            }
            coolDown.put(evt.getPlayer().getName(),System.currentTimeMillis() + 1000);
            SignManager.get().openInventory(evt.getPlayer(), (Slime) evt.getRightClicked());

        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent evt){
        if (coolDown.containsKey(evt.getPlayer().getName())) {
            coolDown.remove(evt.getPlayer().getName());
        }
    }
}
