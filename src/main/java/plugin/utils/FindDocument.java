package plugin.utils;

import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.regex.Pattern;

import static arc.util.Strings.stripColors;
import static com.mongodb.client.model.Filters.eq;
import static plugin.Ploogin.playerCollection;
import static plugin.utils.Utilities.notNullElse;

public class FindDocument {
    public static Document getDoc(int id){
        return playerCollection.find(eq("id", id)).first();
    }
    public static Document getDoc(String uuidOrName){
        Pattern pattern = Pattern.compile(".?" +uuidOrName + ".?", Pattern.CASE_INSENSITIVE);
        return notNullElse(playerCollection.find(Filters.eq("uuid", uuidOrName)).first(), playerCollection.find(Filters.regex("name", pattern)).first());
    }
}
