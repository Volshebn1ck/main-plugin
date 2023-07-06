package plugin.utils;
import org.bson.Document;

import static plugin.utils.FindDocument.*;
public class Checks {
    public static boolean isConsole(int id){
        Document user = getDoc(id);
        return user.getInteger("rank") >= 3;
    }
    public static boolean isConsole(String uuid){
        Document user = getDoc(uuid);
        return user.getInteger("rank") >= 3;
    }
    public static boolean isAdmin(int id){
        Document user = getDoc(id);
        return user.getInteger("rank") >= 2;
    }
    public static boolean isAdmin(String uuid){
        Document user = getDoc(uuid);
        return user.getInteger("rank") >= 2;
    }
}
