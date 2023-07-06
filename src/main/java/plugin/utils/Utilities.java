package plugin.utils;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import org.bson.Document;

import static plugin.utils.FindDocument.getDoc;

public class Utilities {
    // finds player using their name (without colors)
    public static Player findPlayerByName(String name){
        return Groups.player.find(t-> t.plainName().contains(name));
    }
    public static Player findPlayerByID(int id){
        Document user = getDoc(id);
        return Groups.player.find(t-> t.uuid().equals(user.getString("uuid")));
    }
    public static <T> T notNullElse(T value, T value2){
        return value != null ? value : value2;
    }

}

