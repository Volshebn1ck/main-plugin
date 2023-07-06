package plugin.utils;

import com.mongodb.client.model.Filters;
import org.bson.Document;

import static plugin.Ploogin.playerCollection;

public class FindDocument {
    public static Document getDoc(int id){
        return playerCollection.find(Filters.eq("id", id)).first();
    }
    public static Document getDoc(String uuid){
        return playerCollection.find(Filters.eq("uuid", uuid)).first();
    }
}
