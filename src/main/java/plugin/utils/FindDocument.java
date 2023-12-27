package plugin.utils;

import com.mongodb.client.model.Filters;
import plugin.models.PlayerData;

import java.util.regex.Pattern;

import static arc.util.Strings.canParseInt;
import static arc.util.Strings.parseInt;
import static com.mongodb.client.model.Filters.eq;
import static plugin.Plugin.newCollection;
import static plugin.utils.Utilities.notNullElse;

public class FindDocument {
    public static PlayerData getPlayerData(int id){
        return newCollection.find(eq("_id", id)).first();
    }
    public static PlayerData getPlayerData(String uuidOrName) {
        try {
            Pattern pattern = Pattern.compile(".?" + uuidOrName + ".?", Pattern.CASE_INSENSITIVE);
            return notNullElse(newCollection.find(Filters.eq("uuid", uuidOrName)).first(), newCollection.find(Filters.regex("name", pattern)).first());
        } catch (Exception e) {
            return notNullElse(newCollection.find(Filters.eq("uuid", uuidOrName)).first(), newCollection.find(Filters.eq("name", uuidOrName)).first());
        }
    }
    public static PlayerData getPlayerDataByIP(String ip){
        return newCollection.find(eq("ip", ip)).first();
    }
    public static PlayerData getPlayerDataAnyway(String uuidOrNameOrIDOrIp){
        if (canParseInt(uuidOrNameOrIDOrIp)){
            int i = parseInt(uuidOrNameOrIDOrIp);
            return newCollection.find(Filters.eq("_id", i)).first();
        } else {
            return notNullElse(getPlayerData(uuidOrNameOrIDOrIp), getPlayerDataByIP(uuidOrNameOrIDOrIp));
        }
    };
}
