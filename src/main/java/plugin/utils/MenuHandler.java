package plugin.utils;

import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import mindustry.gen.Call;
import mindustry.gen.Player;
import mindustry.ui.Menus;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import plugin.Ploogin;

import static plugin.ConfigJson.discordurl;
import static plugin.utils.FindDocument.getDoc;


public class MenuHandler {
        public static Player plr = Ploogin.victim;
        public static int welcomeMenu = Menus.registerMenu(((player, option) -> {
            switch (option){
                case -1 -> {
                    return;
                }
                case 0 -> {
                    return;
                }
                case 1 -> {
                    Call.openURI(player.con, discordurl);
                }
            }
        }));
        public static int loginMenu;
        public static void loginMenuFunction(SlashCommandCreateEvent listener){
            loginMenu = Menus.registerMenu(((player, option) -> {
                switch (option){
                    case -1 -> {
                        return;
                    }
                    case 0 -> {
                        long discordId = listener.getInteraction().getUser().getId();
                        Document user = getDoc(player.uuid());
                        Bson updates;
                        if (user.getInteger("rank") == 0){
                            updates = Updates.combine(
                                    Updates.set("discordid", discordId),
                                    Updates.set("rank", 1)
                            );
                        } else{
                            updates = Updates.combine(
                                    Updates.set("discordid", discordId)
                            );
                        }
                        Ploogin.playerCollection.updateOne(user, updates, new UpdateOptions().upsert(true));
                        player.sendMessage("[blue]Successfully connected your discord: " + listener.getInteraction().getUser().getName());
                        listener.getSlashCommandInteraction().createImmediateResponder().setContent("Successfully connected your mindustry account!").respond();
                    }
                    case 1 -> {
                        return;
                    }
                }
        }));
    }
}
