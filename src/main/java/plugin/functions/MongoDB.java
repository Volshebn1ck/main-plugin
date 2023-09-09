package plugin.functions;

import arc.struct.ObjectSet;
import arc.util.Log;
import arc.util.Timer;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import mindustry.Vars;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.net.Administration;
import mindustry.net.NetConnection;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static plugin.Plugin.plrCollection;
import static plugin.utils.FindDocument.getDoc;
import static plugin.utils.FindDocument.getDocByIP;

public class MongoDB {
    public static List<String> achievements = new ArrayList<>();
    public static void MongoDbPlayerCreation(Player eventPlayer){
        long lastBan = 0;
        long discordid = 0;
        var id = new ObjectId();
        Document plrDoc = new Document("_id", id);
        plrDoc.append("id", (int) plrCollection.countDocuments());
        plrDoc.append("uuid", eventPlayer.uuid());
        plrDoc.append("name", eventPlayer.plainName());
        plrDoc.append("rawName", eventPlayer.name());
        plrDoc.append("rank", 0);
        plrDoc.append("lastBan", lastBan);
        plrDoc.append("discordid", discordid);
        plrDoc.append("playtime", 0);
        plrDoc.append("achievements", achievements);
        plrDoc.append("ip", eventPlayer.con().address);
        plrDoc.append("joinmessage", "@ [white]joined");
        Document chk = plrCollection.find(Filters.eq("uuid", eventPlayer.uuid())).first();
        if (chk == null){
            plrCollection.insertOne(plrDoc);
        } else {
            return;
        }
    }
    public static void MongoDbPlayerRankCheck(String uuid){
        Player eventPlayer = Groups.player.find(p -> p.uuid().contains(uuid));
        Document user = plrCollection.find(Filters.eq("uuid", uuid)).first();
        String tempName = user.getString("rawName");
        int rank = user.getInteger("rank");
        switch (rank){
            case 0 ->{
                eventPlayer.name = "[white]<P> [orange]" + tempName;
            }
            case 1 ->{
                eventPlayer.name = "[blue]<T> [orange]" + tempName;
            }
            case 2 ->{
                eventPlayer.name = "[#f]<A> [orange]" + tempName;
            }
            case 3 ->{
                eventPlayer.name = "[purple]<C> [orange]" + tempName;
            }
            case 4 -> {
                eventPlayer.name = "[cyan]<O> [orange]" + tempName;
            }
        }
    }
    public static void MongoDbPlayerNameCheck(Player player){
        Document user = getDoc(player.uuid());
        if (player.plainName() != user.getString("name") && player.name() != user.getString("rawName")){
            MongoDbUpdate(user, Updates.set("name", player.plainName()), Updates.set("rawName", player.name()));
        }
    }
    public static void MongoDbPlayerIpCheck(NetConnection player){
        Document user = getDocByIP(player.address);
        if (user == null){
            return;
        }
        if (player.address != user.getString("ip")){
            MongoDbUpdate(user, Updates.set("ip", player.address));
        }
    }
    public static void MongoDbUpdate(Document user, Bson... updates){
        Bson update = Updates.combine(
                updates
        );
        plrCollection.updateOne(user, update, new UpdateOptions().upsert(true));
    }
    public static void MongoDbPlaytimeTimer(){
        Timer.schedule(() -> {
            for (Player player : Groups.player){
                Document user = getDoc(player.uuid());
                int playtime = (int) user.getOrDefault("playtime", 0) + 1;
                MongoDbUpdate(user, Updates.set("playtime", playtime));
            }
        }, 0, 60);
    }
    public static void MongoDbCheck(){
        try (MongoCursor<Document> cursor = plrCollection.find().iterator()) {
            while (cursor.hasNext()) {
                Document csr = cursor.next();
                ObjectSet<Administration.PlayerInfo> ip = Vars.netServer.admins.findByName(csr.getString("uuid"));
                if (ip.size != 0) {
                    appendIfNull(csr, "joinmessage", "@ [white]joined");
                }
            }
        }
    }
    public static void appendIfNull(Document user, String key, Object defaultValue){
        if (user.get(key) == null){
            MongoDbUpdate(user, Updates.set(key, defaultValue));
        }
    }
}
