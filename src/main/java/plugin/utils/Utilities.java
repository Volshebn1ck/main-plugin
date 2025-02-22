package plugin.utils;
import arc.Events;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.maps.Map;
import mindustry.server.ServerControl;
import plugin.models.PlayerData;

import static plugin.commands.MainCommands.votedPlayer;
import static plugin.commands.MainCommands.votes;

public class Utilities {
    // finds player using their name (without colors)
    public static Player findPlayerByName(String name){
        return Groups.player.find(t-> t.plainName().contains(name));
    }
    public static Player findPlayerByID(int id){
        PlayerData data = FindDocument.getPlayerData(id);
        return Groups.player.find(t-> t.uuid().equals(data.uuid));
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
}

