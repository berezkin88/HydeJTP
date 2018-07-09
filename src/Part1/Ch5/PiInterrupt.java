package Part1.Ch5;

public class PiInterrupt extends Object implements Runnable {

    private double latestPiEstimate;

    @Override
    public void run() {
        try {
            System.out.println("for comparison, Math.PI=" + Math.PI);
            calcPi(0.000000001);
            System.out.println("Within accuracy, latest pi=" + latestPiEstimate);
        } catch (InterruptedException ie) {
            System.out.println("INTERRUPTED!! latest pi=" + latestPiEstimate);
        }
    }

    private void calcPi(double v) throws InterruptedException {

        latestPiEstimate = 0.0;
        long iteration = 0;
        int sign = -1;

        while (Math.abs(latestPiEstimate - Math.PI) > v) {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }

            iteration++;

            sign = -sign;
            latestPiEstimate += sign * 4.0 / ((2 * iteration) - 1);
        }
    }

    public static void main(String[] args) {
        PiInterrupt pi = new PiInterrupt();
        Thread t = new Thread(pi);
        t.start();

        try {
            Thread.sleep(10000);
            t.interrupt();
        } catch (InterruptedException ie) {
            //ignore
        }
    }
}
