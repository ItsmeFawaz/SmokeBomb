package me.bottleofglass.SmokeBomb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import me.bottleofglass.SmokeBomb.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
public class SmokeBomb {
    @Getter private static List<SmokeBomb> smokeBombList = new ArrayList<>();
    @Getter private static List<Player> allPlayersInSmoke = new ArrayList<>();
    @Getter private World world;
    @Getter private Location bombLocation;
    @Getter private List<Player> playersInside = new ArrayList<>();
    public SmokeBomb(World world, Location loc, Player p) {
        this.world = world;
        this.bombLocation = loc;
        playersInside.add(p);
        smokeBombList.add(this);
        createSmokeCloud(loc, world, this);
        for(Player playerNearby : world.getPlayers()) {
            if(Util.inRange(loc, playerNearby.getLocation())) {
                addPlayer(playerNearby);
            }
        }

    }
    public void addPlayer(Player p) {
        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 1,true, false));
        for(Player playerNearby : Bukkit.getOnlinePlayers()) {
            if(!playersInside.contains(playerNearby)) {
                playerNearby.hidePlayer(p);
            }
        }
        for(Player player : p.getWorld().getPlayers()) {
            p.showPlayer(player);
        }
        this.playersInside.add(p);
        allPlayersInSmoke.add(p);
    }
    public void removePlayer(Player p) {
        p.removePotionEffect(PotionEffectType.BLINDNESS);
        for(Player playersAround : Bukkit.getOnlinePlayers()) {
            playersAround.showPlayer(p);
            if(playersInside.contains(playersAround)) {
                p.hidePlayer(playersAround);
            }
        }
        this.playersInside.remove(p);
        allPlayersInSmoke.remove(p);
    }
    public void removeAllPlayers() {
        for(Player p: playersInside) {
            p.removePotionEffect(PotionEffectType.BLINDNESS);
            for(Player playersAround : Bukkit.getOnlinePlayers()) {
                playersAround.showPlayer(p);
            }
        }
        this.playersInside.clear();
        allPlayersInSmoke.clear();
    }
    public void createSmokeCloud(final Location loc, World world,SmokeBomb bomb) {
        BukkitRunnable runnable = new BukkitRunnable() {
            int index = 0;
            @Override
            public void run() {
                if(index == 120) {
                    removeAllPlayers();
                    smokeBombList.remove(bomb);
                    cancel();
                }
                for(double index = 0; index < 5; index = index+0.2D) {
                    Location particleLoc = new Location(world, loc.getX(),loc.getY()+index,loc.getZ());
                    float radius = 5f;
                    for(float i = (float) Math.PI; i >= -Math.PI; i = i-0.1f) {
                        double scaleX = (radius * Math.sin(i));
                        double scaleZ = (radius * Math.cos(i));
                        world.playEffect(new Location(world, particleLoc.getX()+scaleX, particleLoc.getY(), particleLoc.getZ()+scaleZ), Effect.SPELL, 0);
                    }
                }
                index++;
            }
        };
        runnable.runTaskTimer(Main.getInstance(), 0, 5);


    }
}
