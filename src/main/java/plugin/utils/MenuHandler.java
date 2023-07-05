package plugin.utils;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import mindustry.gen.Call;
import mindustry.gen.Player;
import mindustry.ui.Menus;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import plugin.Ploogin;
import plugin.discord.Bot;
import useful.Bundle;
import useful.text.TextInput;

import java.awt.*;
import java.util.Date;

import static plugin.ConfigJson.discordurl;


public class MenuHandler {
        public static Player plr = Ploogin.victim;
        public static Player moderator = Ploogin.moderator;
        public static int banMenu = Menus.registerMenu((player, option) -> {
            switch (option) {
                case -1 -> {
                    return;
                }
                case 0 -> {
                    Document usr = Ploogin.playerCollection.find(Filters.eq("uuid", plr.uuid())).first();
                    Date date = new Date();
                    long banTime = date.getTime() + Ploogin.time*86400000;
                    String timeUntilUnban = Bundle.formatDuration(Ploogin.time*86400000);
                    plr.con.kick("You have been banned for: " + Ploogin.reason + ". Wait " + timeUntilUnban + " until unban!", 0);
                    Call.sendMessage(plr.plainName() + " has been banned for: " + Ploogin.reason);
                    Bson updates = Updates.combine(
                            Updates.set("lastBan", banTime)
                    );
                    Ploogin.playerCollection.updateOne(usr, updates, new UpdateOptions().upsert(true));
                    EmbedBuilder embed = new EmbedBuilder()
                            .setTitle("Ban event")
                            .setDescription(plr.plainName() + " has been banned for: " + Ploogin.reason)
                            .setColor(Color.RED)
                            .addField("Moderator", moderator.plainName());
                    Bot.banchannel.sendMessage(embed);
                }
                case 1 -> {
                    return;
                }
            }
        });
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

}
