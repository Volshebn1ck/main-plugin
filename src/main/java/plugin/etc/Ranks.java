package plugin.etc;

public class Ranks{
    public static String playerRank = "[white]Player";
    public static String trustedRank = "[blue]Trusted";
    public static String adminRank = "[red]Administrator";
    public static String consoleRank = "[purple]Console";
    public static String ownerRank = "[cyan]Owner";
    public static String[] ranks = new String[]{"player", "trusted", "admin", "console", "owner"};
    public static String rankName(String rankid){
        switch (rankid){
            case "player" -> {
                return playerRank;
            }
            case "trusted" -> {
                return trustedRank;
            }
            case "admin" -> {
                return adminRank;
            }
            case "console" -> {
                return consoleRank;
            }
            case "owner" -> {
                return ownerRank;
            }
        }
        return playerRank;
    }
}
