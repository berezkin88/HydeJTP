package Part2.Ch15;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class CalcClient {
    public static void main(String[] args) {
        String hostname = "localhost";
        int port = 2001;

        try {
            Socket sock = new Socket(hostname, port);

            DataInputStream in = new DataInputStream(new BufferedInputStream(sock.getInputStream()));
            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(sock.getOutputStream()));

            double val = 4.0;
            out.writeDouble(val);
            out.flush();

            double sqrt = in.readDouble();
            System.out.println("sent up " + val + ", got back " +sqrt);

//            don't ever send another request, but stay alive in this eternally blocked state
            Object lock = new Object();
            while (true) {
                synchronized (lock) {
                    lock.wait();
                }
            }
        } catch (Exception x) {
            x.printStackTrace();
        }
    }
}
