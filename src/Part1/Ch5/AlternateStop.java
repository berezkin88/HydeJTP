package Part1.Ch5;

public class AlternateStop extends Object implements Runnable {

    private volatile boolean stopRequested;
    private Thread runThread;

    @Override
    public void run() {
        runThread = Thread.currentThread();
        stopRequested = false;

        int count = 0;

        while ( !stopRequested ) {
            System.out.println("Running ... count=" + count);
            count++;

            try {
                Thread.sleep(300);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void stopRequest() {
        stopRequested = true;

        if ( runThread != null) {
            runThread.interrupt();
        }
    }

    public static void main(String[] args) {
        AlternateStop as = new AlternateStop();
        Thread t = new Thread(as);
        t.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException ie) {
            //ignore
        }

        as.stopRequest();
    }
}
