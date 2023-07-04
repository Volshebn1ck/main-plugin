package plugin.utils;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import mindustry.Vars;
import mindustry.gen.Call;
import mindustry.gen.Player;
import mindustry.ui.Menus;
import org.bson.BsonArray;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import plugin.Ploogin;
import plugin.discord.Bot;

import java.util.Date;


public class MenuHandler {
        public static Player plr = Ploogin.victim;
        public static Player moderator = Ploogin.moderator;
        public static int banMenu = Menus.registerMenu((player, option) -> {
            switch (option) {
                case -1 -> {
                    return;
                }
                case 0 -> {
                    Date date = new Date();
                    long banTime = date.getTime() + Ploogin.time*1000;
                    /*Vars.netServer.admins.banPlayerIP(plr.ip());
                    Vars.netServer.admins.banPlayer(plr.uuid());*/
                    plr.con.kick("You have been banned for: " + Ploogin.reason);
                    Call.sendMessage(plr.plainName() + " has been banned for: " + Ploogin.reason);
                    Document usr = Ploogin.playerCollection.find(Filters.eq("uuid", plr.uuid())).first();
                    Bson updates = Updates.combine(
                            Updates.set("lastBan", banTime)
                    );
                    Ploogin.playerCollection.updateOne(usr, updates, new UpdateOptions().upsert(true));
                    EmbedBuilder embed = new EmbedBuilder()
                            .setTitle("Ban event")
                            .setDescription(plr.plainName() + " has been banned for: " + Ploogin.reason)
                            .addField("Moderator", moderator.plainName());
                    Bot.banchannel.sendMessage(embed);
                }
                case 1 -> {
                    return;
                }
            }
        });
    }
