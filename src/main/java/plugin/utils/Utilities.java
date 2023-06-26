package plugin.utils;

import arc.util.Strings;
import mindustry.gen.Groups;
import mindustry.gen.Player;

public class Utilities {
    // finds player using their name (without colors)
    public static Player findPlayerByName(String name){
        return Groups.player.find(t-> t.plainName().equals(name));
    }
    public static Player findPlayerById(int id){
        return Groups.player.find(p -> p.id() == id);
    }
    public static boolean isPlayerOutOfBounds(Player player){
        if(player == null) return false;
        if(player.getX()/8 > 250 && player.getX()/8 < 253) return true;
        return false;
    }
}

