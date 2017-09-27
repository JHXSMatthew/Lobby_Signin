package com.mcndsj.lobby_signin.listeners;

import com.mcndsj.lobbyMoney.MoneyType;
import com.mcndsj.lobbyMoney.api.LobbyMoneyApi;
import com.mcndsj.lobby_Vip.LobbyVip;
import com.mcndsj.lobby_Vip.api.VipType;
import com.mcndsj.lobby_signin.Config;
import com.mcndsj.lobby_signin.LobbySignIn;
import com.mcndsj.lobby_signin.managers.SignManager;
import com.mcndsj.lobby_signin.managers.ThreadManager;
import com.mcndsj.lobby_signin.utils.ItemFactory;
import com.mcndsj.lobby_signin.utils.SQLUtils;
import com.mcndsj.lobby_signin.utils.SignInfoContainer;
import com.mcndsj.lobby_signin.utils.VIPUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.concurrent.SynchronousQueue;


/**
 * Created by Matthew on 3/07/2016.
 */
public class SignInventory implements Listener {

    private static String title = "史姆莱";
    private static String dailySign = ChatColor.GREEN  +"每日签到";
    private static String monthSign = ChatColor.GREEN  +"会员福利";

    private Slime boundTo = null;
    private String player;
    private boolean displayed = false;
    private long lastClick = 0;
    private SignInfoContainer cache;

    public SignInventory(String player,Slime boundTo ){
        this.player = player;
        this.boundTo = boundTo;
        LobbySignIn.get().getServer().getPluginManager().registerEvents(this,LobbySignIn.get());
    }


    public void dispose(){
        HandlerList.unregisterAll(this);
    }

    public Player getPlayer(){
        return Bukkit.getPlayer(player);
    }

    public void disPlay(){
        if(getPlayer() == null || !getPlayer().isOnline())
            dispose();
        displayed = true;
        ThreadManager.get().runTask(new Runnable() {
            @Override
            public void run() {
                if(cache == null) {
                    cache = SQLUtils.getSignInfo(getPlayer().getName());
                }
                displayCall(cache);
            }
        });

    }

