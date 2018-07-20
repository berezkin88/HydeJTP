package Part1.Ch6;

public class SetPriority extends Object {
    private static Runnable makeRunnable() {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 5; i++) {
                    Thread t = Thread.currentThread();
                    System.out.println("in run() - priority="
                    + t.getPriority() + ", name=" + t.getName());

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ie) {
                        //ignore
                    }
                }
            }
        };
        return r;
    }

    public static void main(String[] args) {
        Thread threadA = new Thread(makeRunnable(), "threadA");
        threadA.setPriority(8);
        threadA.start();

        Thread threadB = new Thread(makeRunnable(), "threadB");
        threadB.setPriority(2);
        threadB.start();

        Runnable r = new Runnable() {
            @Override
            public void run() {
                Thread threadC = new Thread(makeRunnable(), "threadC");
                threadC.start();
            }
        };

        Thread threadD = new Thread(r, "threadD");
        threadD.setPriority(7);
        threadD.start();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException ie) { }

        threadA.setPriority(3);
        System.out.println("in main() - threadA.getPriority()=" + threadA.getPriority());
    }
}
