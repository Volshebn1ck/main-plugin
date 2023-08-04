package plugin.utils;
import arc.Core;
import arc.Events;
import arc.struct.Seq;
import arc.util.Reflect;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.maps.Map;
import mindustry.net.WorldReloader;
import mindustry.server.ServerControl;
import org.bson.Document;

import java.util.concurrent.atomic.AtomicInteger;

import static mindustry.Vars.logic;
import static plugin.commands.MainCommands.votedPlayer;
import static plugin.commands.MainCommands.votes;
import static plugin.utils.FindDocument.getDoc;

public class Utilities {
    // finds player using their name (without colors)
    public static Player findPlayerByName(String name){
        return Groups.player.find(t-> t.plainName().contains(name));
    }
    public static Player findPlayerByID(int id){
        Document user = getDoc(id);
        return Groups.player.find(t-> t.uuid().equals(user.getString("uuid")));
    }
    public static <T> T notNullElse(T value, T value2){
        return value != null ? value : value2;
    }
    public static void voteCanceled(){
        Call.sendMessage("[red]Vote has been canceled!");
        votes.set(0);
        votedPlayer.clear();
    }
    public static void voteSuccess(Map map){
        Call.sendMessage("[green]Vote success! Changing map!");
        Reflect.set(ServerControl.instance, "nextMapOverride", map);
        Events.fire(new EventType.GameOverEvent(Team.derelict));
        votes.set(0);
        votedPlayer.clear();
    }
    public static Seq<Map> getMaps(){
        Seq<Map> maps = new Seq<>();
        for(Map map : Vars.maps.customMaps()){
            maps.add(map);
        }
        return maps;
    }
}

