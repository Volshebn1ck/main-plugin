package plugin.utils;
import arc.Events;
import arc.func.Func;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.maps.Map;
import mindustry.server.ServerControl;
import java.util.ArrayList;

import static plugin.commands.MainCommands.votedPlayer;
import static plugin.commands.MainCommands.votes;

public class Utilities {
    // finds player using their name (without colors)
    public static Player findPlayerByName(String name){
        return Groups.player.find(t-> t.plainName().contains(name));
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
        ServerControl.instance.setNextMap(map);
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
    public static <T> String stringify(ArrayList<T> arr, Func<T, String> stringer) {
        if (arr == null || arr.isEmpty()) return "";
        StringBuilder out = new StringBuilder();
        for (T elem: arr) {
            out.append(stringer.get(elem));
        }
        return out.toString();
    }
}

