package plugin.functions;

import mindustry.gen.Call;
import mindustry.gen.Player;
import plugin.utils.MenuHandler;

public class Other {
    public static void welcomeMenu(Player player){
        String title = "\uE86B Welcome!";
        String description = "[orange]Welcome to our server!\n\n" +
                "[red]<[orange]Rules[red]>\n" +
                "[#f]\uE815 [orange]Do not grief or sabotage your team.\n" +
                "[#f]\uE815 [orange]Do not build/write any NSFW or offensive content.\n" +
                "[#f]\uE815 [orange]Do not try lag the server using lag machines or similar stuff.\n" +
                "[green]\uE800 [orange]Use common sense, do not be toxic/mean to others.\n\n" +
                "[orange]Write /help to see all commands that are available on server.\n" +
                "[blue]\uE80D Also make sure to join our discord.";
        String button1 = "Close";
        String button2 = "[blue]\uE80D Join our discord!";
        Call.menu(player.con, MenuHandler.welcomeMenu, title, description, new String[][]{{button1}, {button2}});
    }
}
