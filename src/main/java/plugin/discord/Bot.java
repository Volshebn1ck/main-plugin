package plugin.discord;

import arc.Core;
import arc.Events;
import arc.util.Log;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import mindustry.core.GameState;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;
import plugin.ConfigJson;
import plugin.Plugin;
import useful.Bundle;

import java.awt.*;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static arc.util.Strings.stripColors;
import static mindustry.Vars.*;
import static plugin.ConfigJson.discordurl;
import static plugin.Plugin.plrCollection;
import static plugin.discord.Embed.banEmbed;
import static plugin.utils.Checks.isAdmin;
import static plugin.utils.FindDocument.getDoc;
import static plugin.utils.MenuHandler.loginMenu;
import static plugin.utils.MenuHandler.loginMenuFunction;
import static plugin.utils.Utilities.*;

public class Bot {
    // variables for load function
    public static DiscordApi api;
    public static TextChannel channel;
    public static TextChannel banchannel;
    // main bot
    public static void load(){
        api =  new DiscordApiBuilder()
                .setToken(ConfigJson.token)
                .addIntents(Intent.GUILDS, Intent.MESSAGE_CONTENT, Intent.GUILD_MESSAGES)
                .login()
                .join();
        api.addMessageCreateListener(Bot::onMessageCreate);
        api.addSlashCommandCreateListener(Bot::addSlashCommandListener);
        channel = api.getChannelById(ConfigJson.logchannelid).get().asTextChannel().get();
        banchannel = api.getChannelById(ConfigJson.banlogchannelid).get().asTextChannel().get();
        registerSlashCommands();
        init();
    }
    // the stuff that logs if bot is started and also some random events
    public static void init(){
        Log.info("Bot started");
        Events.on(EventType.PlayerChatEvent.class, event  -> {
            if (event.message.startsWith("/")) {
                return;
            }
            channel.sendMessage("`" + event.player.plainName() + ": " + event.message + "`");
        });
        Events.on(EventType.PlayerJoin.class, event -> {
            Document user = getDoc(event.player.uuid());
            channel.sendMessage("`" + event.player.plainName() + " joined the server!" + "`");
        });
        Events.on(EventType.PlayerLeave.class, event -> {
            Document user = getDoc(event.player.uuid());
            channel.sendMessage("`" + event.player.plainName()  + " left the server!" + "`");
        });
    }
     // creating listener once message is created
    private static void onMessageCreate(MessageCreateEvent listener){
        //switch(listener.getMessageContent().split(" ")[0].replace("=", "")){
        //}
        if (listener.getChannel() == channel && listener.getMessageAuthor().isRegularUser()){
            Call.sendMessage("[blue][" + listener.getMessageAuthor().getDisplayName() + "[blue]]: [white]" + listener.getMessageContent());
        }
    }
    // registers slash commands so user can see them and use
    private static void registerSlashCommands(){
        SlashCommand banCommand = SlashCommand.with("ban", "Bans the player",
                        Arrays.asList(
                                SlashCommandOption.create(
                                        SlashCommandOptionType.LONG,
                                        "id",
                                        "id of the player",
                                        true
                                ),
                                SlashCommandOption.create(
                                        SlashCommandOptionType.LONG,
                                        "time",
                                        "Duration of ban (in days)",
                                        true
                                ),
                                SlashCommandOption.create(
                                        SlashCommandOptionType.STRING,
                                        "reason",
                                        "Reason of ban",
                                        true
                                )
                        )
        ).setDefaultEnabledForPermissions(PermissionType.MODERATE_MEMBERS)
                .createGlobal(api).join();
        SlashCommand exitCommand = SlashCommand.with("exit", "exits the servar"
        ).setDefaultEnabledForPermissions(PermissionType.ADMINISTRATOR)
                .createGlobal(api).join();
        SlashCommand listCommand = SlashCommand.with("list", "Lists the players"
        ).createGlobal(api).join();
        SlashCommand adminaddCommand = SlashCommand.with("adminadd", "gives admin to player (use it carefully)",
                        Collections.singletonList(
                                SlashCommandOption.create(
                                        SlashCommandOptionType.STRING,
                                        "name",
                                        "name of the player",
                                        true
                                ))
                ).setDefaultEnabledForPermissions(PermissionType.MODERATE_MEMBERS)
                .createGlobal(api).join();
        SlashCommand gameoverCommand = SlashCommand.with("gameover", "Executes gameover event"
        ).setDefaultEnabledForPermissions(PermissionType.MODERATE_MEMBERS)
                .createGlobal(api).join();
        SlashCommand loginCommand = SlashCommand.with("login", "Connects your discord and mindustry account!",
                Collections.singletonList(
                        SlashCommandOption.create(
                                SlashCommandOptionType.LONG,
                                "id",
                                "id",
                                true
                        ))
                ).createGlobal(api).join();
        SlashCommand getInfoCommand = SlashCommand.with("stats", "Gets stats of player",
                Arrays.asList(
                        SlashCommandOption.create(
                                SlashCommandOptionType.LONG,
                                "id",
                                "Player id"
                        ),
                        SlashCommandOption.create(
                                SlashCommandOptionType.STRING,
                                "name",
                                "Player name"
                        ))
        ).createGlobal(api).join();
        SlashCommand searchCommand = SlashCommand.with("search", "Searchs the players in db",
                Collections.singletonList(
                        SlashCommandOption.create(
                                SlashCommandOptionType.STRING,
                                "name",
                                "Player name",
                                true
                        ))
        ).createGlobal(api).join();
    }
    // calling slash command functions once they got used
    private static void addSlashCommandListener(SlashCommandCreateEvent listener) {
        if(!state.is(GameState.State.playing)){
            listener.getSlashCommandInteraction().createImmediateResponder().setContent("Server is not running.").respond();
            return;
        }
        switch(listener.getSlashCommandInteraction().getCommandName()){
            case "ban" -> {

                String response;

                int id = Math.toIntExact(listener.getSlashCommandInteraction().getOptionByName("id").get().getLongValue().get());
                String reason = listener.getSlashCommandInteraction().getOptionByName("reason").get().getStringValue().get();
                long time = listener.getSlashCommandInteraction().getOptionByName("time").get().getLongValue().get();
                Date date = new Date();
                long banTime = date.getTime() + TimeUnit.DAYS.toMillis(time);
                String timeUntilUnban = Bundle.formatDuration(Duration.ofDays(time));
                Document user = getDoc(id);
                if (user == null) {
                    response = "Could not find that player.";
                    listener.getSlashCommandInteraction()
                            .createImmediateResponder().setContent(response)
                            .respond();
                    return;
                }
                Player plr = Groups.player.find(p -> p.uuid().equals(user.getString("uuid")));
                if (isAdmin(id)) {
                    listener.getSlashCommandInteraction().createImmediateResponder().setContent("You cant ban an admin!").respond();
                    return;
                }
                if (plr == null) {
                    Log.info("Player is offline, not kicking him");
                } else {
                    plr.con.kick("[red]You have been banned!\n\n" + "[white]Reason: " + reason + "\nDuration: " + timeUntilUnban + " until unban\nIf you think this is a mistake, make sure to appeal ban in our discord: " + discordurl, 0);
                }
                listener.getSlashCommandInteraction()
                        .createImmediateResponder().setContent("Banned: " + user.getString("name"))
                        .respond();

                Call.sendMessage(user.getString("name") + " has been banned for: " + reason);
                Bson updates = Updates.combine(
                        Updates.set("lastBan", banTime)
                );
                Plugin.plrCollection.updateOne(user, updates, new UpdateOptions().upsert(true));
                Bot.banchannel.sendMessage(banEmbed(user, reason, banTime, listener.getInteraction().getUser().getName()));
                return;
            }
            case "exit" -> {
                 Log.info("Stopping server");
                 api.disconnect();
                 net.dispose();
                 Core.app.exit();
            }
            case "list" -> {
                StringBuilder list = new StringBuilder();
                list.append("```Players online: ").append(Groups.player.size()).append("\n\n");
                for (Player player : Groups.player){
                    Document user = plrCollection.find(Filters.eq("uuid", player.uuid())).first();
                    int id = user.getInteger("id");
                    if (player.admin()){
                        list.append("# [A] " + player.plainName()).append("; ID: " + id).append("\n");
                    } else {
                        list.append("# " + player.plainName()).append("; ID: " + id).append("\n");
                    }
                }
                list.append("```");
                listener.getSlashCommandInteraction()
                        .createImmediateResponder().setContent(String.valueOf(list))
                        .respond();
                return;
            }
            case "adminadd" -> {
                String name = listener.getSlashCommandInteraction().getOptionByName("name").get().getStringValue().get();
                Player player = findPlayerByName(name);
                if (player == null){
                    listener.getSlashCommandInteraction().createImmediateResponder().setContent("No such player!").respond(); return;
                }
                if (player.admin()){
                    listener.getSlashCommandInteraction().createImmediateResponder().setContent("Player is already admin!").respond(); return;
                }
                netServer.admins.adminPlayer(String.valueOf(player.id()), player.usid());
                listener.getSlashCommandInteraction().createImmediateResponder().setContent("Successfully admin " + player.plainName()).respond(); return;
            }
            case "gameover" -> {
                Events.fire(new EventType.GameOverEvent(Team.derelict));
                listener.getSlashCommandInteraction().createImmediateResponder().setContent("Gameover executed!").respond(); return;
            }
            case "login" -> {
                int id = Math.toIntExact(listener.getSlashCommandInteraction().getOptionByName("id").get().getLongValue().get());
                Document user = getDoc(id);
                if (user == null){
                    listener.getSlashCommandInteraction().createImmediateResponder().setContent("This player doesnt exists!").respond();
                    return;
                }
                Player player = Groups.player.find(p -> p.uuid().equals(user.getString("uuid")));
                if (player == null){
                    listener.getSlashCommandInteraction().createImmediateResponder().setContent("This player doesnt exists or offline!").respond();
                    return;
                }
                loginMenuFunction(listener);
                Call.menu(player.con, loginMenu, "Request", listener.getInteraction().getUser().getName() + " requests to connect your mindustry account", new String[][]{{"Connect"}, {"Cancel"}});
                listener.getSlashCommandInteraction().createImmediateResponder().setContent("req sended!").respond();
            }
            case "stats" -> {
                int id = Math.toIntExact(listener.getSlashCommandInteraction().getOptionByName("id").flatMap(SlashCommandInteractionOption::getLongValue).orElse(2147483647L));
                String name= listener.getSlashCommandInteraction().getOptionByName("name").flatMap(SlashCommandInteractionOption::getStringValue).orElse("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAa");
                Document user = notNullElse(getDoc(id), getDoc(name));
                if (user == null){
                    listener.getSlashCommandInteraction().createImmediateResponder().setContent("Could not find that player!").respond();
                    return;
                }
                String discordId = String.valueOf(user.getLong("discordid"));
                if (discordId.equals("0")){
                    discordId = "none";
                }
                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle("Information")
                        .setColor(Color.RED)
                        .addField("Name", stripColors(user.getString("name")))
                        .addField("ID", String.valueOf(user.getInteger("id")))
                        .addField("Rank", String.valueOf(user.getInteger("rank")))
                        .addField("Discord (if linked)", "<@" +discordId +">");
                listener.getSlashCommandInteraction().createImmediateResponder().addEmbed(embed).respond();
            }
            case "search" -> {
                String name= listener.getSlashCommandInteraction().getOptionByName("name").flatMap(SlashCommandInteractionOption::getStringValue).orElse("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAa");
                StringBuilder list = new StringBuilder();
                Pattern pattern = Pattern.compile(".?" +name + ".?", Pattern.CASE_INSENSITIVE);
                list.append("```Results:\n\n");
                MongoCursor<Document> cursor =  plrCollection.find(Filters.regex("name", pattern)).limit(10).iterator();
                try {
                    while(cursor.hasNext()) {
                        Document csr = cursor.next();
                        list.append(csr.get("name")).append("; ID: ").append(csr.get("id")).append("\n");
                    }
                } finally {
                    cursor.close();
                }
                list.append("```");
                listener.getSlashCommandInteraction().createImmediateResponder().setContent(String.valueOf(list)).respond();
            }
          }
        }
    }
