package Part2.Ch17;

public class SyncBlock {
    private Object longLock;

    public SyncBlock() {
        longLock = new Object();
    }

    public void doStuff() {
        print("about to try to get exclusive access to longLock");

        synchronized (longLock) {
            print("got exclusive access to longLock");
            try {
                Thread.sleep(10000);
            } catch (InterruptedException x) { /**/ }
            print("about to relinquish exclusive access to longLock");
        }
    }

    private static void print(String s) {
        String name = Thread.currentThread().getName();
        System.err.println(name + ": " + s);
    }

    private static Thread launch(final SyncBlock sb, String name) {

        Runnable r = () -> {
            print("in run()");
            sb.doStuff();
        };

        Thread t = new Thread(r, name);
        t.start();

        return t;
    }

    public static void main(String[] args) {
        try {
            SyncBlock sb = new SyncBlock();

            Thread t1 = launch(sb, "T1");
            Thread.sleep(500);

            Thread t2 = launch(sb, "T2");
            Thread t3 = launch(sb, "T3");

            Thread.sleep(1000);

            print("about to interrupt T2");
            t2.interrupt();
            print("just interrupted T2");
        } catch (InterruptedException x) {
            x.printStackTrace();
        }
    }
}
