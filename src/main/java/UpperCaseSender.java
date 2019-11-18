import org.json.JSONObject;
import java.io.IOException;
import java.net.*;

public class UpperCaseSender implements Observer {
    private DatagramSocket senderSocket = new DatagramSocket(null);
    private InetAddress activeIp;

    UpperCaseSender(InetAddress ipAddress, int port) throws SocketException {
        this.activeIp = ipAddress;
        this.senderSocket.bind(new InetSocketAddress(ipAddress, port));
        System.out.println("UpperCaseSender sender has been added with port: " + port);
    }

    @Override
    public void notify(JSONObject message) {
    if (message.getString("destination").equals("makeUpper")) {
        try {
            InetAddress sendToAddress = InetAddress.getByName(message.getString("from"));
            String modifiedString = message.getString("toModify").toUpperCase();
            JSONObject jsonPacket = new JSONObject();
            jsonPacket.put("destination", "makeUpperResponse");
            jsonPacket.put("from", this.activeIp.toString());
            jsonPacket.put("result", modifiedString);
            byte[] buffer = jsonPacket.toString().getBytes();
            DatagramPacket packetToSend = new DatagramPacket(buffer, buffer.length, sendToAddress, 9090);
            this.senderSocket.send(packetToSend);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    }
}
