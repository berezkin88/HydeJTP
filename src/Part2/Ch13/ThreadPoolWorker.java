package Part2.Ch13;

import Part2.Ch18.ObjectFIFO;

public class ThreadPoolWorker {
    private static int nextWorkerID = 0;

    private ObjectFIFO idleWorkers;
    private int workerID;
    private ObjectFIFO handoffBox;

    private Thread internalThread;
    private volatile boolean noStopRequested;


    public ThreadPoolWorker(ObjectFIFO idleWorkers) {
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
        internalThread.start();
    }

    public static synchronized int getNextWorkerID() {
//        notice: sync'd at the class level to ensure uniqueness
        int id = nextWorkerID;
        nextWorkerID++;
        return id;
    }

    public void process(Runnable target) throws InterruptedException{
        handoffBox.add(target);
    }

    private void runWork() {
        while (noStopRequested) {
            try {
                System.out.println("workerID=" + workerID + ", ready for work");
//            worker is ready work. This will never block because the idleWorker FIFO queue has
//            enough capacity for all workers
                idleWorkers.add(this);

//            wait here until the server adds a request
                Runnable r = (Runnable) handoffBox.remove();

                System.out.println("workerID=" + workerID + ", starting execution of new Runnable: " + r);
                runIt(r); // catch all exceptions
            } catch (InterruptedException x) {
                Thread.currentThread().interrupt(); // re-assert
            }
        }
    }

    private void runIt(Runnable r) {
        try {
            r.run();
        } catch (Exception runex) {
//            catch any and all exceptions
            System.err.println("Uncaught exception fell through from run()");
            runex.printStackTrace();
        } finally {
//            clear the interrupted flag (in case it comes back set) so that if the loop goes
//            again, the handoffBox.remove() does not mistakenly throw an InterruptedException
            Thread.interrupted();
        }
    }

    public void stopRequest() {
        System.out.println("workerID=" + workerID + ", stopRequest() received");
        noStopRequested = false;
        internalThread.interrupt();
    }

    public boolean isAlive() {
        return internalThread.isAlive();
    }
}
