package Part2.Ch13;

import Part2.Ch18.ObjectFIFO;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {

//    currently available HttpWorker objects
    private ObjectFIFO idleWorkers;

//    all HttpWorker objects
    private HttpWorker[] workerList;
    private ServerSocket ss;

    private Thread internalThread;
    private volatile boolean noStopRequested;

    public HttpServer(File docRoot, int port, int numberOfWorkers, int maxPriority) throws IOException {

//        allow a max of 10 sockets to queue up waiting for accpet()
        ss = new ServerSocket(port, 10);

        if ((docRoot == null) || !docRoot.exists() || !docRoot.isDirectory()) {
            throw new IOException("specified docRoot is null or does not exist or not a directory");
        }

//        ensure that at least one worker is created
        numberOfWorkers = Math.max(1, numberOfWorkers);

//        ensure: (minAllow +2) <= serverPriority <= (maxAllow - 1)
//        which is generally: 3 <= serverPriority <= 9
        int serverPriority = Math.max(Thread.MIN_PRIORITY + 2, Math.min(maxPriority, Thread.MAX_PRIORITY - 1));

//        have the workers run at a slightly lower priority so that new requests are handled with more urgency
//        than in-progress requests
        int workerPriority = serverPriority - 1;

        idleWorkers = new ObjectFIFO(numberOfWorkers);
        workerList = new HttpWorker[numberOfWorkers];

        for (int i = 0; i < numberOfWorkers; i++) {
//            workers get a reference to the FIFO to add themselves back in when they are ready to handle
//            a new request
            workerList[i] = new HttpWorker(docRoot, workerPriority, idleWorkers);
        }

//        just before returning, the thread should be created and started
        noStopRequested =true;

        Runnable r = () -> {
            try {
                runWork();
            } catch (Exception e) {
//                in case any exception slips through
                e.printStackTrace();
            }
        };

        internalThread = new Thread(r);
        internalThread.setPriority(serverPriority);
        internalThread.start();
    }

    private void runWork() {
        System.out.println("HttpServer ready to receive requests");

        while (noStopRequested) {
            try {
                Socket s = ss.accept();

                if (idleWorkers.isEmpty()) {
                    System.out.println("HttpServer too busy, denying request");

                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));

                    writer.write("HTTP/1.0 503 Service Unavailable\r\n\r\n");

                    writer.flush();
                    writer.close();
                    writer = null;
                } else {
//                    no need to be worried that idleWorkers will suddenly be empty since
//                    this is the only thread removing items from the queue
                    HttpWorker worker = (HttpWorker) idleWorkers.remove();

                    worker.processRequest(s);
                }
            } catch (IOException iox) {
                if (noStopRequested) {
                    iox.printStackTrace();
                }
            } catch (InterruptedException x) {
//                re-assert interrupt
                Thread.currentThread().interrupt();
            }
        }
    }

    public void stopRequest() {
        noStopRequested = false;
        internalThread.interrupt();

        for (int i = 0; i < workerList.length; i++) {
            workerList[i].stopRequest();
        }

        if (ss != null) {
            try {
                ss.close();
            } catch (IOException iox) { /**/ }
            ss = null;
        }
    }

    public boolean isAlive() {
        return internalThread.isAlive();
    }

    private static void usageAndExit(String msg, int exitCode) {
        System.err.println(msg);
        System.err.println("Usage: java HttpServer <port> <numWorkers> <documentRoot>");
        System.err.println("<port> - to to listen on for HTTP requests");
        System.err.println("<numWorkers> - number of worker threads to create");
        System.err.println("<document Root> - base directory for HTML files");
        System.exit(exitCode);
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            usageAndExit("wrong number of arguments", 1);
        }

        String portStr = args[0];
        String numWorkersStr = args[1];
        String docRootStr = args[2];

        int port = 0;

        try {
            port = Integer.parseInt(portStr);
        } catch (NumberFormatException x) {
            usageAndExit("could not parse port number from '" + portStr + "'", 2);
        }

        if (port < 1) {
            usageAndExit("invalid port number specified: " + port, 3);
        }

        int numWorkers = 0;

        try {
            numWorkers = Integer.parseInt(numWorkersStr);
        } catch (NumberFormatException x) {
            usageAndExit("could not parse port number from '" + numWorkersStr + "'", 4);
        }

        File docRoot = new File(docRootStr);

        try {
            new HttpServer(docRoot, port, numWorkers, 6);
        } catch (IOException x) {
            x.printStackTrace();
            usageAndExit("could not construct HttpServer", 5);
        }
    }
}
