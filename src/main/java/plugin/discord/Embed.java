package plugin.discord;

import org.bson.Document;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import plugin.models.PlayerData;

import java.awt.*;
import java.util.Optional;

import static arc.util.Strings.stripColors;

public class Embed {

    public static EmbedBuilder banEmbed(PlayerData data, String reason, long banTime, String moderator){
        return new EmbedBuilder()
            .setTitle("Ban event")
            .setColor(Color.RED)
            .addField("**ID**", String.valueOf(data.id))
            .addField("**Name**", data.name)
            .addField("**UUID**", data.uuid)
            .addField("**IP**", data.ip)
            .addField("**Reason**", reason)
            .addField("**Expires**", "<t:" + banTime/1000 +":D>")
            .addField("**Moderator**", moderator);
            
}
    public static EmbedBuilder noRoleEmbed(Optional<Role> role){
        return new EmbedBuilder()
                .setTitle("Not enough permissions!")
                .setColor(Color.RED)
                .setDescription("You should have <@&" + role.get().getId() + "> Role to interact with this command/button!");
    }
}
