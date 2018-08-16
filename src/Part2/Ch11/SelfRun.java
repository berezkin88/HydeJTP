package Part2.Ch11;

public class SelfRun implements Runnable {
    private Thread internalThread;
    private volatile boolean noStopRequested;

    public SelfRun() {
//        other constructor stuff should appear here first...
        System.out.println("in constructor - initializing...");

//        just before returning, the thread should be created and started
        noStopRequested = true;
        internalThread = new Thread(this);
        internalThread.start();
    }

    @Override
    public void run() {
//        check if no one has erroneously invoked this public method
        if (Thread.currentThread() != internalThread) {
            throw new RuntimeException("only the internal thread is allowed to invoke run()");
        }

        while (noStopRequested) {
            System.out.println("in run() - still going...");

            try {
                Thread.sleep(700);
            } catch (InterruptedException x) {
//                any caught interrupts should be habitually reasserted for any blocking statements
//                which follow
                Thread.currentThread().interrupt();
            }
        }
    }

    public void stopRequest() {
        noStopRequested = false;
        internalThread.interrupt();
    }

    public boolean isAlive() {
        return internalThread.isAlive();
    }
}
