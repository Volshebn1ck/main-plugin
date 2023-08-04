package plugin.utils;

import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.regex.Pattern;

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
}
