package Part1.Ch6;

public class GetPrioriry extends Object {
    private static Runnable makeRunnable() {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 5; i++) {
                    Thread t = Thread.currentThread();
                    System.out.println("in run() - priority="
                    + t.getPriority() + ", name="
                    + t.getName());

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
        System.out.println("in main() - Thread.currentThread().getPriority()="
        + Thread.currentThread().getPriority());

        System.out.println("in main() - Thread.currentThread().getName()="
        + Thread.currentThread().getName());

        Thread threadA = new Thread(makeRunnable(), "threadA");
        threadA.start();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException ie) {
            //ignore
        }

        System.out.println("in main() - threadA.getPriority()="
        + threadA.getPriority());
    }
}
