package plugin;

import arc.*;
import arc.util.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import mindustry.Vars;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.mod.Plugin;
import mindustry.gen.Player;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.javacord.api.*;
import org.javacord.api.entity.intent.Intent;
import org.json.simple.parser.ParseException;
import plugin.discord.Bot;
import plugin.ConfigJson;
import plugin.utils.Utilities;


import java.io.IOException;
import java.util.Map;

import static com.mongodb.client.model.Filters.eq;
import static mindustry.Vars.*;



public class Ploogin extends Plugin implements ApplicationListener{
    MongoClient mongoClient;
    MongoDatabase db;
    MongoCollection<Document> playerCollection;
    public static Player victim;
    public static String reason;
    public static Player moderator;

    // loads bot and other shit
    public Ploogin() throws IOException, ParseException {
        ConfigJson.read();
        Bot.load();
        mongoClient = MongoClients.create(ConfigJson.mongodburl);
        db = mongoClient.getDatabase("mindustry");
        playerCollection = db.getCollection("players");
    }
    public void MongoDbPlayerCreation(Player eventPlayer){
        var id = new ObjectId();
        Document plrDoc = new Document("_id", id);
        plrDoc.append("uuid", eventPlayer.uuid());
        plrDoc.append("lastBan", "0");
        Document chk = playerCollection.find(Filters.eq(eventPlayer.uuid())).first();
        if (chk == null){
            playerCollection.insertOne(plrDoc);
        } else {
            return;
        }
    }
    //  starts once plugin is started
    public void init() {
        Log.info("Plugin started!");
        Events.on(EventType.PlayerJoin.class, event ->
                MongoDbPlayerCreation(event.player)
        );
    }
    // registers commands for client such as /ping
    @Override
    public void registerClientCommands(CommandHandler handler){
        handler.<Player>register("announce", "<text...>", "calls an announce", (args, player)->{
            if (!player.admin) {
                player.sendMessage("You do not have enough permissions!");
            } else {
                Call.announce(args[0]);
            }
        });
        handler.<Player>register("gameover", "Executes a gameover event", (args, player) -> {
            if (!player.admin) {
                player.sendMessage("You do not have enough permissions!");
            } else {
                 Events.fire(new EventType.GameOverEvent(Team.derelict));
            }
        });
        handler.<Player>register("list", "Lists all players on the server", (args, player) -> {
            Groups.player.each(player1 -> player.sendMessage("Name: " + player1.name + "; id: " + player1.id()));
        });
        handler.<Player>register("js", "<code...>", "Execute JavaScript code.", (args, player) -> {
            if (player.admin()) {
                try {
                    String output = mods.getScripts().runConsole(args[0]);
                    player.sendMessage("> " + ("[#ff341c]" + output));
                } catch (Exception e) {
                    player.sendMessage("Error! " + e);
                    return;
                }
            } else {
                player.sendMessage("[scarlet]You must be admin to use this command.");
            }
        });
        handler.<Player>register("ban", "<player> <reason...>",  "Bans the players", (args,player) -> {
            String id = args[0];
            reason = args[1];
            victim = Utilities.findPlayerByName(id);
            moderator = player;
            if (victim == null){
                player.sendMessage("This player doesnt exist!");
                return;
            }
            if (victim.admin()){
                player.sendMessage("[red]You cant ban an admin!");
                return;
            }
            if (victim == player){
                player.sendMessage("You cant ban yourself!");
                return;
            }
            if (player.admin()){
                Call.menu(player.con, plugin.utils.MenuHandler.banMenu, "Ban", "Are you sure you want to ban " + victim.plainName() + "?", new String[][]{{"Confirm ", "Cancel"}});
            } else {
                player.sendMessage("[red]Not enough permissions!");
            }
        });
    }
    // i dont know why it even exists
    @Override
    public void dispose(){
        exit();
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
    }
}
