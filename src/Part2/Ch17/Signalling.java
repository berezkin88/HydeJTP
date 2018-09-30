package Part2.Ch17;

public class Signalling {
    private BooleanLock readyLock;

    public Signalling(BooleanLock readyLock) {
        this.readyLock = readyLock;

        Runnable r = () -> {
            try {
                runWork();
            } catch (Exception x) {
//                in case any exception slips through
                x.printStackTrace();
            }
        };

        Thread internalThread = new Thread(r);
        internalThread.start();
    }

    private void runWork() {
        try {
            print("about to wait for readyLock to be true");
            readyLock.waitUntilTrue(0);
            print("readyLock is now true");
        } catch (InterruptedException e) {
            print("interrupting while waiting for readyLock to become true");
        }
    }

    private static void print(String s) {
        String name = Thread.currentThread().getName();
        System.err.println(name + ": " + s);
    }

    public static void main(String[] args) {
        try {
            print("creating BooleanLock instance");
            BooleanLock ready = new BooleanLock(false);

            print("creating Signalling instance");
            new Signalling(ready);

            print("about to sleep for 3 seconds");
            Thread.sleep(3000);

            print("about to set value to true");
            ready.setValue(true);
            print("ready.isTrue()=" + ready.isTrue());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
