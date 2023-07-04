package plugin.utils;

import mindustry.Vars;
import mindustry.gen.Call;
import mindustry.gen.Player;
import mindustry.ui.Menus;
import plugin.Ploogin;
import plugin.discord.Bot;


public class MenuHandler {
        public static Player plr = Ploogin.victim;
        public static int banMenu = Menus.registerMenu((player, option) -> {
            switch (option) {
                case -1 -> {
                    return;
                }
                case 0 -> {
                    Vars.netServer.admins.banPlayerIP(plr.ip());
                    Vars.netServer.admins.banPlayer(plr.uuid());
                    Call.sendMessage(plr.plainName() + " has been banned for: " + Ploogin.reason);
                    Bot.channel.sendMessage(plr.plainName() + " has been banned for: " + Ploogin.reason);
                }
                case 1 -> {
                    return;
                }
            }
        });
    }
