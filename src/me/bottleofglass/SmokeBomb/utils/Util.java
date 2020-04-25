package me.bottleofglass.SmokeBomb.utils;

import org.bukkit.Location;

public class Util {
    public static boolean inRange(Location bombLoc, Location playerLoc) {
        double distance = Math.sqrt(Math.pow(bombLoc.getX()- playerLoc.getX(),2) + Math.pow(bombLoc.getZ() - playerLoc.getZ(), 2));
        double yDistance = bombLoc.getY() - playerLoc.getY();
        if(distance < 5.5D && yDistance > -5 && yDistance <2) {
            return true;
        }
        return false;
    }
}
