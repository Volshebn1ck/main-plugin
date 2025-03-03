package plugin.models;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import mindustry.gen.Player;
import mindustry.net.NetConnection;
import plugin.etc.Ranks;

import java.util.ArrayList;
import java.util.Optional;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Filters.eq;
import static plugin.Plugin.players;

public class PlayerData {
    private final PlayerDataCollection collection;

    public static ArrayList<PlayerData> findByName(String name) {
        ArrayList<PlayerData> output = new ArrayList<>();
        Pattern pattern = Pattern.compile(".?" + name + ".?", Pattern.CASE_INSENSITIVE);
        try (MongoCursor<PlayerDataCollection> cursor = players.find(Filters.regex("name", pattern)).limit(25).iterator()) {
            while (cursor.hasNext())
                output.add(new PlayerData(cursor.next()));
        }
        return output;
    }

    public static ArrayList<PlayerData> findByIp(String ip) {
        ArrayList<PlayerData> output = new ArrayList<>();
        try (MongoCursor<PlayerDataCollection> cursor = players.find(Filters.in("ips", ip)).limit(25).iterator()) {
            while (cursor.hasNext())
                output.add(new PlayerData(cursor.next()));
        }
        return output;
    }

    public PlayerData(PlayerDataCollection collection){
        this.collection = collection;
    }
    public PlayerData(int id) {
        collection = players.find(eq("_id", id)).first();
    }

    public PlayerData(String uuid) {
        collection = players.find(eq("uuid", uuid)).first();
    }
    public PlayerData(NetConnection player){
        collection = players.find(eq("uuid", player.uuid)).first();
    }
    public PlayerData(Player player) {
        collection = Optional.ofNullable(players.find(eq("uuid", player.uuid())).first()).orElse(
                new PlayerDataCollection(getNextID(), player.uuid()));
        if (!collection.names.contains(player.plainName())) collection.names.add(player.plainName());
        collection.rawName = player.name();
        if (!collection.ips.contains(player.con.address)) collection.ips.add(player.con.address);
        commit();
    }

    public boolean isExist() {
        return collection != null;
    }

    public boolean isNotExist() {
        return collection == null;
    }


    public void commit() {
        players.replaceOne(eq("_id", collection.id), collection, new ReplaceOptions().upsert(true));
    }

    public static int getNextID() {
        PlayerDataCollection data = players.find().sort(new BasicDBObject("_id", -1)).first();
        return (data == null) ? 0 : data.id + 1;
    }

    //setters
    public void setLastBanTime(long time) {
        collection.lastBan = time;
        commit();
    }

    public void setRank(Ranks.Rank rank) {
        collection.rank = rank.ordinal();
        commit();
    }
    public void setDiscordId(long id){
        collection.discordId = id;
        commit();
    }

    public void setRank(String rank) {
        collection.rank = Ranks.getRank(rank).ordinal();
        commit();
    }

    public void setVip(boolean isVip) {
        collection.isVip = isVip;
        commit();
    }
    public void setJoinMessage(String message){
        collection.joinMessage = message;
        commit();
    }
    //mutators
    public void addAchievement(String ach){
        collection.achievements.add(ach);
        commit();
    }
    public void removeAchievement(String ach){
        collection.achievements.remove(ach);
        commit();
    }
    public void playtimeIncrease() {
        collection.playtime++;
        commit();
    }

    //getters
    public int getId() {
        return collection.id;
    }

    ;

    public String getUuid() {
        return collection.uuid;
    }

    ;

    public ArrayList<String> getNames() {
        return collection.names;
    }

    ;

    public String getLastName() {
        return collection.names.get(collection.names.size() - 1);
    }

    public String getRawName() {
        return collection.rawName;
    }

    ;

    public Ranks.Rank getRank() {
        return Ranks.getRank(collection.rank);
    }

    ;

    public String getJoinMessage() {
        return collection.joinMessage;
    }

    ;

    public ArrayList<String> getIPs() {
        return collection.ips;
    }

    ;

    public long getLastBanTime() {
        return collection.lastBan;
    }

    ;

    public long getDiscordId() {
        return collection.discordId;
    }

    public ArrayList<String> getAchievements() {
        return collection.achievements;
    }

    ;

    public int getPlaytime() {
        return collection.playtime;
    }

    ;

    public boolean isVip() {
        return collection.isVip;
    }
}
