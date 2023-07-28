package plugin.discord;

import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;

import java.util.List;
import java.util.Optional;

import static plugin.ConfigJson.adminid;
import static plugin.ConfigJson.moderatorid;
import static plugin.discord.Embed.noRoleEmbed;

public class DiscordFunctions {
    public static boolean isModerator(SlashCommandCreateEvent listener){
        User user =  listener.getInteraction().getUser();
        org.javacord.api.entity.server.Server server = listener.getInteraction().getServer().get();
        Optional<Role> moderatorRole = server.getRoleById(moderatorid);
        List<Role> roles = user.getRoles(server);
        if (roles.contains(moderatorRole.get())){
            return true;
        } else {
            listener.getSlashCommandInteraction().createImmediateResponder().addEmbed(noRoleEmbed(moderatorRole)).respond();
            return false;
        }
    }
    public static boolean isAdmin(SlashCommandCreateEvent listener){
        User user =  listener.getInteraction().getUser();
        org.javacord.api.entity.server.Server server = listener.getInteraction().getServer().get();
        Optional<Role> adminRole = server.getRoleById(adminid);
        List<Role> roles = user.getRoles(server);
        if (roles.contains(adminRole.get())){
            return true;
        } else {
            listener.getSlashCommandInteraction().createImmediateResponder().addEmbed(noRoleEmbed(adminRole)).respond();
            return false;
        }
    }
}
