package Part1.Ch5;

public class DaemonThread extends Object implements Runnable {

    @Override
    public void run() {
        System.out.println("entering run()");

        try {
            System.out.println("in run() - currentThread()=" + Thread.currentThread());

            while (true) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie) {
                    //ignore
                }

                System.out.println("in run() - woke up again");
            }
        } finally {
            System.out.println("leaving run()");
        }
    }
}
