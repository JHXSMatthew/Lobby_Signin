package com.mcndsj.lobby_signin.animations;

import com.mcndsj.lobby_signin.LobbySignIn;
import com.mcndsj.lobby_signin.utils.ItemFactory;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Slime;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Created by Matthew on 3/07/2016.
 */
public class ItemAndExtension implements  Animation{

    private List<Slime> isChanging  = new ArrayList<Slime>();

    @Override
    public void play(Slime signNpcLocation) {


            if(isChanging.contains(signNpcLocation)){
                return ;
            }

            new BukkitRunnable(){

                @Override
                public void run() {
                    isChanging.add(signNpcLocation);
                    Slime s = (Slime)signNpcLocation ;
                    Random r = new Random();

                    s.playEffect(EntityEffect.HURT);
                    s.getWorld().playSound(s.getLocation(), Sound.FIREWORK_LAUNCH, 1, 1);
                    ArrayList<Item> items = new ArrayList<>();


                    new BukkitRunnable(){

                        boolean smalling = false;
                        Location last = s.getLocation();
                        int cacheSize =s.getSize();
                        @Override
                        public void run() {
                            if(cacheSize  == 1){
                                if(smalling){
                                    cancel();
                                    isChanging.remove(signNpcLocation);
                                }
                                last = s.getLocation();
                                cacheSize ++;
                                s.setSize(cacheSize);
                                s.teleport(last);
                            }else {
                                if(smalling){
                                    last = s.getLocation();
                                    cacheSize --;
                                    s.setSize(cacheSize);
                                    s.teleport(last);
                                }else if(cacheSize == 5){
                                    smalling = true;
                                }else {
                                    last = s.getLocation();
                                    cacheSize ++;
                                    s.setSize(cacheSize);
                                    s.teleport(last);
                                }


                            }

                        }

                    }.runTaskTimer(LobbySignIn.get(), 0, 2);

                    Location itemSpawn = s.getEyeLocation();
                    itemSpawn.setY(itemSpawn.getY() + 2 );
                    for (int i = 0; i < 10; i++) {
                        final Item ITEM = s.getWorld().dropItem(s.getEyeLocation(),  ItemFactory.create(Material.DOUBLE_PLANT, (byte) 0, UUID.randomUUID().toString()) );
                        final Item ITEM2 = s.getWorld().dropItem(s.getEyeLocation(),  ItemFactory.create(Material.EMERALD, (byte) 0, UUID.randomUUID().toString()) );


                        ITEM.setPickupDelay(30000);

                        ITEM.setVelocity(new Vector(r.nextDouble() * 0.3  -0.15 , 0.6, r.nextDouble()*0.3 -0.15 ));

                        ITEM2.setPickupDelay(30000);
                        ITEM2.setVelocity(new Vector(r.nextDouble()*0.5 -0.25 , 0.4, r.nextDouble()*0.5 -0.25 ));

                        items.add(ITEM);
                        items.add(ITEM2);
                    }

                    Bukkit.getScheduler().runTaskLater(LobbySignIn.get(), new Runnable() {
                        @Override
                        public void run() {
                            for (Item i : items)
                                i.remove();
                        }
                    }, 40);


                }

            }.runTaskLater(LobbySignIn.get(), 20);


    }
}
