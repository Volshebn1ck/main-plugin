package plugin.etc;

public class Ranks{
    public static String playerRank = "[white]Player";
    public static String trustedRank = "[blue]Trusted";
    public static String adminRank = "[red]Administrator";
    public static String consoleRank = "[purple]Console";
    public static String ownerRank = "[cyan]Owner";
    public static String rankName(int rankid){
        switch (rankid){
            case 0 -> {
                return playerRank;
            }
            case 1 -> {
                return trustedRank;
            }
            case 2 -> {
                return adminRank;
            }
            case 3 -> {
                return consoleRank;
            }
            case 4 -> {
                return ownerRank;
            }
        }
        return playerRank;
    }
}
