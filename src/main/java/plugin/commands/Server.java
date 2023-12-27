package plugin.commands;

public class Server {
    public final String name;
    public final String ip;
    public final long port;

    public Server(String name, String ip, long port) {
        this.name = name;
        this.ip = ip;
        this.port = port;
    }
}
