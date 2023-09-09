package plugin;

import arc.ApplicationListener;
import arc.Events;
import arc.util.CommandHandler;
import arc.util.Log;
import arc.util.Strings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.net.Packets;
import org.bson.Document;
import org.json.simple.parser.ParseException;
import plugin.discord.Bot;
import plugin.etc.AntiVpn;
import useful.Bundle;

import javax.sql.ConnectionEvent;
import java.io.IOException;

import static mindustry.Vars.*;
import static plugin.ConfigJson.discordurl;
import static plugin.commands.BanMenu.loadBanMenu;
import static plugin.commands.ConsoleCommands.loadServerCommands;
import static plugin.commands.MainCommands.*;
import static plugin.commands.history.History.historyPlayers;
import static plugin.commands.history.History.loadHistory;
import static plugin.etc.AntiVpn.loadAntiVPN;
import static plugin.functions.MongoDB.*;
import static plugin.functions.Other.kickIfBanned;
import static plugin.functions.Other.welcomeMenu;
import static plugin.utils.FindDocument.getDoc;


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
        loadAntiVPN();
        loadBanMenu();
        loadHistory();
        Log.info("Plugin started!");
        Bundle.load(Plugin.class);
        Events.on(EventType.PlayerJoin.class, event -> {
            Player plr = event.player;
            welcomeMenu(plr);
            MongoDbPlayerCreation(plr);
            MongoDbPlayerNameCheck(event.player);
            MongoDbPlayerRankCheck(plr.uuid());
            kickIfBanned(event.player);
            Document user = getDoc(plr.uuid());
            String joinMessage = user.getString("joinmessage");
            if (joinMessage.endsWith(" ")){
                joinMessage = joinMessage.substring(0, joinMessage.length()-1);
            }
            Call.sendMessage(Strings.format(joinMessage + " [grey][" + user.getInteger("id") + "]", plr.name()));
        });
        MongoDbPlaytimeTimer();
        Vars.net.handleServer(Packets.Connect.class, (con, connect) -> {
            Events.fire(new EventType.ConnectionEvent(con));
            MongoDbPlayerIpCheck(con);

            if (netServer.admins.isIPBanned(connect.addressTCP) || netServer.admins.isSubnetBanned(connect.addressTCP)){
                con.kick(Packets.KickReason.banned);
            }
            kickIfBanned(con);
            if (AntiVpn.checkAddress(connect.addressTCP))
                con.kick("[orange]You are suspected in using VPN or being a bot! Please, if its not true, report that incident on our discord: " + discordurl );
        });
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
        Events.on(EventType.PlayerLeave.class, event -> {
            historyPlayers.remove(event.player.uuid());
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
