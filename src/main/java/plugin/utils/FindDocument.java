package plugin.utils;

import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.regex.Pattern;

import static arc.util.Strings.canParseInt;
import static arc.util.Strings.parseInt;
import static com.mongodb.client.model.Filters.eq;
import static plugin.Plugin.plrCollection;
import static plugin.utils.Utilities.notNullElse;

public class FindDocument {
    public static Document getDoc(int id){
        return plrCollection.find(eq("id", id)).first();
    }
    public static Document getDoc(String uuidOrName) {
        try {
            Pattern pattern = Pattern.compile(".?" + uuidOrName + ".?", Pattern.CASE_INSENSITIVE);
            return notNullElse(plrCollection.find(Filters.eq("uuid", uuidOrName)).first(), plrCollection.find(Filters.regex("name", pattern)).first());
        } catch (Exception e) {
            return notNullElse(plrCollection.find(Filters.eq("uuid", uuidOrName)).first(), plrCollection.find(Filters.eq("name", uuidOrName)).first());
        }
    }
    public static Document getDocByIP(String ip){
        return plrCollection.find(eq("ip", ip)).first();
    }
    public static Document getDocAnyway(String uuidOrNameOrIDOrIp){
        if (canParseInt(uuidOrNameOrIDOrIp)){
            int i = parseInt(uuidOrNameOrIDOrIp);
            return plrCollection.find(Filters.eq("id", i)).first();
        } else {
            return notNullElse(getDoc(uuidOrNameOrIDOrIp), getDocByIP(uuidOrNameOrIDOrIp));
        }
    };
}
