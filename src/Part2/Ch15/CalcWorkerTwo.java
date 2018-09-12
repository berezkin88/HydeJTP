package Part2.Ch15;

import java.io.*;
import java.net.Socket;

public class CalcWorkerTwo {
    private DataInputStream dataIn;
    private DataOutputStream dataOut;

    private Thread internalThread;
    private volatile boolean noStopRequested;

    public CalcWorkerTwo(Socket sock) throws IOException {
        dataIn = new DataInputStream(new BufferedThreadedInputStream(sock.getInputStream()));
        dataOut = new DataOutputStream(new BufferedOutputStream(sock.getOutputStream()));

        noStopRequested = true;
        Runnable r = () -> {
            try {
                runWork();
            } catch (Exception x) {
//                in case any exception slips through
                x.printStackTrace();
            }
        };

        internalThread = new Thread(r);
        internalThread.start();
    }

    private void runWork() {
        while (noStopRequested) {
            try {
                System.out.println("in CalcWorkerTwo - about to block waiting to read a double");
                double val = dataIn.readDouble();
                System.out.println("in CalcWorkerTwo - read a double!");
                dataOut.writeDouble(Math.sqrt(val));
                dataOut.flush();
            } catch (InterruptedIOException iiox) {
                System.out.println("in CalcWorkerTwo - blocked read was interrupted");
            } catch (IOException x) {
                if (noStopRequested) {
                    x.printStackTrace();
                    stopRequest();
                }
            }
        }

//        in real-world code, be sure to close other streams and the socket as part of the clean-up.
//        Omitted here for brevity

        System.out.println("in CalcWorkerTwo - leaving runWork()");
    }

    public void stopRequest() {
        System.out.println("in CalcWorkerTwo - entering stopRequest()");
        noStopRequested = false;
        internalThread.interrupt();

        System.out.println("in CalcWorkerTwo - leaving stopRequest()");
    }

    public boolean isAlive() {
        return internalThread.isAlive();
    }
}
