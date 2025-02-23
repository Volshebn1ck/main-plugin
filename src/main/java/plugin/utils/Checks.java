package plugin.utils;

import plugin.models.PlayerData;

import java.util.Arrays;
import java.util.Objects;

import static plugin.utils.FindDocument.*;
public class Checks {
    public static String[] adminRanks = new String[]{"moderator", "js", "administrator"};
    public static String[] consoleRanks = new String[]{"js", "administrator"};
    public static boolean isConsole(int id){
        PlayerData data = getPlayerData(id);
        return Arrays.stream(consoleRanks).anyMatch(rank -> rank.equals(data.rank));
    }
    public static boolean isConsole(String uuid){
        PlayerData data = getPlayerData(uuid);
        return Arrays.stream(consoleRanks).anyMatch(rank -> rank.equals(data.rank));
    }
    public static boolean isAdmin(int id){
        PlayerData data = getPlayerData(id);
        return Arrays.stream(adminRanks).anyMatch(rank -> rank.equals(data.rank));
    }
    public static boolean isAdmin(String uuid){
        PlayerData data = getPlayerData(uuid);
        return Arrays.stream(adminRanks).anyMatch(rank -> rank.equals(data.rank));
    }
    public static boolean isVipOrOwner(int id){
        PlayerData data = getPlayerData(id);
        return Objects.equals(data.rank, "administrator") || data.isVip;
    }
    public static boolean isVipOrOwner(String uuid){
        PlayerData data = getPlayerData(uuid);
        return Objects.equals(data.rank, "administrator") || data.isVip;
    }
}
