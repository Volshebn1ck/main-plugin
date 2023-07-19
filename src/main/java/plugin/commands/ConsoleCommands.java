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

import static plugin.Plugin.plrCollection;
import static plugin.functions.MongoDB.MongoDbPlayerRankCheck;
import static plugin.functions.MongoDB.MongoDbUpdate;

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
            int id = 0;
            try {
                id = Integer.parseInt(args[0]);
            } catch (NumberFormatException e){
                Log.warn("Please, type an ID");
                return;
            }
            int rankid = Integer.parseInt(args[1]);
            Document user = plrCollection.find(Filters.eq("id", id)).first();
            if (user == null){
                Log.warn("This user doesnt exist!");
                return;
            }
            MongoDbUpdate(user, Updates.set("rank", rankid));
            Log.info("Rank has been given!");
            Player player = Groups.player.find(p -> p.uuid().equals(user.getString("uuid")));
            if (player == null){
                return;
            }
            MongoDbPlayerRankCheck(user.getString("uuid"));
        });
    }
}
