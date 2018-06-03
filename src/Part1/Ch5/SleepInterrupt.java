package Part1.Ch5;

public class SleepInterrupt extends Object implements Runnable{

    @Override
    public void run() {
        try {
            System.out.println("in run() - about to sleep for 20 seconds");
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            System.out.println("in run() - interrupted while sleeping");
            return;
        }

        System.out.println("in run() - doing stuff after nap");
        System.out.println("in run() - leaving normally");
    }

    public static void main(String[] args) {
        SleepInterrupt si = new SleepInterrupt();
        Thread t = new Thread(si);
        t.start();

//        be sure that the new thread gets a chance to run for a while
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
//            ignore
        }

        System.out.println("in main() - interrupting other thread");
        t.interrupt();
        System.out.println("in main() - leaving");
    }
}
