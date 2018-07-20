package Part1.Ch6;

public class PriorityComplete extends Object {
    private volatile int count;
    private boolean yield;
    private Thread internalThread;
    private volatile boolean noStopRequested;

    public PriorityComplete(String name, int priority, boolean yield) {
        count = 0;
        this.yield = yield;
        noStopRequested = true;

        Runnable r = () -> {
            try {
                runWork();
            } catch (Exception e) {
//                    in case any Exception splits through
                e.printStackTrace();
            }
        };

        internalThread = new Thread(r, name);
        internalThread.setPriority(priority);
    }

    private void runWork() {
        Thread.yield();

        while (noStopRequested) {
            if (yield) {
                Thread.yield();
            }

            count++;

            for (int i = 0; i < 1000; i++) {
                double x = i * Math.PI / Math.E;
            }
        }
    }

    public void startRequest() {
        internalThread.start();
    }

    public void stopRequeest() {
        noStopRequested = false;
    }

    public int getCount() {
        return count;
    }

    public String getNameAndPriority() {
        return internalThread.getName() + ": priority=" + internalThread.getPriority();
    }

    private static void runSet(boolean yield) {
        PriorityComplete[] pc = new PriorityComplete[3];
        pc[0] = new PriorityComplete("PC0", 3, yield);
        pc[1] = new PriorityComplete("PC1", 6, yield);
        pc[2] = new PriorityComplete("PC2", 6, yield);

//        let the dust settle for a bit before starting them up
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ie) { }

        for (PriorityComplete aPc2 : pc) {
            aPc2.startRequest();
        }

        long startTime = System.currentTimeMillis();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException ie) { }

        for (PriorityComplete aPc1 : pc) {
            aPc1.stopRequeest();
        }

        long stopTime = System.currentTimeMillis();

//        let things settle down again
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ie) { }

        int totalCount = 0;
        for (PriorityComplete aPc : pc) {
            totalCount += aPc.getCount();
        }

        System.out.println("totalCount=" + totalCount + ", count/ms="
        + roundTo(((double) totalCount) / (stopTime - startTime), 3));

        for (PriorityComplete aPc : pc) {
            double perc = roundTo(100.0 * aPc.getCount() / totalCount, 2);
            System.out.println(aPc.getNameAndPriority() + ", " + perc + "%, count=" + aPc.getCount());
        }
    }

    private static double roundTo(double v, int i) {
        double factor = Math.pow(10, i);
        return ((int) ((v * factor) + 0.5)) / factor;
    }

    public static void main(String[] args) {
        Runnable r = () -> {
            System.out.println("Run without using yield()");
            System.out.println("=========================");
            runSet(false);

            System.out.println();
            System.out.println("Run using yield()");
            System.out.println("=================");
            runSet(true);
        };

        Thread t = new Thread(r, "Priority complete");
        t.setPriority(Thread.MAX_PRIORITY - 1);
        t.start();
    }
}
