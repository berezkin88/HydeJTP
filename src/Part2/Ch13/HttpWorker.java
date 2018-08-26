package Part2.Ch13;

import Part2.Ch18.ObjectFIFO;

import java.io.*;
import java.net.Socket;
import java.net.URLConnection;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class HttpWorker {
    private static int nextWorkerID = 0;

    private File docRoot;
    private ObjectFIFO idleWorkers;
    private int workerID;
    private ObjectFIFO handoffBox;

    private Thread internalThread;
    private volatile boolean noStopRequested;

    public HttpWorker(File docRoot, int workerPriority, ObjectFIFO idleWorkers) {
        this.docRoot = docRoot;
        this.idleWorkers = idleWorkers;

        workerID = getNextWorkerID();
        handoffBox = new ObjectFIFO(1); //only one slot

//        just before returning, the thread should be created
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
        internalThread.setPriority(workerPriority);
        internalThread.start();
    }

    public static synchronized int getNextWorkerID() {
//        notice: sync'd at the class level to ensure uniqueness
        int id = nextWorkerID;
        nextWorkerID++;
        return id;
    }

    public void processRequest(Socket s) throws InterruptedException{
        handoffBox.add(s);
    }


    private void runWork() {
        Socket s = null;
        InputStream in = null;
        OutputStream out = null;

        while (noStopRequested) {
            try {
//                worker is ready to receive new service requests, so it adds itself
//                to the idle worker queue
                idleWorkers.add(this);

//                wait here until the server puts a request into the handoff box
                s = (Socket) handoffBox.remove();

                in = s.getInputStream();
                out = s.getOutputStream();
                generateResponse(in, out);
                out.flush();
            } catch (IOException x) {
                System.err.println("I/O error while processing request, ignoring and adding" +
                        "back to idle queue - workerID=" + workerID);
            } catch (InterruptedException x) {
                Thread.currentThread().interrupt(); // re-assert
            } finally {
//                try to close everything, ignoring any IOExceptions that might occur
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException x) { /**/ }
                    finally {
                        in = null;
                    }
                }

                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException x) { /**/ }
                    finally {
                        out = null;
                    }
                }

                if (s != null) {
                    try {
                        s.close();
                    } catch (IOException x) { /**/ }
                    finally {
                        s = null;
                    }
                }
            }
        }
    }

    private void generateResponse(InputStream in, OutputStream out) throws IOException{
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        String requestLine = reader.readLine();

        if ((requestLine == null) || (requestLine.length() < 1)) {
            throw new IOException("could not read request");
        }

        System.out.println("workerID=" + workerID + ", requestLine=" + requestLine);

        StringTokenizer st = new StringTokenizer(requestLine);
        String fileName = null;

        try {
//            request method, typically 'GET', but ignored
            st.nextToken();

//            the second token should be the filename
            fileName = st.nextToken();
        } catch (NoSuchElementException x) {
            throw new IOException("could not parse request line");
        }

        File requestedFile = generateFile(fileName);

        BufferedOutputStream buffOut = new BufferedOutputStream(out);

        if (requestedFile.exists()) {
            System.out.println("workerID=" + workerID + ", 200 OK: " + fileName);

            int fileLen = (int) requestedFile.length();

            BufferedInputStream fileIn = new BufferedInputStream(new FileInputStream(requestedFile));

//            use this utility to make a guess about the content type based on the first few bytes
//            in the stream
            String contentType = URLConnection.guessContentTypeFromStream(fileIn);

            byte[] headerBytes = createHeaderBytes("HTTP/1.0 200 OK", fileLen, contentType);

            buffOut.write(headerBytes);

            byte[] buf = new byte[2048];
            int blockLen = 0;

            while ((blockLen = fileIn.read(buf)) != -1) {
                buffOut.write(buf, 0, blockLen);
            }

            fileIn.close();
        } else {
            System.out.println("workerID=" + workerID + ", 404 Not Found: " + fileName);
            byte[] headerBytes = createHeaderBytes("HTTP/1.0 404 Not Found", -1, null);
            buffOut.write(headerBytes);
        }

        buffOut.flush();
    }

    private File generateFile(String fileName) {
        File requestedFile = docRoot; //start at the base

//        build up the path to the requested file in a platform independent way.
//        URL's use '/' in their path, but this platform may not
        StringTokenizer st = new StringTokenizer(fileName, "/");
        while (st.hasMoreTokens()) {
            String tok = st.nextToken();

            if (tok.equals("..")) {
//                silently ignore parts of path that might lead out of the document root area
                continue;
            }

            requestedFile = new File(requestedFile, tok);
        }

        if (requestedFile.exists() && requestedFile.isDirectory()) {
//            if a directory was requested, modify the request to look for the "index.html"
//            file in that directory
            requestedFile = new File(requestedFile, "index.html");
        }

        return requestedFile;
    }

    private byte[] createHeaderBytes(String s, int fileLen, String contentType) throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(baos));

//        write the first line of the response, followed by the RFC-specified line termination sequence
        writer.write(s + "\r\n");

//        if a length was specified, add it to the header
        if (fileLen != -1) {
            writer.write("Content-Length: " + fileLen + "\r\n");
        }

//        if a type was specified, add it to the header
        if (contentType != null) {
            writer.write("Content-Type: " + contentType + "\r\n");
        }

//        a blank line is required after the header
        writer.write("\r\n");
        writer.flush();
        byte[] data = baos.toByteArray();
        writer.close();

        return data;
    }

    public void stopRequest() {
        noStopRequested = false;
        internalThread.interrupt();
    }

    public boolean isAlive() {
        return internalThread.isAlive();
    }
}
