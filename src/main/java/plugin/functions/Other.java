package plugin.functions;

import mindustry.gen.Call;
import mindustry.gen.Player;
import plugin.utils.MenuHandler;

public class Other {
    public static void welcomeMenu(Player player){
        String title = "Welcome message";
        String description = "[orange]Welcome to our server! Before we begin, make sure to read basic rules.\n" +
                "[white]- Do not grief or sabotage your team.\n" +
                "- Do not build/write any NSFW or offensive content.\n" +
                "- Do not try lag the server using lag machines or similar stuff.\n" +
                "- Use common sense, do not be toxic/mean to others.\n" +
                "[orange]Write /help to see all commands that are available on server.\n" +
                "Also make sure to join our discord.";
        String button1 = "Close";
        String button2 = "[blue]Join our discord!";
        Call.menu(player.con, MenuHandler.welcomeMenu, title, description, new String[][]{{button1}, {button2}});
    }
}
