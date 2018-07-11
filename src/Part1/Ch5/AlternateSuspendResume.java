package Part1.Ch5;

public class AlternateSuspendResume extends Object implements Runnable {

    private volatile int firstVal;
    private volatile int secondVal;
    private volatile boolean suspended;

    public boolean areValuesEqual() {
        return ( firstVal == secondVal );
    }

    @Override
    public void run() {
        try {
            suspended = false;
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
//            block only if suspended is true
            waitWhileSuspended();

            stepOne(val);
            stepTwo(val);
            val++;

//            block only if suspended is true
            waitWhileSuspended();

            Thread.sleep(200); //Pause before looping again
        }
    }

    private void waitWhileSuspended() throws InterruptedException{
        while (suspended) {
            Thread.sleep(200);
        }
    }

    private void stepOne(int val) throws InterruptedException{
        firstVal = val;
        Thread.sleep(300); //simulate some other long process
    }

    private void stepTwo(int val) {
        secondVal = val;
    }

    public void suspendRequest() {
        suspended = true;
    }

    public void resumeRequest() {
        suspended = false;
    }

    public static void main(String[] args) {
        AlternateSuspendResume asr = new AlternateSuspendResume();
        Thread t = new Thread(asr);
        t.start();

        //        let the other thread get going and run for a while
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ie) {
            //ignore
        }

        for (int i = 0; i < 10; i++) {
            asr.suspendRequest();

//            give the thread a chance to notice the suspension request
            try { Thread.sleep(350); } catch (InterruptedException ie){ /*ignore*/ }

            System.out.println("asr.areValuesEquals = " + asr.areValuesEqual());

            asr.resumeRequest();
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
