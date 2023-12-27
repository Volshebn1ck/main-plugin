package plugin;

import arc.util.Log;
import mindustry.Vars;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.file.Files;

public class ConfigJson {
    // literally variables for other shit
    public static String token;
    public static String banlogchannelid;
    public static String logchannelid;
    public static String mongodburl;
    public static String discordurl;
    public static String moderatorid;
    public static String adminid;
    public static String prefix;
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
            moderatorid = (String) object.get("moderatorid");
            adminid= (String) object.get("adminid");
            prefix = (String) object.get("prefix");
        }catch(Exception e){
            Log.err("Config reading failed! Creating config file! Make sure to setup everything to prevent further errors!");
            File config = new File(Vars.dataDirectory.absolutePath() + "/" + "config.json");
            FileWriter writer = new FileWriter(Vars.dataDirectory.absolutePath() + "/" + "config.json");
            writer.write(
                    "{\n" +
                            "    \"token\": \"bot token\",\n" +
                            "    \"logchannelid\": \"channel where messages will be logged\",\n" +
                            "    \"banlogchannelid\": \"channel where bans will be logged\",\n" +
                            "    \"mongodburl\": \"Mongo Database URL connection\",\n" +
                            "    \"discordurl\": \"Discord invite code\",\n" +
                            "    \"moderatorid\": \"Role ID of moderator\",\n" +
                            "    \"adminid\": \"Role ID of admin\"\n" +
                            "    \"prefix\": \"bot prefix\"\n" +
                            "}\n"
            );
            writer.close();
        }
    }
}
