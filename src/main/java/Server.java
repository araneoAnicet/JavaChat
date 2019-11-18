import org.json.JSONObject;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;


class Server implements Observed {
    private int port;
    private InetAddress activeIP = null;
    private DatagramSocket serverSocket = null;
    private ArrayList<Observer> senders = new ArrayList<>();

    Server() throws SocketException {
        Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
        System.out.println("Available ip addresses:");
        for (NetworkInterface netInterface: Collections.list(netInterfaces)) {

            if (netInterface.getName().equals("lo") || !netInterface.isUp()) {
                continue;
            }

            Enumeration<InetAddress> inetAddresses = netInterface.getInetAddresses();
            for (InetAddress inetAddress: Collections.list(inetAddresses)) {
                if (inetAddress instanceof Inet4Address) {
                    this.activeIP = inetAddress;
                }
            }

        }

        if (this.activeIP != null) {
            System.out.println("Found active IP: " + this.activeIP.getHostName());
        } else {
            System.out.println("No active IPs found on this machine");
        }

        this.addObserver(new UpperCaseSender(this.activeIP, 7070));
    }

    void openPort(int port) {
        this.port = port;
        try {
            this.serverSocket = new DatagramSocket(null);
            System.out.println("Created UDP socket");
        } catch (SocketException e) {
            System.out.println("Error while creating socket");
        }

        try {
            this.serverSocket.bind(new InetSocketAddress(this.activeIP, this.port));
            System.out.println("Binded UDP socket to the activ machine IP: " + this.activeIP.getHostName() + ":" + this.port);
        } catch (SocketException e) {
            System.out.println("Error while binding socket");
        }

    }

    void run() {

            byte[] buffer = new byte[1024];
            DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
            System.out.println("Server runs and waits for incomming requests...");
            while (true) {
                try {
                    this.serverSocket.receive(datagramPacket);
                    this.notifyObservers(new JSONObject(new String(buffer, 0, datagramPacket.getLength())));
                } catch (Exception e) {
                    this.serverSocket.close();
                    System.out.println("Closing socket...");
                    break;
                }
            }

            System.out.println("Error broke the loop");

    }


    public void addObserver(Observer observer) {
        this.senders.add(observer);
    }


    public void notifyObservers(JSONObject message) {
        for (Observer observer: this.senders) {
            observer.notify(message);
        }
    }
}
