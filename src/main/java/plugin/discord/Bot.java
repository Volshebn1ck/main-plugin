package plugin.discord;

import arc.Core;
import arc.Events;
import arc.util.Log;
import com.mongodb.client.model.Filters;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.core.GameState;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.maps.Map;
import mindustry.net.Packets;
import org.bson.Document;
import org.javacord.api.*;
import mindustry.mod.*;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.interaction.*;
import plugin.utils.Utilities;
import java.util.Collections;
import mindustry.gen.Player;
import plugin.ConfigJson;
import static mindustry.Vars.*;
import static plugin.Ploogin.playerCollection;

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
        Events.on(EventType.PlayerChatEvent.class, event  ->
            channel.sendMessage("`"+event.player.plainName() + ": " + event.message+"`")
        );
        Events.on(EventType.PlayerJoin.class, event ->
            channel.sendMessage("`"+event.player.plainName() + " joined the server!"+"`")
        );
        Events.on(EventType.PlayerLeave.class, event ->
            channel.sendMessage("`"+event.player.plainName() + " left the server!"+"`")
        );
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
                Collections.singletonList(
                        SlashCommandOption.create(
                                SlashCommandOptionType.STRING,
                                "name",
                                "name of the player"
                )
                )
        ).setDefaultEnabledForPermissions(PermissionType.KICK_MEMBERS)
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
                                "name of the player"
                        )
                )
                ).setDefaultEnabledForPermissions(PermissionType.KICK_MEMBERS)
                .createGlobal(api).join();
        SlashCommand gameoverCommand = SlashCommand.with("gameover", "Executes gameover event"
        ).setDefaultEnabledForPermissions(PermissionType.KICK_MEMBERS)
                .createGlobal(api).join();
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

                String PlayerName = listener.getSlashCommandInteraction().getOptionByName("name").get().getStringValue().get();
                Player player = Utilities.findPlayerByName(PlayerName);
                if (player == null) {
                    response = "Could not find that player.";
                    listener.getSlashCommandInteraction()
                            .createImmediateResponder().setContent(response)
                            .respond();
                    return;
                }
                if (player.admin()){
                    listener.getSlashCommandInteraction().createImmediateResponder().setContent("You cant ban an admin!").respond(); return;
                }
                netServer.admins.banPlayerID(player.uuid());
                netServer.admins.banPlayerIP(netServer.admins.getInfo(player.uuid()).lastIP);
                player.con.kick(Packets.KickReason.banned);
                listener.getSlashCommandInteraction()
                        .createImmediateResponder().setContent("Banned: " + PlayerName)
                        .respond();
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
                    Document user = playerCollection.find(Filters.eq("uuid", player.uuid())).first();
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
                String PlayerName = listener.getSlashCommandInteraction().getOptionByName("name").get().getStringValue().get();
                Player player = Utilities.findPlayerByName(PlayerName);
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
          }
        }
    }
