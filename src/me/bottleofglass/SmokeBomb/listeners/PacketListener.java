package me.bottleofglass.SmokeBomb.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.Packets;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.*;
import com.comphenix.protocol.wrappers.EnumWrappers;
import me.bottleofglass.SmokeBomb.Main;
import me.bottleofglass.SmokeBomb.Vector3D;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class PacketListener implements CommandExecutor {
    ProtocolManager protocolManager;

    public PacketListener() {
        protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(
                new PacketAdapter(Main.getInstance(), ListenerPriority.NORMAL,
                        PacketType.Play.Client.USE_ENTITY) {
                    @Override
                    public void onPacketSending(PacketEvent evt) {
                        // Item packets (id: 0x29)
                        for(int i : evt.getPacket().getIntegers().getValues()) {
                            if(i == Bukkit.getPlayer("Nawaf0902").getEntityId()) {
                                Bukkit.broadcastMessage("VALUE : " + true);
                            }
                        }

                        if (evt.getPacketType() ==
                                PacketType.Play.Server.NAMED_SOUND_EFFECT) {
                            evt.setCancelled(true);
                        }
                    }
                });
        // This is where the magic happens
        protocolManager.getAsynchronousManager().registerAsyncHandler(
                new PacketAdapter(Main.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Client.ARM_ANIMATION) {

                    @Override
                    public void onPacketReceiving(PacketEvent event) {
                        final int ATTACK_REACH = 4;
                        Random rnd = new Random();

                        Player observer = event.getPlayer();
                        Location observerPos = observer.getEyeLocation();
                        Vector3D observerDir = new Vector3D(observerPos.getDirection());

                        Vector3D observerStart = new Vector3D(observerPos);
                        Vector3D observerEnd = observerStart.add(observerDir.multiply(ATTACK_REACH));

                        Player hit = null;

                        // Get nearby entities
                        for (Player target : protocolManager.getEntityTrackers(observer)) {
                            // No need to simulate an attack if the player is already visible
                            if (!observer.canSee(target)) {
                                // Bounding box of the given player
                                Vector3D targetPos = new Vector3D(target.getLocation());
                                Vector3D minimum = targetPos.add(-0.5, 0, -0.5);
                                Vector3D maximum = targetPos.add(0.5, 1.67, 0.5);

                                if (hasIntersection(observerStart, observerEnd, minimum, maximum)) {
                                    if (hit == null || hit.getLocation().distanceSquared(observerPos) > target.getLocation().distanceSquared(observerPos)) {
                                        hit = target;
                                    }
                                }
                            }
                        }

                        // Simulate a hit against the closest player
                        if (hit != null) {
                            PacketContainer useEntity = protocolManager.createPacket(PacketType.Play.Client.USE_ENTITY, false);
                            useEntity.getIntegers().
                                    write(0, observer.getEntityId());
                            useEntity.getEntityModifier(hit.getWorld()).write(0,hit);
                            useEntity.getEntityUseActions().write(0, EnumWrappers.EntityUseAction.ATTACK);

                            // Chance of breaking the visibility
                            if (rnd.nextDouble() < 0.3) {
                                toggleVisibilityNative(observer, hit);
                            }
                            try {
                                protocolManager.recieveClientPacket(event.getPlayer(), useEntity);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    // Get entity trackers is not thread safe
                }).syncStart();
    }

    // Source:
    //    [url]http://www.gamedev.net/topic/338987-aabb---line-segment-intersection-test/[/url]
    private boolean hasIntersection(Vector3D p1, Vector3D p2, Vector3D min, Vector3D max) {
        final double epsilon = 0.0001f;

        Vector3D d = p2.subtract(p1).multiply(0.5);
        Vector3D e = max.subtract(min).multiply(0.5);
        Vector3D c = p1.add(d).subtract(min.add(max).multiply(0.5));
        Vector3D ad = d.abs();

        if (Math.abs(c.x) > e.x + ad.x)
            return false;
        if (Math.abs(c.y) > e.y + ad.y)
            return false;
        if (Math.abs(c.z) > e.z + ad.z)
            return false;

        if (Math.abs(d.y * c.z - d.z * c.y) > e.y * ad.z + e.z * ad.y + epsilon)
            return false;
        if (Math.abs(d.z * c.x - d.x * c.z) > e.z * ad.x + e.x * ad.z + epsilon)
            return false;
        if (Math.abs(d.x * c.y - d.y * c.x) > e.x * ad.y + e.y * ad.x + epsilon)
            return false;

        return true;
    }

    private void toggleVisibilityNative(Player observer, Player target) {
        if (observer.canSee(target)) {
            Bukkit.broadcastMessage("player visible, changing to invisible");
            observer.hidePlayer(target);
        } else {
            observer.hidePlayer(target);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        Player observer = null;
        Player target = null;

        // Get the target argument
        if (args.length > 0) {
            target = Main.getInstance().getServer().getPlayerExact(args[0]);
        } else {
            sender.sendMessage(ChatColor.RED + "This command requires at least one argument.");
            return true;
        }

        // Get the observer argument
        if (args.length == 2) {
            observer = Main.getInstance().getServer().getPlayerExact(args[1]);
        } else {
            if (sender instanceof Player) {
                observer = (Player) sender;
            } else {
                sender.sendMessage(ChatColor.RED + "Optional parameter is only valid for player commands.");
                return true;
            }
        }

        toggleVisibilityNative(observer, target);
        return true;
    }
}
