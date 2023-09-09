package plugin.commands;

import arc.util.CommandHandler;
import arc.util.Log;
import arc.util.Timer;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import org.bson.Document;
import org.bson.conversions.Bson;
import plugin.discord.Bot;

import java.util.ArrayList;

import static arc.util.Strings.canParseInt;
import static arc.util.Strings.parseInt;
import static plugin.Plugin.plrCollection;
import static plugin.functions.MongoDB.*;
import static plugin.utils.FindDocument.getDoc;
import static plugin.utils.FindDocument.getDocAnyway;

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
            Document user = getDocAnyway(args[0]);
            if (!canParseInt(args[1])){
                Log.warn("Type a number!");
                return;
            }
            int rankid = parseInt(args[1]);
            if (user == null){
                Log.warn("No such player!");
                return;
            }
            if (rankid > 4){
                Log.warn("This rank doesnt exist!");
            }
            MongoDbUpdate(user, Updates.set("rank", rankid));
            Log.info("Rank has been given!");
            Player player = Groups.player.find(p -> p.uuid().equals(user.getString("uuid")));
            if (player == null){
                return;
            }
            MongoDbPlayerRankCheck(user.getString("uuid"));
        });
        handler.register("check", "Checks mongodb", (args, params) -> {
            MongoDbCheck();
        });
    }
}
