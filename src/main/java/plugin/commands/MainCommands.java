package plugin.commands;

import arc.Events;
import arc.struct.Seq;
import arc.util.CommandHandler;
import com.mongodb.client.model.Filters;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.maps.Map;
import org.bson.Document;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import static arc.util.Strings.canParseInt;
import static mindustry.Vars.*;
import static plugin.ConfigJson.discordurl;
import static plugin.Plugin.plrCollection;
import static plugin.functions.Other.statsMenu;
import static plugin.utils.Checks.isConsole;
import static plugin.utils.Utilities.*;

public class MainCommands {
    public static AtomicInteger votes = new AtomicInteger(0);
    public static Seq<Player> votedPlayer = new Seq<>();
    public static boolean isVoting = false;
    public static void loadClientCommands(CommandHandler handler){
        handler.<Player>register("announce", "<text...>", "calls an announce", (args, player)->{
            if (!player.admin) {
                player.sendMessage("[red]You do not have enough permissions!");
            } else {
                Call.announce(args[0]);
            }
        });
        handler.<Player>register("gameover", "Executes a gameover event", (args, player) -> {
            if (!player.admin) {
                player.sendMessage("[red]You do not have enough permissions!");
            } else {
                Events.fire(new EventType.GameOverEvent(Team.derelict));
            }
        });
        handler.<Player>register("list", "Lists all players on the server", (args, player) -> {
            StringBuilder list = new StringBuilder();
            for (Player plr : Groups.player){
                Document user = plrCollection.find(Filters.eq("uuid", plr.uuid())).first();
                int id = user.getInteger("id");
                list.append(plr.name()+ "; [white]ID: " + id + "\n");
            }
            player.sendMessage(String.valueOf(list));
        });
        handler.<Player>register("js", "<code...>", "Execute JavaScript code.", (args, player) -> {
            Document user = plrCollection.find(Filters.eq("uuid", player.uuid())).first();
            if (player.admin() && isConsole(player.uuid())) {
                try {
                    String output = mods.getScripts().runConsole(args[0]);
                    player.sendMessage("> " + ("[#ff341c]" + output));
                } catch (Exception e) {
                    player.sendMessage("Error! " + e);
                    return;
                }
            } else {
                player.sendMessage("[scarlet]You must be console to use this command.");
            }
        });
        handler.<Player>register("maps", "<page>","List all maps", (args, player) -> {
            StringBuilder list = new StringBuilder();
            if (!canParseInt(args[0])){
                player.sendMessage("[red]Page must be number!");
                return;
            }
            int mapsPerPage = 10;
            int page = Integer.parseInt(args[0]);
            Seq<Map> maps = getMaps();
            maps.list().stream().skip(page*10L).limit(mapsPerPage + (page * 10L)).forEach(
                    map -> list.append(map.name() + "[white], by " + map.author())
            );
            if (!String.valueOf(list).contains("by")){
                player.sendMessage("[red]No maps detected!");
                return;
            }
            player.sendMessage(String.valueOf(list));
        });
        handler.<Player>register("rtv", "<map...>", "Rock the vote to change map!", (args, player) -> {
            final int[] votesRequired = new int[1];
            AtomicInteger time = new AtomicInteger(60);
            votesRequired[0] = (int) Math.ceil((double) Groups.player.size()/2);
            Timer timer = new Timer();
            if (isVoting){
                player.sendMessage("Vote is already running!");
                return;
            }
            Map choosedMap = Vars.maps.customMaps().find(map -> map.name().contains(args[0]));
            if (choosedMap == null){
                player.sendMessage("Could not find that map!");
                return;
            }
            Call.sendMessage(player.plainName() + " Started vote for map " + choosedMap.plainName() + " -> " + votes.get() +"/"+ votesRequired[0] + ", y/n to vote");
            isVoting = true;
            timer.schedule((new TimerTask() {
                @Override
                public void run() {
                    time.getAndAdd(-1);
                    votesRequired[0] = (int) Math.ceil((double) Groups.player.size() / 2);
                    if (votes.get() >= votesRequired[0]) {
                        voteSuccess(choosedMap);
                        isVoting = false;
                        timer.cancel();
                    }
                    if (time.get() < 0) {
                        voteCanceled();
                        isVoting = false;
                        timer.cancel();
                    }
                }
            }), 0, 1000);
        });
        handler.<Player>register("discord", "Link to our discord!", (args,player) -> {
            Call.openURI(player.con, discordurl);
        });
        handler.<Player>register("stats", "[player...]", "Get stats of player or yourself", (args, player) -> {
            if (args.length == 0){
                statsMenu(player, player);
                return;
            }
            Player plr = findPlayerByName(args[0]);
            if (plr == null){
                player.sendMessage("Could not find that player!");
                return;
            }
            statsMenu(player, plr);
        });
    }
}
