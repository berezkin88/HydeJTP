package Part1.Ch5;

public class DepricatedSuspendResume extends Object implements Runnable {

    private volatile int firstVal;
    private volatile int secondVal;

    public boolean areValuesEqual() {
        return ( firstVal == secondVal );
    }

    @Override
    public void run() {
        try {
            firstVal = 0;
            secondVal = 0;
            workMethod();
        } catch ( InterruptedException ie ) {
            System.out.println("interrupted while in workMethod()");
        }
    }

    private void workMethod() throws InterruptedException{
        int val = 1;

        while (true) {
            stepOne(val);
            stepTwo(val);
            val++;

            Thread.sleep(200); //Pause before looping again
        }
    }

    private void stepTwo(int val) {

        secondVal = val;
    }

    private void stepOne(int val) throws InterruptedException{

        firstVal = val;
        Thread.sleep(300); //simulate some other long process
    }

    public static void main(String[] args) {
        DepricatedSuspendResume dsr = new DepricatedSuspendResume();
        Thread t = new Thread(dsr);
        t.start();

//        let the other thread get going and run for a while
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ie) {
            //ignore
        }

        for (int i = 0; i < 10; i++) {
            t.suspend();
            System.out.println("dsr.areValuesEquals = " + dsr.areValuesEqual());

            t.resume();
            try {
//                pause for a random amount of time
//                between 0 and 2 seconds
                Thread.sleep(( long ) (Math.random() * 2000.0));
            } catch (InterruptedException e) {
                // ignore
            }
        }

        System.exit(0);
    }
}
