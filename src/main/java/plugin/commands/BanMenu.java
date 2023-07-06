package plugin.commands;

import arc.Events;
import arc.util.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import mindustry.game.EventType.AdminRequestEvent;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.net.Administration.TraceInfo;
import mindustry.net.Packets.KickReason;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import plugin.Ploogin;
import plugin.discord.Bot;
import useful.*;
import useful.text.TextInput;
import useful.State.StateKey;

import java.awt.*;
import java.time.Duration;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static mindustry.Vars.*;
import static plugin.ConfigJson.discordurl;
import static plugin.discord.Embed.banEmbed;

public class BanMenu {

    public static final StateKey<Player> TARGET = new StateKey<>("target");
    public static final StateKey<Integer> DURATION = new StateKey<>("duration");

    public static final TextInput
            durationInput = new TextInput(),
            reasonInput = new TextInput();

    public static void loadBanMenu() {
        net.handleServer(AdminRequestCallPacket.class, (con, packet) -> {
            var player = con.player;
            var other = packet.other;

            if (!player.admin || other == null || (other.admin && other != player)) {
                return;
            }

            var action = packet.action;
            var params = packet.params;

            Events.fire(new AdminRequestEvent(player, other, action));

            switch (action) {
                case wave -> {
                    logic.skipWave();
                    Log.info("&lc@ &fi&lk[&lb@&fi&lk]&fb has skipped the wave.", player.plainName(), player.uuid());
                }

                case ban -> durationInput.show(player, TARGET, other);

                case kick -> {
                    other.kick(KickReason.kick);
                    Log.info("&lc@ &fi&lk[&lb@&fi&lk]&fb has kicked @ &fi&lk[&lb@&fi&lk]&fb.", player.plainName(), player.uuid(), other.plainName(), other.uuid());
                }

                case trace -> {
                    Call.traceInfo(player.con, other, new TraceInfo(
                            other.ip(),
                            other.uuid(),
                            other.con.modclient,
                            other.con.mobile,
                            other.getInfo().timesJoined,
                            other.getInfo().timesKicked,
                            other.getInfo().ips.toArray(String.class),
                            other.getInfo().names.toArray(String.class)
                    ));

                    Log.info("&lc@ &fi&lk[&lb@&fi&lk]&fb has requested trace info of @ &fi&lk[&lb@&fi&lk]&fb.", player.plainName(), player.uuid(), other.plainName(), other.uuid());
                }

                case switchTeam -> {
                    if (params instanceof Team team)
                        other.team(team);
                }
            }
        });

        durationInput.transform(input -> {
            input.title("Ban duration (in days)");
            input.content("Write ban duration (in days)");

            input.textLength(4);

            input.result((view, text) -> {
                if (!Strings.canParsePositiveInt(text)) {
                    Action.show().get(view);
                    return;
                }

                Action2.openWith(reasonInput, DURATION).get(view, Strings.parseInt(text));
            });
        });

        reasonInput.transform(input -> {
            input.title("Ban reason");
            input.content("Write ban reason:");

            input.textLength(64);
            input.defaultText("No reason");

            input.closed(Action.back());
            input.result((view, text) -> {
                var target = view.state.get(TARGET);
                long duration = view.state.get(DURATION);
                Document user = Ploogin.playerCollection.find(Filters.eq("uuid", target.uuid())).first();
                Date date = new Date();
                long banTime = date.getTime() + TimeUnit.DAYS.toMillis(duration);
                String timeUntilUnban = Bundle.formatDuration(Duration.ofDays(duration));
                target.con.kick("[red]You have been banned!\n\n" + "[white]Reason: " + text +"\nDuration: " + timeUntilUnban + " until unban\nIf you think this is a mistake, make sure to appeal ban in our discord: " + discordurl, 0);
                Call.sendMessage(target.plainName() + " has been banned for: " + text);
                Bson updates = Updates.combine(
                        Updates.set("lastBan", banTime)
                );
                Ploogin.playerCollection.updateOne(user, updates, new UpdateOptions().upsert(true));
                Bot.banchannel.sendMessage(banEmbed(user,text,banTime, view.player.plainName()));
            });
        });
    }
}