    private void displayCall(SignInfoContainer container){

        int hour = (int) ((container.getLastSign() + 86400000 - System.currentTimeMillis() )/ 1000/60/60) ;
        int min = (int) ((container.getLastSign() + 86400000 - System.currentTimeMillis() )/ 1000/60 % 60);


        if(container.getLastSign() + 86400000*2 <  System.currentTimeMillis()){
            container.setStack(0);
        }

        if(container.getLastSign() == 0  ){
            hour = 0;
            min = 0;
        }

        Material M_daily = Material.GLASS_BOTTLE;
        if(hour <= 0 && min <=0){
            hour = 0;
            min = 0;
            M_daily = Material.EXP_BOTTLE;
        }



        ItemStack daily = ItemFactory.create(M_daily,(byte)0,dailySign
                , ChatColor.GRAY  +"点击签到."
                , " "
                ,ChatColor.GRAY  +"金币:"+ ChatColor.LIGHT_PURPLE +" +" + String.valueOf(15 + 15 * container.getStack() * 0.05)
                ,ChatColor.GRAY  +"经验: "+ ChatColor.LIGHT_PURPLE +"+" +  String.valueOf( 100 + 100 * container.getStack() * 0.05)
                , "  "
                ,ChatColor.GRAY  +"连续签到: " + container.getStack() + ChatColor.GRAY.toString() + " 天"
                ,ChatColor.GRAY  +"额外奖励: " + container.getStack()  * 5 + "%"
                ,ChatColor.GRAY  +"距离下次签到还剩: " + hour + " 小时 " + min +" 分钟");



        String VIPname = ChatColor.WHITE + "无会员";
        String monthInfo = ChatColor.GREEN + "购买VIP即可领取每月福利";
        int count = 0;
        Material M_vipSign = Material.MINECART;

        if(LobbyVip.getApi().isVip(player)) {
            VipType type = LobbyVip.getApi().getvipType(player);

            VIPname = type.toString();
            if(Calendar.getInstance().get(Calendar.MONTH) != container.getLastSignMonth()){
                monthInfo = ChatColor.GREEN + "点击可领取福利";
                M_vipSign = Material.STORAGE_MINECART;
            }else{
                monthInfo = ChatColor.YELLOW + "当前月份已领取过福利";
            }
            count = VIPUtils.vipLevelToMonthChest(type.getLevel());
        }

        ItemStack vipSign = ItemFactory.create(M_vipSign,(byte)0,monthSign
                , ChatColor.GRAY  +"点击领取会员每月福利."
                , " "
                ,ChatColor.GRAY  +"当前会员: "+ ChatColor.LIGHT_PURPLE + VIPname
                ,ChatColor.GRAY  +"珍藏宝箱: "+ ChatColor.LIGHT_PURPLE +"+" + count
                , "  "
                ,ChatColor.GRAY  + monthInfo
        );

        if( getPlayer()!= null && getPlayer().isOnline()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Inventory i = Bukkit.createInventory(null, 27, title);
                    i.setItem(15, vipSign);
                    i.setItem(11, daily);
                    getPlayer().openInventory(i);
                }
            }.runTask(LobbySignIn.get());
        }else{
            dispose();
        }

    }


    @EventHandler
    public void onClick(InventoryClickEvent evt){
        if(isPlayer(evt.getClickedInventory())){
            evt.setCancelled(true);
        }
        if(!isInventory(evt.getClickedInventory()))
            return;
        if(evt.getCurrentItem() == null || evt.getCurrentItem().getType() == Material.AIR || !evt.getCurrentItem().hasItemMeta()){
            return;
        }

        if(lastClick + 1000 > System.currentTimeMillis()) {
            return;
        }

        if(cache == null) {
            return;
        }


        lastClick = System.currentTimeMillis();

        if(evt.getCurrentItem().getItemMeta().getDisplayName().equals(dailySign)){
            ThreadManager.get().runTask(new Runnable() {
                @Override
                public void run() {
                    long last = cache.getLastSign();
                    long current = System.currentTimeMillis();


                    if( current < last + 86400000  && current >= last){
                        getPlayer().sendMessage(Config.prefix + "时机尚未成熟，少侠仍需等待.");
                        getPlayer().closeInventory();
                        cache = null;
                        return ;
                    }

                    if(last == 0 || current  > last + 86400000 * 2){
                        SQLUtils.setLastSign(getPlayer().getName(), System.currentTimeMillis(), 1, cache.getLastSign() <= 0 ? true : false);
                        LobbyMoneyApi.get().setCurrency(getPlayer().getName(), MoneyType.money, LobbyMoneyApi.get().getCurrency(getPlayer().getName(),MoneyType.money) + 15);
                        SignManager.get().playEffect(boundTo);
                        getPlayer().sendMessage(Config.prefix +"恭喜您成功领取每日奖励,当前连续签到 " + ChatColor.RED + "1" + ChatColor.GRAY+" 天.");
                        getPlayer().closeInventory();
                        cache = null;
                        return ;
                    }

                    if( current >= last + 86400000   &&  current < last + 86400000 *2   ){
                        int stack = cache.getStack();

                        if(cache.getStack() <10){
                            stack ++;
                        }
                        SQLUtils.setLastSign(getPlayer().getName(), System.currentTimeMillis(), stack ,  false);
                        LobbyMoneyApi.get().setCurrency(getPlayer().getName(), MoneyType.money, LobbyMoneyApi.get().getCurrency(getPlayer().getName(),MoneyType.money) + (int)(15 + 15 * stack * 0.05));
                        SignManager.get().playEffect(boundTo);
                        getPlayer().playSound(getPlayer().getLocation(), Sound.LEVEL_UP,1F,1F);
                        getPlayer().sendMessage(Config.prefix +"恭喜您成功领取每日奖励,当前连续签到 " + ChatColor.RED + stack + ChatColor.GRAY+" 天.");
                        getPlayer().closeInventory();
                        cache = null;
                        return ;
                    }

                }
            });
        }else if(evt.getCurrentItem().getItemMeta().getDisplayName().equals(monthSign)) {
            ThreadManager.get().runTask(new Runnable() {
                @Override
                public void run() {
                    int month = cache.getLastSignMonth();
                    Calendar cal = Calendar.getInstance();

                    if (month == -1 || !LobbyVip.getApi().isVip(getPlayer().getName())) {
                        getPlayer().sendMessage(Config.prefix +"您不是我们的会员.开通会员领取每月珍藏宝箱福利!详情查看 www.mcndsj.com");
                        getPlayer().closeInventory();
                        cache = null;
                        return;
                    }

                    if (cal.get(Calendar.MONTH) == month) {
                        getPlayer().closeInventory();
                        getPlayer().sendMessage(Config.prefix +"时机尚未成熟，少侠仍需等待.");
                        cache = null;
                        return;
                    }


                    int vipLevel = LobbyVip.get().getVipLevel(getPlayer().getName());
                    if (vipLevel <= 0) {
                        cache = null;
                        return;
                    }
                    SQLUtils.setVipLastSignMonth(getPlayer().getName(), cal.get(Calendar.MONTH));

                    SignManager.get().giveChest(getPlayer().getName(), VIPUtils.vipLevelToMonthChest(vipLevel));
                    SignManager.get().playEffect(boundTo);
                    getPlayer().playSound(getPlayer().getLocation(), Sound.LEVEL_UP,1F,1F);
                    getPlayer().sendMessage(Config.prefix + "恭喜您成功领取每月福利,感谢您对我们的支持.");
                    getPlayer().closeInventory();
                    cache = null;
                }
            });
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent evt){
        if(isInventory(evt.getInventory()))
            dispose();

    }

    @EventHandler
    public void onLeave(PlayerQuitEvent evt){
        if(evt.getPlayer().getName().equals(player)){
            dispose();
        }

    }

    private boolean isInventory(Inventory i){
        if (i != null&& i.getTitle() != null && i.getTitle().equals(title)) {
            for(HumanEntity entity : i.getViewers()){
                if(entity instanceof Player){
                    Player p = (Player) entity;
                    if(p.getName().equals(player)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isPlayer(Inventory i){
        if (i != null){
            for(HumanEntity entity : i.getViewers()){
                if(entity instanceof Player){
                    Player p = (Player) entity;
                    if(p.getName().equals(player)){
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
