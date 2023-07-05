package plugin;

import arc.*;
import arc.util.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import mindustry.Vars;
import mindustry.game.*;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.mod.Plugin;
import mindustry.gen.Player;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.json.simple.parser.ParseException;
import plugin.discord.Bot;
import useful.Bundle;
import java.io.IOException;
import java.util.Date;

import static mindustry.Vars.mods;
import static plugin.commands.BanMenu.loadBanMenu;
import static plugin.functions.MongoDB.MongoDbPlayerCreation;
import static plugin.functions.MongoDB.MongoDbPlayerRankCheck;


public class Ploogin extends Plugin implements ApplicationListener{
    public static MongoClient mongoClient;
    public static MongoDatabase db;
    public static MongoCollection<Document> playerCollection;
    public static Player victim;
    public static String reason;
    public static Player moderator;

    public static long time;

    // loads bot and other shit
    public Ploogin() throws IOException, ParseException {
        ConfigJson.read();
        Bot.load();
        mongoClient = MongoClients.create(ConfigJson.mongodburl);
        db = mongoClient.getDatabase("mindustry");
        playerCollection = db.getCollection("players");
    }

    //  starts once plugin is started
    public void init() {
        loadBanMenu();
        Log.info("Plugin started!");
        Bundle.load(Ploogin.class);
        Events.on(EventType.PlayerJoin.class, event -> {
            Player plr = event.player;
            MongoDbPlayerCreation(plr);
            MongoDbPlayerRankCheck(plr.uuid());
        });
        Events.on(EventType.PlayerConnect.class, event -> {
            Document user = playerCollection.find(Filters.eq("uuid", event.player.uuid())).first();
            if (user == null){
                return;
            }
            long lastBan = user.getLong("lastBan");
            Date date = new Date();
            if (lastBan > date.getTime()) {
                String timeUntilUnban = Bundle.formatDuration(lastBan - date.getTime());
                event.player.con.kick("You have been banned! Wait " + timeUntilUnban + " more for unban!", 0);
            }
        });
    }


    @Override
    public void registerClientCommands(CommandHandler handler){
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
                Document user = playerCollection.find(Filters.eq("uuid", plr.uuid())).first();
                int id = user.getInteger("id");
                list.append(plr.name()+ "; [white]ID: " + id + "\n");
            }
            player.sendMessage(String.valueOf(list));
        });
        handler.<Player>register("js", "<code...>", "Execute JavaScript code.", (args, player) -> {
            Document user = playerCollection.find(Filters.eq("uuid", player.uuid())).first();
            boolean isConsole = user.getInteger("rank") == 2;
            if (player.admin() && isConsole) {
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
    }
    // shit to register console commands because yes
    @Override
    public void registerServerCommands(CommandHandler handler){
        handler.removeCommand("exit");
        handler.register("exit", "exit the server process", (args) -> {
            Bot.api.disconnect();
            Timer.schedule(()-> {
                System.exit(0);
            }, 1f);
        });
        handler.register("setrank", "<id> <rank>", "Sets rank to player", (args, params) -> {
            int id = Integer.parseInt(args[0]);
            int rankid = Integer.parseInt(args[1]);
            Document user = playerCollection.find(Filters.eq("id", id)).first();
            if (user == null){
                Log.warn("This user doesnt exist!");
                return;
            }
            Bson updates = Updates.combine(
                    Updates.set("rank", rankid)
            );
            playerCollection.updateOne(user, updates, new UpdateOptions().upsert(true));
            Log.info("Rank has been given!");
            MongoDbPlayerRankCheck(user.getString("uuid"));
        });
    }
}
