package Part1.Ch5;

public class DepricatedStop extends Object implements Runnable {

    @Override
    public void run() {
        int count = 0;

        while (true) {
            System.out.println("Running ... count=" + count);
            count++;

            try {
                Thread.sleep(300);
            } catch (InterruptedException ie) {
                //ignore
            }
        }
    }

    public static void main(String[] args) {
        DepricatedStop ds = new DepricatedStop();
        Thread t = new Thread(ds);
        t.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException ie) {
            //ignore
        }

//        Abruptly stop the other thread in its tracks!
        t.stop();
    }
}
