import java.net.SocketException;

public class Main {
    public static void main(String[] args) throws SocketException {
        Server server = new Server();
        server.openPort(9090);
        server.run();
    }
}
