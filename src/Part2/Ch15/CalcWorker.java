package Part2.Ch15;

import java.io.*;
import java.net.Socket;

public class CalcWorker {
    private InputStream sockIn;
    private OutputStream sockOut;
    private DataInputStream dataIn;
    private DataOutputStream dataOut;

    private Thread internalThread;
    private volatile boolean noStopRequested;

    public CalcWorker(Socket sock) throws IOException {
        sockIn = sock.getInputStream();
        sockOut = sock.getOutputStream();

        dataIn = new DataInputStream(new BufferedInputStream(sockIn));
        dataOut = new DataOutputStream(new BufferedOutputStream(sockOut));

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
                System.out.println("in CalcWorker - about to block waiting to read a double");
                double val = dataIn.readDouble();
                System.out.println("in CalcWorker - read a double!");
                dataOut.writeDouble(Math.sqrt(val));
                dataOut.flush();
            } catch (IOException x) {
                if (noStopRequested) {
                    x.printStackTrace();
                    stopRequest();
                }
            }
        }

//        in real-world code, be sure to close other streams and the socket as part of the clean-up.
//        Omitted here for brevity

        System.out.println("in CalcWorker - leaving runWork()");
    }

    public void stopRequest() {
        System.out.println("in CalcWorker - entering stopRequest()");
        noStopRequested = false;
        internalThread.interrupt();

        if (sockIn != null) {
            try {
                sockIn.close();
            } catch (IOException x) {
                /**/
            } finally {
                sockIn = null;
            }
        }

        System.out.println("in CalcWorker - leaving stopRequest()");
    }

    public boolean isAlive() {
        return internalThread.isAlive();
    }
}
