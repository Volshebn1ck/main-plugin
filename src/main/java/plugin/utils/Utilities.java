package plugin.utils;

import arc.util.Strings;
import mindustry.gen.Groups;
import mindustry.gen.Player;
public class Utilities {
    // finds player using their name (without colors)
    public static Player findPlayerByName(String name){
        return Groups.player.find(t-> t.plainName().contains(name));
    }
}

