package plugin;

import arc.ApplicationListener;
import arc.Events;
import arc.util.CommandHandler;
import arc.util.Log;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import mindustry.game.EventType;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import org.bson.Document;
import org.json.simple.parser.ParseException;
import plugin.discord.Bot;
import useful.Bundle;

import java.io.IOException;

import static plugin.commands.BanMenu.loadBanMenu;
import static plugin.commands.ConsoleCommands.loadServerCommands;
import static plugin.commands.MainCommands.*;
import static plugin.functions.MongoDB.*;
import static plugin.functions.Other.kickIfBanned;
import static plugin.functions.Other.welcomeMenu;


public class Plugin extends mindustry.mod.Plugin implements ApplicationListener{
    public static MongoClient mongoClient;
    public static MongoDatabase db;
    public static MongoCollection<Document> plrCollection;

    // loads bot and other shit
    public Plugin() throws IOException, ParseException {
        ConfigJson.read();
        Bot.load();
        mongoClient = MongoClients.create(ConfigJson.mongodburl);
        db = mongoClient.getDatabase("mindustry");
        plrCollection = db.getCollection("players");
    }

    //  starts once plugin is started
    public void init() {
        loadBanMenu();
        Log.info("Plugin started!");
        Bundle.load(Plugin.class);
        Events.on(EventType.PlayerJoin.class, event -> {
            Player plr = event.player;
            welcomeMenu(plr);
            MongoDbPlayerCreation(plr);
            MongoDbPlayerNameCheck(plr);
            MongoDbPlayerRankCheck(plr.uuid());
        });
        MongoDbPlaytimeTimer();
        MongoDbCheck();
        Events.on(EventType.PlayerConnect.class, event -> kickIfBanned(event.player));
        Events.on(EventType.PlayerChatEvent.class, event ->{
            if (isVoting){
                if (votedPlayer.contains(event.player)){
                    event.player.sendMessage("You already voted!");
                    return;
                }
                int votesRequired = (int) Math.ceil((double) Groups.player.size()/2);
                switch (event.message){
                    case "y" ->{
                        votes.getAndAdd(1);
                        votedPlayer.add(event.player);
                        Call.sendMessage(event.player.plainName() +" Voted: " + votes.get() +"/"+ votesRequired);
                    }
                    case "n" ->{
                        votes.getAndAdd(-1);
                        votedPlayer.add(event.player);
                        Call.sendMessage(event.player.plainName() +" Voted: " + votes.get() +"/"+ votesRequired);
                    }
                }
            }
        });
    }


    @Override
    public void registerClientCommands(CommandHandler handler){
        loadClientCommands(handler);
    }
    @Override
    public void registerServerCommands(CommandHandler handler){
        loadServerCommands(handler);
    }
}
