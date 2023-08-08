package plugin.functions;

import arc.Core;
import arc.struct.Seq;
import com.mongodb.client.model.Filters;
import mindustry.gen.Call;
import mindustry.gen.Player;
import org.bson.Document;
import plugin.utils.MenuHandler;
import useful.Bundle;

import java.lang.reflect.Array;
import java.time.Duration;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

import static mindustry.Vars.mods;
import static plugin.ConfigJson.discordurl;
import static plugin.Plugin.plrCollection;
import static plugin.etc.Ranks.*;
import static plugin.utils.FindDocument.getDoc;

public class Other {

    public static void welcomeMenu(Player player){
        String title = "\uE86B Welcome!";
        String description = "[orange]Welcome to our server!\n\n" +
                "[red]<[orange]Rules[red]>\n" +
                "[#f]\uE815 [orange]Do not grief or sabotage your team.\n" +
                "[#f]\uE815 [orange]Do not build/write any NSFW or offensive content.\n" +
                "[#f]\uE815 [orange]Do not try lag the server using lag machines or similar stuff.\n" +
                "[green]\uE800 [orange]Use common sense, do not be toxic/mean to others.\n\n" +
                "[orange]Write /help to see all commands that are available on server.\n" +
                "[blue]\uE80D Also make sure to join our discord.";
        String button1 = "Close";
        String button2 = "[blue]\uE80D Join our discord!";
        Call.menu(player.con, MenuHandler.welcomeMenu, title, description, new String[][]{{button1}, {button2}});
    }
    public static void kickIfBanned(Player player){
        Document user = plrCollection.find(Filters.eq("uuid", player.uuid())).first();
        if (user == null){
            return;
        }
        long lastBan = user.getLong("lastBan");
        Date date = new Date();
        if (lastBan > date.getTime()) {
            String timeUntilUnban = Bundle.formatDuration(lastBan - date.getTime());
            player.con.kick("[red]You have been banned!\n\n" +"[white]Duration: " + timeUntilUnban + " until unban\n\nIf you think this is a mistake, make sure to appeal ban in our discord: " + discordurl, 0);
        }
    }
    public static void statsMenu(Player player, Player reqPlayer){
        Document user = getDoc(reqPlayer.uuid());
        String rank = rankName(user.getInteger("rank"));
        String title = "\uE86B Stats";
        long playtime = Long.parseLong(String.valueOf(user.getInteger("playtime")));
        String description  = "[orange]Name: " + reqPlayer.name()
        + "\n[orange]ID: [white]" + user.getInteger("id")
        + "\n[orange]Rank: " + rank
        + "\n\n[orange]Playtime: [white]" + Bundle.formatDuration(Duration.ofMinutes(playtime));
        String button = "[red]Close";
        Call.menu(player.con, MenuHandler.statsMenu, title, description, new String[][]{{button}});
    }
    public static  String readValueFromArraySeparated(String[] array, int startPoint, int endPoint){
        String[] newArray = Arrays.copyOfRange(array, startPoint, endPoint);
        StringBuilder ha = new StringBuilder();
        for (String element : newArray){
            ha.append(element).append(" ");
        }
        String a = String.valueOf(ha);
        return a.substring(0, a.length()-1);
    }
    public static <T> boolean inBounds(T[] array, int index){
        return (index >= 0) && (index < array.length);
    }
}
