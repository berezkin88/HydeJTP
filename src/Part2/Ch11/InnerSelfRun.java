package Part2.Ch11;

public class InnerSelfRun {
    private Thread internalThread;
    private volatile boolean noStopRequested;

    public InnerSelfRun() {
//        other constructor stuff should appear here first...
        System.out.println("in constructor - initializing...");

//        just before returning, the thread should be created and started
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
            System.out.println("in runWork() - still going...");

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
