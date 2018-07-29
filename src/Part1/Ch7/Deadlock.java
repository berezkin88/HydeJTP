package Part1.Ch7;

public class Deadlock {
    private String objID;

    public Deadlock(String objID) {
        this.objID = objID;
    }

    public synchronized void checkOther (Deadlock other) {
        print("entering checkOther()");

//        simulate some lengthy process
        try {
            Thread.sleep(2000);
        } catch (InterruptedException x) { /**/ }

        print("in checkOther() - about to invoke 'other.action()'");
        other.action();

        print("leaving checkOther()");
    }

    private synchronized void action() {
        print("entering action()");

//        simulate some work here
        try {
            Thread.sleep(500);
        } catch (InterruptedException x) { /**/ }

        print("leaving action()");
    }

    public void print(String msg) {
        threadPrint("objID=" + objID + " - " + msg);
    }

    public static void threadPrint(String s) {
        String threadName = Thread.currentThread().getName();
        System.out.println(threadName + ": " + s);
    }

    public static void main(String[] args) {
        final Deadlock obj1 = new Deadlock("obj1");
        final Deadlock obj2 = new Deadlock("obj2");

        Runnable runA = () -> {
            obj1.checkOther(obj2);
        };

        Thread threadA = new Thread(runA, "threadA");
        threadA.start();

        try {
            Thread.sleep(200);
        } catch (InterruptedException x) { /**/ }

        Runnable runB = () -> {
            obj2.checkOther(obj1);
        };

        Thread threadB = new Thread(runB, "threadB");
        threadB.start();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException x) { /**/ }

        threadPrint("finished sleeping");

        threadPrint("about to interrupt() threadA");
        threadA.interrupt();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException x) { /**/ }

        threadPrint("about to interrupt() threadB");
        threadB.interrupt();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException x) { /**/ }

        threadPrint("did that break the deadlock?");
    }
}
