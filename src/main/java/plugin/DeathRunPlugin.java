package plugin;

import arc.Events;
import arc.util.Align;
import arc.util.Timer;
import mindustry.game.EventType;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;

public class DeathRunPlugin {
    public DeathRunPlugin(){}
    int i = 60;
    public void init(){
        Timer.schedule(()->{
            for(Player player : Groups.player){
                if(player.x/8 > 250 && player.y/8 > 250){
                    player.sendMessage("hayes");
                }
            }
        }, 0f, 1f);
        Events.on(EventType.UnitDestroyEvent.class, event ->{
            if (event.unit.isPlayer()) {
                Call.announce(event.unit.getPlayer().name + " literally got 1984ed");
            } else {
                return;
            }
        });
        Timer.schedule(()-> {
            Call.infoPopup("Time until 1984: " + i, 1, Align.bottom | Align.center, 0, 0, 0,0 );
            i--;
            if (i <= 0) {
                Call.announce("1984!");
                i = 60;
            }
        },  0f, 1);
    }
}
