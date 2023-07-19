package plugin.discord;

import org.bson.Document;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;

import static arc.util.Strings.stripColors;

public class Embed {

    public static EmbedBuilder banEmbed(Document user, String reason, long banTime, String moderator){
        return new EmbedBuilder()
            .setTitle("Ban event")
            .setColor(Color.RED)
            .addField("**ID**", String.valueOf(user.getInteger("id")))
            .addField("**Name**", stripColors(user.getString("name")))
            .addField("**Reason**", reason)
            .addField("**Expires**", "<t:" + banTime/1000 +":D>")
            .addField("**Moderator**", moderator);
}}
