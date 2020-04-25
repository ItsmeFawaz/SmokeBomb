package me.bottleofglass.SmokeBomb.commands;

import me.bottleofglass.SmokeBomb.Main;
import me.bottleofglass.SmokeBomb.SmokeBomb;
import me.bottleofglass.SmokeBomb.listeners.PlayerMovementTracker;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SmokeBombCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(strings.length == 0) {
            commandSender.sendMessage(ChatColor.GOLD + "/smokebomb <playername>");
            return true;
        }
        if (Bukkit.getPlayer(strings[0]) != null) {
            Player p = Bukkit.getPlayer(strings[0]);
            World world = p.getWorld();
            Location loc = p.getLocation().getBlock().getLocation();
            SmokeBomb bomb = new SmokeBomb(world, new Location(world,loc.getX()+0.5D, loc.getY(), loc.getZ()+0.5D), p);
            bomb.addPlayer(p);

            return true;
        } else {
            commandSender.sendMessage(ChatColor.RED + "Invalid Player");
            return true;
        }
    }

}
