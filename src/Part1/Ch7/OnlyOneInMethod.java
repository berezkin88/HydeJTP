package Part1.Ch7;

public class OnlyOneInMethod extends Object {
    private String objID;

    public OnlyOneInMethod(String objID) {
        this.objID = objID;
    }

    public synchronized void doStuff(int val) {
        print("entering doStuff()");
        int num = val * 2 + objID.length();
        print("in doStuff() - local variable num=" + num);

//        slow things down to make observations
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ie) { /**/ }

        print("leaving doStuff()");
    }

    public void print(String msg) {
        threadPrint("objID=" + objID + " - " + msg);
    }

    public static void threadPrint(String s) {
        String threadName = Thread.currentThread().getName();
        System.out.println(threadName + ": " + s);
    }

    public static void main(String[] args) {
        final OnlyOneInMethod ooim = new OnlyOneInMethod("obj1");

        Runnable runA = () -> {
            ooim.doStuff(3);
        };

        Thread threadA = new Thread(runA, "threadA");
        threadA.start();

        try {
            Thread.sleep(200);
        } catch (InterruptedException ie) { /**/ }

        Runnable runB = () -> {
            ooim.doStuff(7);
        };

        Thread threadB = new Thread(runB, "threadB");
        threadB.start();
    }
}
