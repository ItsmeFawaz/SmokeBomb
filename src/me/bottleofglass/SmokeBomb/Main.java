package me.bottleofglass.SmokeBomb;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import lombok.Getter;
import me.bottleofglass.SmokeBomb.commands.SmokeBombCommand;
import me.bottleofglass.SmokeBomb.listeners.PacketListener;
import me.bottleofglass.SmokeBomb.listeners.PlayerMovementTracker;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class Main extends JavaPlugin implements Listener {
    @Getter
    private static Main instance;
    @Getter
    private ProtocolManager protocolManager;

    @Override
    public void onEnable() {
        instance = this;
        getCommand("smokebomb").setExecutor(new SmokeBombCommand());
        new PlayerMovementTracker();
        new PacketListener();
    }
}