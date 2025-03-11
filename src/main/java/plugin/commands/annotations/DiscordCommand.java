package plugin.commands.annotations;

import plugin.etc.Ranks;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)

@Target(ElementType.METHOD)
public @interface DiscordCommand {
    String name();

    String args() default "";

    String description();

    Ranks.Rank requiredRank() default Ranks.Rank.None;

    int minArgsCount() default 0;

    int maxArgsCount() default 0;

    boolean isLastArgText() default false;
}
