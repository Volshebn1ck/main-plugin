package plugin.etc;

public class Ranks {
    public enum Rank {
        None("none", ""),
        Player("[white]Player", ""),
        Verified("[blue]Verified", "[blue]<V>"),
        Moderator("[blue]Moderator", "[blue]<M>"),
        JS("[purple]JS", "[purple]<JS>"),
        Administrator("[#00bfff]Administrator", "[#00bfff]<A>");
        private final String name, prefix;

        Rank(String name, String prefix) {
            this.name = name;
            this.prefix = prefix;
        }

        public String getName() {
            return name;
        }
        public String getPrefix(){
            return prefix;
        }
    }

    ;

    public static Rank getRank(String name) {
        switch (name.toLowerCase()) {
            case "player" -> {
                return Rank.Player;
            }
            case "verified" -> {
                return Rank.Verified;
            }
            case "moderator" -> {
                return Rank.Moderator;
            }
            case "js" -> {
                return Rank.JS;
            }
            case "administrator" -> {
                return Rank.Administrator;
            }
            default -> {
                return Rank.None;
            }
        }
    }
}
