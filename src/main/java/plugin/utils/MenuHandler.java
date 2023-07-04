package plugin.utils;

import mindustry.Vars;
import mindustry.gen.Call;
import mindustry.gen.Player;
import mindustry.ui.Menus;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import plugin.Ploogin;
import plugin.discord.Bot;


public class MenuHandler {
        public static Player plr = Ploogin.victim;
        public static Player moderator = Ploogin.moderator;
        public static int banMenu = Menus.registerMenu((player, option) -> {
            switch (option) {
                case -1 -> {
                    return;
                }
                case 0 -> {
                    Vars.netServer.admins.banPlayerIP(plr.ip());
                    Vars.netServer.admins.banPlayer(plr.uuid());
                    plr.con.kick("You have been banned for: " + Ploogin.reason);
                    Call.sendMessage(plr.plainName() + " has been banned for: " + Ploogin.reason);
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
