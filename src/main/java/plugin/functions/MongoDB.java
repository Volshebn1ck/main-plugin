package plugin.functions;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import static plugin.Ploogin.playerCollection;
import static plugin.utils.FindDocument.getDoc;

public class MongoDB {
    public static void MongoDbPlayerCreation(Player eventPlayer){
        long lastBan = 0;
        long discordid = 0;
        var id = new ObjectId();
        Document plrDoc = new Document("_id", id);
        plrDoc.append("id", (int) playerCollection.countDocuments());
        plrDoc.append("uuid", eventPlayer.uuid());
        plrDoc.append("name", eventPlayer.plainName());
        plrDoc.append("rawName", eventPlayer.name());
        plrDoc.append("rank", 0);
        plrDoc.append("lastBan", lastBan);
        plrDoc.append("discordid", discordid);
        Document chk = playerCollection.find(Filters.eq("uuid", eventPlayer.uuid())).first();
        if (chk == null){
            playerCollection.insertOne(plrDoc);
        } else {
            return;
        }
    }
    public static void MongoDbPlayerRankCheck(String uuid){
        Player eventPlayer = Groups.player.find(p -> p.uuid().contains(uuid));
        Document user = playerCollection.find(Filters.eq("uuid", uuid)).first();
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
        }
    }
    public static void MongoDbPlayerNameCheck(Player player){
        Document user = getDoc(player.uuid());
        if (player.plainName() != user.getString("name") && player.name() != user.getString("rawName")){
            Bson updates = Updates.combine(
                    Updates.set("name", player.plainName()),
                    Updates.set("rawName", player.name())
            );
            playerCollection.updateOne(user, updates, new UpdateOptions().upsert(true));
        }
    }
}
