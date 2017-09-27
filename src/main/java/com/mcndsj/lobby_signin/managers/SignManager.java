package com.mcndsj.lobby_signin.managers;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.mcndsj.lobby_signin.LobbySignIn;
import com.mcndsj.lobby_signin.listeners.SignInventory;
import com.mcndsj.lobby_signin.animations.Animation;
import com.mcndsj.lobby_signin.animations.ItemAndExtension;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matthew on 3/07/2016.
 */
public class SignManager {


    private static SignManager manager;
    private Animation animation = null;
    private List<Slime> hdOn = new ArrayList<>();

    public SignManager(){
        this.animation = new ItemAndExtension();
        new BukkitRunnable() {
            @Override
            public void run() {
                System.out.println("Try to find Slimes");
                for(Entity e : Bukkit.getWorld("lobby").getEntities()){
                    if(e.hasMetadata("NPC") && e.getName().contains("史姆莱")){
                        System.out.println("Found Slime, add HD");
                        Hologram hologram =  HologramsAPI.createHologram(LobbySignIn.get(), e.getLocation().clone().add(0,2.7,0));
                        hologram.appendTextLine(ChatColor.GREEN + ChatColor.BOLD.toString() + ">一只史姆莱<");
                        hdOn.add((Slime) e);
                    }
                }
            }
        }.runTaskLater(LobbySignIn.get(),20 * 30);
    }

    public static SignManager get(){
        if(manager == null){
            manager = new SignManager();
        }

        return manager;
    }

    public void playEffect(Slime s){

        animation.play(s);
    }


    public void giveChest(String who,int amount){
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "uc give key "+amount+" " + who);
    }

    public void openInventory(Player gp, Slime click){
        SignInventory inv = new SignInventory(gp.getName(),click);
        inv.disPlay();
        if(!hdOn.contains(click)){
            Hologram hologram =  HologramsAPI.createHologram(LobbySignIn.get(), click.getLocation().clone().add(0,2.7,0));
            hologram.appendTextLine(ChatColor.GREEN + ChatColor.BOLD.toString() + ">一只史姆莱<");
            hdOn.add(click);
        }
    }


}
