package me.bottleofglass.SmokeBomb.listeners;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import me.bottleofglass.SmokeBomb.Main;
import me.bottleofglass.SmokeBomb.SmokeBomb;
import me.bottleofglass.SmokeBomb.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

public class PlayerMovementTracker {
    ProtocolManager protocolManager;
    //TODO Make players invisible to other players outside cloud
    public PlayerMovementTracker() {
        protocolManager = ProtocolLibrary.getProtocolManager();
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                outerloop:
                for(Player p : Bukkit.getOnlinePlayers()) {
                    if(SmokeBomb.getSmokeBombList() != null) {
                        for(SmokeBomb bomb : SmokeBomb.getSmokeBombList()) {
                            if(bomb.getWorld().equals(p.getWorld()) && Util.inRange(bomb.getBombLocation(), p.getLocation())) {
                                if(bomb.getPlayersInside().contains(p)) {
                                    continue outerloop;
                                }
                                bomb.addPlayer(p);
                                if(Math.random() <0.5D) {
                                    Bukkit.broadcastMessage("added player " + p.getName());
                                } else {
                                    Bukkit.broadcastMessage("added player " + ChatColor.RED+ p.getName());
                                }
                            } else {
                                if(bomb.getPlayersInside().contains(p)) {
                                    Bukkit.broadcastMessage("removed player : " + p.getName());
                                    bomb.removePlayer(p);
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 5);
    }

}
