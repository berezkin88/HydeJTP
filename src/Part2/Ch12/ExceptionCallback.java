package Part2.Ch12;

import java.beans.ExceptionListener;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ExceptionCallback {
    private Set exceptionListeners;
    private Thread internalThread;
    private volatile boolean noStopRequested;

    public ExceptionCallback(ExceptionListener[] initialGroup) {
        init(initialGroup);
    }

    public ExceptionCallback(ExceptionListener initialListener) {
        ExceptionListener[] group = new ExceptionListener[1];
        group[0] = initialListener;
        init(group);
    }

    public ExceptionCallback() {
        init(null);
    }

    private void init(ExceptionListener[] initialGroup) {
        System.out.println("in constructor - initializing...");

        exceptionListeners = Collections.synchronizedSet(new HashSet());

//        if any listeners should be added before the internal thread starts, add them now
        if (initialGroup != null) {
            for (int i = 0; i < initialGroup.length; i++) {
                addExceptionListener(initialGroup[i]);
            }
        }

//        just before returning from the constructor, the thread should be created and started
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
        try {
            makeConnection(); //will throw an IOException
        } catch (IOException x) {
            sendException(x);
//            probably in a real scenario, a "return" statement should be here
        }

        String str = null;
        int len = determineLength(str); //NullPointerException
    }

    private void makeConnection() throws IOException{
//        a NumberFormatException will be thrown when this String is parsed
        String portStr = "j20";
        int port = 0;

        try {
            port = Integer.parseInt(portStr);
        } catch (NumberFormatException x) {
            sendException(x);
            port = 80; //use default
        }

        connectToPort(port); //will throw an IOException
    }

    private void connectToPort(int port) throws IOException{
        throw new IOException("connection refused");
    }

    private int determineLength(String str) {
        return str.length();
    }

    public void stopRequest() {
        noStopRequested = false;
        internalThread.interrupt();
    }

    public boolean isAlive() {
        return internalThread.isAlive();
    }

    private void sendException(Exception x) {
        if (exceptionListeners.size() == 0) {
//            if there aren't any listeners, dump the stack trace to the console
            x.printStackTrace();
            return;
        }

//        used "synchronized" to make sure that other threads do not make changes to the Set
//        while iterating
        synchronized (exceptionListeners) {
            Iterator iter = exceptionListeners.iterator();
            while (iter.hasNext()) {
                ExceptionListener l = (ExceptionListener) iter.next();

                l.exceptionThrown(x);
            }
        }
    }

    private void addExceptionListener(ExceptionListener exceptionListener) {
//        silently ignore the request to add a "null" listener
        if (exceptionListener != null) {
//            if a listener was already in the Set, it will silently replace itself so that
//            no duplicates accumulate
            exceptionListeners.add(exceptionListener);
        }
    }

    public void removeExceptionListener(ExceptionListener l) {
//        silently ignore a request to remove a listener that is not in the Set
        exceptionListeners.remove(l);
    }

    public String toString() {
        return getClass().getName() + "[isAlive()=" + isAlive() + "]";
    }
}