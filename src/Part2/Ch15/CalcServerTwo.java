package Part2.Ch15;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;

public class CalcServerTwo {
    private ServerSocket ss;
    private LinkedList workerList;

    private Thread internalThread;
    private volatile boolean noStopRequested;

    public CalcServerTwo (int port) throws IOException {
        ss = new ServerSocket(port);
        workerList = new LinkedList();

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
        System.out.println("in CalcServerTwo - ready to accept connections");

        while (noStopRequested) {
            try {
                System.out.println("in CalcServerTwo - about to block waiting for a new connection");
                Socket sock = ss.accept();
                System.out.println("in CalcServerTwo - received new connection");
                workerList.add(new CalcWorkerTwo(sock));
            } catch (IOException x) {
                if (noStopRequested) {
                    x.printStackTrace();
                }
            }
        }

//        stop all the workers that were created
        System.out.println("in CalcServerTwo - putting in a stop request to all the workers");
        Iterator iter = workerList.iterator();
        while (iter.hasNext()) {
            CalcWorkerTwo worker = (CalcWorkerTwo) iter.next();
            worker.stopRequest();
        }

        System.out.println("in CalcServerTwo - leaving runWork()");
    }

    public void stopRequest() {
        System.out.println("in CalcServerTwo - entering stopRequest()");
        noStopRequested = false;
        internalThread.interrupt();

        if (ss != null) {
            try {
                ss.close();
            } catch (IOException x) { /**/ }
            finally {
                ss = null;
            }
        }
    }

    public boolean isAlive() {
        return internalThread.isAlive();
    }

    public static void main(String[] args) {
        int port = 2001;

        try {
            CalcServerTwo server = new CalcServerTwo(port);
            Thread.sleep(15000);
            server.stopRequest();
        } catch (IOException | InterruptedException x) {
            x.printStackTrace();
        }
    }
}
