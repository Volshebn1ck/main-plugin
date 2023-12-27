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
import mindustry.server.ServerControl;
import org.bson.Document;
import org.javacord.api.event.message.MessageCreateEvent;
import plugin.models.PlayerData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static plugin.commands.MainCommands.votedPlayer;
import static plugin.commands.MainCommands.votes;
import static plugin.utils.FindDocument.getPlayerData;

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
    public static String formatRanks(int rank){
        switch (rank){
            case 0: return "player";
            case 1: return "trusted";
            case 2: return "admin";
            case 3: return "console";
            case 4: return "owner";
        }
        return "player";
    }
}

