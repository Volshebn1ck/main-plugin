package plugin;

import arc.util.Log;
import mindustry.Vars;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ConfigJson {
    // literally variables for other shit
    public static String token;
    public static String banlogchannelid;
    public static String logchannelid;
    public static String mongodburl;
    public static String discordurl;
    // reads variables from config.json
    public static void read() throws IOException, ParseException {
        try {
            JSONObject object = (JSONObject) new JSONParser().parse(new
                    FileReader(Vars.dataDirectory.absolutePath() + "/config.json"));
            token = (String) object.get("token");
            logchannelid = (String) object.get("logchannelid");
            banlogchannelid = (String) object.get("banlogchannelid");
            mongodburl = (String) object.get("mongodburl");
            discordurl = (String) object.get("discordurl");
        }catch(Exception e){
            Log.err("Skill issue: " + e);
        }
    }
}
