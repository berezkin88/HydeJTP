package Part1.Ch8;

public class ThreadIDMain implements Runnable {
    private ThreadID var;

    public ThreadIDMain(ThreadID var) {
        this.var = var;
    }

    @Override
    public void run() {
        try {
            print("var.getThreadID()=" + var.getThreadID());
            Thread.sleep(2000);
            print("var.getThreadID()=" + var.getThreadID());
        } catch (InterruptedException x) { /**/ }
    }

    private static void print(String s) {
        String threadName = Thread.currentThread().getName();
        System.out.println(threadName + ": " + s);
    }

    public static void main(String[] args) {
        ThreadID tid = new ThreadID();
        ThreadIDMain shared = new ThreadIDMain(tid);

        try {
            Thread threadA = new Thread(shared, "threadA");
            threadA.start();

            Thread.sleep(500);

            Thread threadB = new Thread(shared, "threadB");
            threadB.start();

            Thread.sleep(500);

            Thread threadC = new Thread(shared, "threadC");
            threadC.start();
        } catch (InterruptedException x) { /**/ }
    }
}
