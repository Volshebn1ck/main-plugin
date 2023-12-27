package plugin.commands;

import arc.util.CommandHandler;
import arc.util.Log;
import arc.util.Timer;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import mindustry.Vars;
import mindustry.content.Items;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.type.Item;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.consumers.Consume;
import org.bson.Document;
import org.json.simple.parser.ParseException;
import plugin.discord.Bot;
import plugin.models.PlayerData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static arc.util.Strings.canParseInt;
import static arc.util.Strings.parseInt;
import static plugin.Plugin.newCollection;
import static plugin.ServersConfig.resetServersConfig;
import static plugin.etc.Ranks.ranks;
import static plugin.functions.MongoDB.*;
import static plugin.utils.FindDocument.getPlayerDataAnyway;
import static plugin.utils.Utilities.findPlayerByID;
import static plugin.utils.Utilities.formatRanks;

public class ConsoleCommands {
    public static void loadServerCommands(CommandHandler handler){
        handler.removeCommand("exit");
        handler.register("exit", "exit the server process", (args) -> {
            Bot.api.disconnect();
            Timer.schedule(()-> {
                System.exit(0);
            }, 1f);
        });
        handler.register("setrank", "<id> <rank>", "Sets rank to player", (args, params) -> {
            PlayerData data = getPlayerDataAnyway(args[0]);
            String rankid = args[1];
            if (data == null){
                Log.warn("No such player!");
                return;
            }
            if (!Arrays.asList(ranks).contains(rankid)){
                Log.warn("This rank doesnt exist!");
                return;
            }
            data.rank = rankid;
            MongoDbUpdate(data);
            Log.info("Rank has been given!");
            Player player = Groups.player.find(p -> p.uuid().equals(data.uuid));
            if (player == null){
                return;
            }
            MongoDbPlayerRankCheck(data.uuid);
        });
        handler.register("check", "Checks mongodb", (args, params) -> {
            MongoDbCheck();
        });
        /*handler.register("transfer", "mongodb transfer", (args) -> {
            try (MongoCursor<PlayerData> cursor = newCollection.find().iterator()) {
                while (cursor.hasNext()) {
                    PlayerData csr = cursor.next();
                    PlayerData data = new PlayerData((String) csr.get("uuid"), csr.getInteger("id"));
                    data.name = csr.getString("name");
                    data.rawName = csr.getString("rawName");
                    data.ip = csr.getString("ip");
                    data.rank = formatRanks(csr.getInteger("rank"));
                    data.playtime = csr.getInteger("playtime");
                    data.achievements = (ArrayList<String>) csr.getList("achievements", String.class);
                    data.joinMessage = csr.getString("joinmessage");
                    data.lastBan = csr.getLong("lastBan");
                    data.discordId = csr.getLong("discordid");
                    try {
                        newCollection.insertOne(data);
                    } catch (Exception e ){
                        Log.debug("Duplicate, ignoring");
                        return;
                    }
                    Log.debug(data.id);
                }
            }
        });*/
         handler.register("reloaddb", "mongodb transfer", (args) -> {
            try (MongoCursor<PlayerData> cursor = newCollection.find().iterator()) {
                while (cursor.hasNext()) {
                    PlayerData csr = cursor.next();
                    MongoDbUpdate(csr);
                }
            }
        });
        handler.register("achcheck", "gives achievements to those who deserves them", (args) -> {
            try (MongoCursor<PlayerData> cursor = newCollection.find(Filters.gte("playtime", 2250)).iterator()) {
                while (cursor.hasNext()) {
                    PlayerData csr = cursor.next();
                    String activeAch = "[lime]Hyper[green]active";
                    if (!csr.achievements.contains(activeAch)) {
                        csr.achievements.add(activeAch);
                        MongoDbUpdate(csr);
                        MongoDbPlayerRankCheck(findPlayerByID(csr.id).uuid());
                    }
                }
            }
            try (MongoCursor<PlayerData> cursor = newCollection.find(Filters.lte("_id", 150)).iterator()) {
                while (cursor.hasNext()) {
                    PlayerData csr = cursor.next();
                    String activeAch = "[orange]Vete[yellow]ran";
                    if (!csr.achievements.contains(activeAch)) {
                        csr.achievements.add(activeAch);
                        MongoDbUpdate(csr);
                        MongoDbPlayerRankCheck(findPlayerByID(csr.id).uuid());
                    }
                }
            }
        });
        handler.register("resetserversconfig", "Resets serverConfig.json file", (args) -> {
            try {
                resetServersConfig();
            } catch (IOException | ParseException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
